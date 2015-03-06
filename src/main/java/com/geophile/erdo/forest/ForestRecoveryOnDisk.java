/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.forest;

import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.apiimpl.DatabaseOnDisk;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.map.diskmap.DiskMap;
import com.geophile.erdo.map.diskmap.Manifest;
import com.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;
import com.geophile.erdo.transaction.TransactionOwners;
import com.geophile.erdo.util.FileUtil;
import com.geophile.erdo.util.LongArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.max;

public class ForestRecoveryOnDisk implements ForestRecovery
{
    // ForestRecovery interface

    public Forest recoverForest(DatabaseImpl databaseImpl) throws IOException, InterruptedException
    {
        LOG.log(Level.WARNING, "Starting recovery of {0}", databaseImpl);
        DatabaseOnDisk database = (DatabaseOnDisk) databaseImpl;
        Factory factory = database.factory();
        AbstractSegmentFileManager segmentFileManager = factory.segmentFileManager();
        Forest forest;
        java.util.Map<Long, SealedMap> maps = new HashMap<>(); // map id -> map
        TransactionOwners transactionOwners = new TransactionOwners();
        LongArray obsoleteTreeIds = new LongArray();
        // Find trees on disk and gather ids of obsolete trees. Also compute max map id to restore
        // map id generator.
        DBStructure dbStructure = database.dbStructure();
        long maxMapId = -1L;
        for (int treeId : dbStructure.treeIds()) {
            DiskMap map = DiskMap.recover(database, treeId);
            if (map == null) {
                if (deleteUnrecoverableTrees()) {
                    LOG.log(Level.WARNING, "{0} is not recoverable - deleting directory", treeId);
                    FileUtil.deleteFile(dbStructure.manifestFile(treeId));
                }
            } else {
                maps.put(map.mapId(), map);
                if (map.mapId() > maxMapId) {
                    maxMapId = map.mapId();
                }
                for (long obsoleteTreeId : map.obsoleteTreeIds()) {
                    obsoleteTreeIds.append(obsoleteTreeId);
                }
            }
        }
        // Delete obsolete trees
        for (Long obsoleteTreeId : obsoleteTreeIds) {
            SealedMap obsoleteTree = maps.remove(obsoleteTreeId);
            if (obsoleteTree != null) {
                obsoleteTree.destroyPersistentState();
            }
        }
        // Compute transactionOwners and compute max transaction timestamp
        long maxTransactionTimestamp = -1L;
        long maxSegmentId = -1L;
        for (SealedMap map : maps.values()) {
            transactionOwners.add(map);
            maxTransactionTimestamp = max(maxTransactionTimestamp, map.timestamps().maxTimestamp());
            maxSegmentId = max(maxSegmentId, maxSegmentId(map));
        }
        transactionOwners.checkCoverage(maxTransactionTimestamp);
        factory.restoreTransactionTimestampGenerator(maxTransactionTimestamp);
        factory.restoreMapIdGenerator(maxMapId);
        segmentFileManager.restoreSegmentIdGenerator(maxSegmentId);
        forest = Forest.recover(database, transactionOwners, maps.values());
        factory.transactionManager(forest);
        LOG.log(Level.WARNING, "Finished recovery of {0}", databaseImpl);
        return forest;
    }

    public boolean deleteUnrecoverableTrees()
    {
        return true;
    }

    // For use by this class

    private long maxSegmentId(SealedMap map)
    {
        DiskMap diskMap = (DiskMap) map;
        Manifest manifest = diskMap.manifest();
        long maxSegmentId = -1L;
        for (int level = 0; level < manifest.levels(); level++) {
            LongArray segmentIds = manifest.segmentIds(level);
            for (int i = 0; i < segmentIds.size(); i++) {
                maxSegmentId = max(maxSegmentId, segmentIds.at(i));
            }
        }
        return maxSegmentId;
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(ForestRecoveryOnDisk.class.getName());
}
