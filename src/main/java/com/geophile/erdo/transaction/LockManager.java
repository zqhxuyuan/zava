/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.AbstractKey;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;

public class LockManager
{
    // LockManager interface

    public void lock(AbstractKey key, Transaction transaction)
        throws InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        bucket(key).lock(key, transaction);
    }

    public void transactionCommitted(Transaction transaction, List<Transaction> irrelevantTransactions)
    {
        LOG.log(Level.INFO, "Aborting transactions waiting on keys locked by {0}", transaction);
        boolean mustVisit[] = possiblyLockedBuckets(transaction);
        for (int b = 0; b < nBuckets; b++) {
            if (mustVisit[b]) {
                buckets[b].abortAllWaiters(transaction);
            }
        }
        releaseLocksHeldByIrrelevantTransactions(irrelevantTransactions);
    }

    public void transactionAborted(Transaction transaction,
                                   List<Transaction> irrelevantTransactions)
    {
        LOG.log(Level.INFO, "Releasing locks held by {0}", transaction);
        assert irrelevantTransactions.contains(transaction);
        releaseLocksHeldByIrrelevantTransactions(irrelevantTransactions);
    }

    public void killDeadlockVictims() throws IOException, InterruptedException
    {
        Set<Transaction> deadlockVictims = deadlockDetector.victims();
        if (LOG.isLoggable(Level.INFO) && !deadlockVictims.isEmpty()) {
            LOG.log(Level.INFO, "Deadlock victims: {0}", deadlockVictims);
            for (LockManagerBucket bucket : buckets) {
                List<String> conflicts = new ArrayList<>();
                bucket.describeConflicts(conflicts);
                for (String conflict : conflicts) {
                    LOG.log(Level.INFO, "deadlock: {0}", conflict);
                }
            }
        }
        for (Transaction deadlockVictim : deadlockVictims) {
            deadlockVictim.markDeadlockVictim();
        }
        for (LockManagerBucket bucket : buckets) {
            bucket.wakeUp();
        }
    }

    public LockManager()
    {
        this(DEFAULT_BUCKETS);
    }

    public LockManager(int nBuckets)
    {
        this.nBuckets = nBuckets;
        this.buckets = new LockManagerBucket[nBuckets];
        for (int b = 0; b < nBuckets; b++) {
            buckets[b] = new LockManagerBucket(b);
        }
        this.deadlockDetector = new DeadlockDetector(this);
    }

    // For use by this package

    // This is used by DeadlockDetector. The output map is keyed by WaitsFor.waiter().
    // We get an atomic of view of each bucket, but not a point-in-time view for all buckets.
    // That's OK because the state pertaining to deadlock persists, and inconsistencies in
    // other transactions don't matter.
    Map<Transaction, WaitsFor> dependencies()
    {
        Map<Transaction, WaitsFor> dependencies = new ConcurrentHashMap<>();
        for (LockManagerBucket bucket : buckets) {
            bucket.findDependencies(dependencies);
        }
        return dependencies;
    }

    // For testing

    void waitOnConflict(boolean waitOnConflict)
    {
        this.waitOnConflict = waitOnConflict;
    }

    // For use by this class

    private void releaseLocksHeldByIrrelevantTransactions(List<Transaction> irrelevantTransactions)
    {
        for (Transaction irrelevantTransaction : irrelevantTransactions) {
            assert irrelevantTransaction.irrelevant() : irrelevantTransaction;
        }
        List<List<AbstractKey>> lockedKeysByBucket = lockedKeysByBucket(irrelevantTransactions);
        for (int b = 0; b < nBuckets; b++) {
            List<AbstractKey> keys = lockedKeysByBucket.get(b);
            if (keys != null) {
                if (LOG.isLoggable(Level.INFO) && !keys.isEmpty()) {
                    LOG.log(Level.INFO, "release locks for bucket {0}: {1}", new Object[]{b, keys});
                }
                buckets[b].releaseLocks(keys);
            }
        }
    }

    private List<List<AbstractKey>> lockedKeysByBucket(List<Transaction> transactions)
    {
        List<List<AbstractKey>> keysByBucket = new ArrayList<>(nBuckets);
        for (int bucketNumber = 0; bucketNumber < nBuckets; bucketNumber++) {
            keysByBucket.add(null);
        }
        for (Transaction transaction : transactions) {
            for (AbstractKey key : transaction.lockedForWrite()) {
                int bucketNumber = bucketNumber(key);
                LOG.log(Level.INFO,
                        "lockedKeysByBucket {0} {1}: bucket {2}",
                        new Object[]{transaction, key, bucketNumber});
                List<AbstractKey> keys = keysByBucket.get(bucketNumber);
                if (keys == null) {
                    keys = new ArrayList<>();
                    keysByBucket.set(bucketNumber, keys);
                }
                keys.add(key);
            }
        }
        return keysByBucket;
    }

    private LockManagerBucket bucket(AbstractKey key)
    {
        return buckets[bucketNumber(key)];
    }

    private boolean[] possiblyLockedBuckets(Transaction transaction)
    {
        boolean[] possiblyLockedBuckets = new boolean[nBuckets];
        Set<AbstractKey> locked = transaction.lockedForWrite();
        // TODO: Restore the original code
        if (false) { // locked.size() < DEFAULT_BUCKETS) {
            for (AbstractKey key : locked) {
                possiblyLockedBuckets[bucketNumber(key)] = true;
            }
        } else {
            // If locked.size() is sufficiently high we'll probably need to visit all buckets
            // anyway. No point in visiting every locked key to discover that fact.
            Arrays.fill(possiblyLockedBuckets, true);
        }
        return possiblyLockedBuckets;
    }
    
    private int bucketNumber(AbstractKey key)
    {
        return abs(key.hashCode() % nBuckets); 
    }

    // Class state

    static final int DEFAULT_BUCKETS = 100;
    private static final Logger LOG = Logger.getLogger(LockManager.class.getName());

    // Object state

    private final int nBuckets;
    private final LockManagerBucket[] buckets;
    private final DeadlockDetector deadlockDetector;
    volatile boolean waitOnConflict = true; // Sometimes false in tests
}
