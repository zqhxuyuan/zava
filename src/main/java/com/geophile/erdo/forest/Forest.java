/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.forest;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.apiimpl.TreePositionTracker;
import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.consolidate.Consolidation;
import com.geophile.erdo.consolidate.ConsolidationSet;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.mergescan.FastMergeCursor;
import com.geophile.erdo.map.mergescan.MergeCursor;
import com.geophile.erdo.map.transactionalmap.TransactionalMap;
import com.geophile.erdo.transaction.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.geophile.erdo.consolidate.Consolidation.Element;

// A Forest is the Consolidation.Container of a ConsolidationSet. ConsolidationSet synchronizes
// all actions on the its ConsolidationSet.container, because the ConsolidationSet and
// the Forest's transactionOwners have to be updated atomically.

public class Forest extends TransactionManager implements Consolidation.Container
{
    // TransactionManager interface

    @Override
    public void makeUpdatesPublic(Transaction transaction)
        throws IOException, InterruptedException
    {
        TransactionUpdates updates = transaction.transactionalMap().takeUpdates();
        updates.transactionTimestamp(transaction.timestamp());
        // transactionOwners is needed to find the map that contains an update given a timestamp.
        // If a transaction does no updates, it needs no entry in transactionOwners.
        if (updates.recordCount() > 0) {
            transactionOwners.add(updates);
        }
        consolidationSet.add((Element) updates, currentTransaction().synchronousCommit());
    }

    @Override
    public TransactionalMap newTransactionalMap()
    {
        return new TransactionalMap(snapshot());
    }

    // Consolidation.Container interface

    @Override
    public Configuration configuration()
    {
        return database.configuration();
    }

    @Override
    public Factory factory()
    {
        return database.factory();
    }

    // Doesn't have to be synchronized as it doesn't operate on consolidationSet or transactionOwner.
    @Override
    public Element consolidate(List<Element> obsolete,
                               boolean inputDurable,
                               boolean outputDurable)
        throws IOException, InterruptedException
    {
        int consolidationId = consolidationCounter.getAndIncrement();
        SealedMap replacement;
        TimestampSet consolidatedTimestamps = consolidatedTimestamps(obsolete);
        long start = 0;
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "[{0}] Starting {1} consolidation of {2}",
                    new Object[]{consolidationId, consolidationType(inputDurable, outputDurable), obsolete});
            start = System.currentTimeMillis();
        }
        boolean slowmerge = Boolean.getBoolean("slowmerge");
        MergeCursor recordScan = null;
        MergeCursor keyScan = null;
        try {
            recordScan =
                slowmerge || !inputDurable // If !inputDurable, fast merge has no benefit
                ? new MergeCursor(null, true)
                : new FastMergeCursor();
            keyScan = new MergeCursor(null, true);
            List<SealedMap> obsoleteTrees = new ArrayList<>();
            for (Element element : obsolete) {
                SealedMap map = (SealedMap) element;
                recordScan.addInput(slowmerge ? map.cursor(null, false) : map.consolidationScan());
                if (keyScan != null) {
                    if (map.keysInMemory()) {
                        keyScan.addInput(map.keyScan(null, false));
                    } else {
                        keyScan.close();
                        keyScan = null;
                    }
                }
                obsoleteTrees.add(map);
            }
            long maxDeletionTimestamp = consolidatedTimestamps.maxDeletionTimestamp();
            Factory factory = database.factory();
            replacement =
                    outputDurable
                    ? factory.newPersistentMap(database, consolidatedTimestamps, obsoleteTrees)
                    : factory.newTransientMap(database, consolidatedTimestamps, obsoleteTrees);
            // Accumulate transactions from non-durable maps. Eventually the transactions will be
            // associated with a durable transaction, and Transaction.markDurable() will be invoked
            // for each of them (a few lines below).
            if (!inputDurable) {
                for (Element element : obsolete) {
                    replacement.registerTransactions(element.transactions());
                }
            }
            recordScan.start();
            MapCursor cleanupRecordScan =
                    maxDeletionTimestamp >= 0
                    ? new RemoveDeletedRecordCursor(recordScan, maxDeletionTimestamp)
                    : recordScan;
            MapCursor cleanupKeyScan = null;
            if (keyScan != null) {
                keyScan.start();
                if (maxDeletionTimestamp >= 0) {
                    cleanupKeyScan = new RemoveDeletedRecordCursor(keyScan, maxDeletionTimestamp);
                } else {
                    cleanupKeyScan = keyScan;
                }
            }
            replacement.loadForConsolidation(cleanupRecordScan, cleanupKeyScan);
        } catch (RuntimeException | Error e) {
            LOG.log(Level.SEVERE, "Caught error or exception during consolidation", e);
            throw e;
        } finally {
            if (recordScan != null) {
                recordScan.close();
            }
            if (keyScan != null) {
                keyScan.close();
            }
            TreePositionTracker.destroyRemainingTreePositions(null);
        }
        if (outputDurable) {
            // The replacement's transactions have just become durable.
            List<Transaction> transactions = replacement.transactions();
            for (Transaction transaction : transactions) {
                transaction.markDurable();
            }
        }
        if (LOG.isLoggable(Level.INFO)) {
            long stop = System.currentTimeMillis();
            LOG.log(Level.INFO,
                    "[{0}] Finished {1} consolidation, created {2} from {3} trees: {4}. {5} msec",
                    new Object[]{consolidationId,
                                 consolidationType(inputDurable, outputDurable),
                                 replacement,
                                 obsolete.size(),
                                 obsolete,
                                 stop - start});
        }
        return replacement;
    }

    @Override
    public void replaceObsolete(List<Element> obsolete, Element replacement)
    {
        assert Thread.holdsLock(this);
        for (Element element : obsolete) {
            SealedMap map = (SealedMap) element;
            // Non-durable maps with no records aren't tracked in transactionOwners
            if (map.durable() || map.recordCount() > 0) {
                transactionOwners.remove(map);
            }
        }
        if (replacement.durable() || replacement.count() > 0) {
            transactionOwners.add((SealedMap) replacement);
        }
    }

    @Override
    public void reportCrash(Throwable crash)
    {
        database.reportCrash(crash);
    }

    // Forest interface

    public synchronized ForestSnapshot snapshot()
    {
        ForestSnapshot snapshot = new ForestSnapshot(database, transactionOwners.copy(), consolidationSet.snapshot());
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "Created ForestSnapshot with population {0}, recordCount: {1}, complexity {2}",
                    new Object[]{snapshot.population(), snapshot.recordCount(), snapshot.complexity()});
        }
        return snapshot;
    }
    
    public DatabaseImpl database()
    {
        return database;
    }

    public void flush() throws IOException, InterruptedException
    {
        consolidationSet.flush();
    }

    public void close() throws InterruptedException, IOException
    {
        consolidationSet.shutdown();
    }

    public void consolidateAll() throws IOException, InterruptedException
    {
        consolidationSet.consolidateAll();
    }

    public static Forest create(DatabaseImpl database) throws IOException, InterruptedException
    {
        return new Forest(database, new TransactionOwners(), Collections.<SealedMap>emptyList());
    }

    public static Forest recover(DatabaseImpl database,
                                 TransactionOwners transactionOwners,
                                 Collection<SealedMap> trees)
        throws IOException, InterruptedException
    {
        return new Forest(database, transactionOwners, trees);
    }

    // For use by this class

    private TimestampSet consolidatedTimestamps(List<Element> obsolete)
    {
        List<TimestampSet> consolidatedTimestampsList = new ArrayList<>();
        for (Element element : obsolete) {
            consolidatedTimestampsList.add(element.timestamps());
        }
        return TimestampSet.consolidate(consolidatedTimestampsList);
    }

    private String consolidationType(boolean durable1, boolean durable2)
    {
        return (durable1 ? "d" : "n") + (durable2 ? "d" : "n");
    }

    private Forest(DatabaseImpl database,
                   TransactionOwners transactionOwners,
                   Collection<SealedMap> maps)
        throws IOException, InterruptedException
    {
        super(database.factory());
        this.database = database;
        List<Element> consolidationElements = new ArrayList<>();
        for (SealedMap map : maps) {
            consolidationElements.add(map);
        }
        this.transactionOwners = transactionOwners;
        this.consolidationSet = ConsolidationSet.newConsolidationSet(this, consolidationElements);
    }

    private static final Logger LOG = Logger.getLogger(Forest.class.getName());
    private static final AtomicInteger consolidationCounter = new AtomicInteger(0);

    // Object state

    private final DatabaseImpl database;
    private final ConsolidationSet consolidationSet;
    private final TransactionOwners transactionOwners;
}
