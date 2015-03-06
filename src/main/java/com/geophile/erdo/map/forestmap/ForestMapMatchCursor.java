/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.forestmap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.bloomfilter.BloomFilter;
import com.geophile.erdo.forest.ForestSnapshot;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.mergescan.MergeCursor;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

class ForestMapMatchCursor extends ForestMapCursor
{
    // MapCursor interface

    @Override
    public LazyRecord next() throws IOException, InterruptedException
    {
        return neighbor(true);
    }

    @Override
    public LazyRecord previous() throws IOException, InterruptedException
    {
        return neighbor(false);
    }

    // ForestMapMatchCursor interface

    ForestMapMatchCursor(ForestSnapshot forestSnapshot, AbstractKey key)
        throws IOException, InterruptedException
    {
        super(forestSnapshot, key, true);
    }

    // For use by this class

    private LazyRecord updateRecord(AbstractKey key) throws IOException, InterruptedException
    {
        SealedMap map = forestSnapshot.mapContainingTransaction(key.transactionTimestamp());
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Getting record of {0} from {1}", new Object[]{key, map});
        }
        assert map != null : key;
        MapCursor cursor = map.cursor(key, true);
        LazyRecord updateRecord = cursor.next();
        cursor.close();
        assert updateRecord != null : key;
        return updateRecord;
    }

    private LazyRecord neighbor(boolean forward) throws IOException, InterruptedException
    {
        MergeCursor smallTreeRecordScan = null;
        MergeCursor bigTreeRecordScan = null;
        LazyRecord neighbor = null;
        List<SealedMap> smallTrees = null;
        try {
            if (state != State.DONE) {
                // cursor vs. keyScan:
                // - cursor merges after getting records, but uses bloom filter to avoid getting
                //   record unnecessarily.
                // - keyScan uses KeyArrays, probably slower than bloom filter, but has the
                //   advantage of finding only the most recent.
                // - Big trees don't have KeyArrays.
                smallTreeRecordScan = new MergeCursor(startKey, forward);
                smallTrees = forestSnapshot.smallTrees();
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Scanning small trees: {0}", smallTrees);
                }
                for (SealedMap smallTree : smallTrees) {
                    smallTreeRecordScan.addInput(BloomFilter.USE_BLOOM_FILTER
                                                 ? smallTree.cursor(startKey, true)
                                                 : smallTree.keyScan(startKey, true));
                }
                smallTreeRecordScan.start();
                // If smallTreeKeyScan.next()/previous() returns a key, it is the one and only key that
                // this cursor will yield. Otherwise, if the key is present, it must come from a
                // big tree.
                neighbor = forward ? smallTreeRecordScan.next() : smallTreeRecordScan.previous();
                if (neighbor != null) {
                    if (!BloomFilter.USE_BLOOM_FILTER) {
                        AbstractKey key = neighbor.key();
                        neighbor.destroyRecordReference();
                        neighbor = updateRecord(key);
                    }
                } else {
                    bigTreeRecordScan = new MergeCursor(startKey, forward);
                    for (SealedMap bigTree : forestSnapshot.bigTrees()) {
                        bigTreeRecordScan.addInput(bigTree.cursor(startKey, true));
                    }
                    bigTreeRecordScan.start();
                    neighbor = forward ? bigTreeRecordScan.next() : bigTreeRecordScan.previous();
                }
                // Because this is an exact-match cursor, at most one record will be returned.
                // So whether we found one or not, we're done.
                close();
            }
        } finally {
            if (smallTreeRecordScan != null) {
                smallTreeRecordScan.close();
            }
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Scan of small trees done: {0}", smallTrees);
            }
            if (bigTreeRecordScan != null) {
                bigTreeRecordScan.close();
            }
        }
        return neighbor;
    }

}
