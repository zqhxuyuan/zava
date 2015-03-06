/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.systemtest.lockmanagement;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.RecordFactory;
import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.forest.Forest;
import com.geophile.erdo.forest.ForestRecovery;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.testarraymap.TestArrayMap;
import com.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;
import com.geophile.erdo.segmentfilemanager.ReferenceCountedSegmentFileManager;
import com.geophile.erdo.segmentfilemanager.SegmentFileManager;
import com.geophile.erdo.transaction.LockManager;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.transaction.TransactionManager;

import java.io.IOException;
import java.util.List;

public class TestFactory extends Factory
{
    // Factory interface

    @Override
    public SealedMap newPersistentMap(DatabaseImpl database,
                                      TimestampSet timestamps,
                                      List<SealedMap> obsoleteTrees)
    {
        return new TestArrayMap(this, timestamps);
    }

    @Override
    public SealedMap newTransientMap(DatabaseImpl database, TimestampSet timestamps, List<SealedMap> obsoleteTrees) throws IOException
    {
        return new TestArrayMap(this, timestamps);
    }

    @Override
    public AbstractSegmentFileManager segmentFileManager()
    {
        if (segmentFileManager == null) {
            SegmentFileManager segmentFileManager = new SegmentFileManager(configuration);
            this.segmentFileManager = new ReferenceCountedSegmentFileManager(configuration, segmentFileManager);
        }
        return segmentFileManager;
    }

    @Override
    public LockManager lockManager()
    {
        return lockManager;
    }

    @Override
    public Class forestRecoveryClass()
    {
        return ForestRecoveryInMemory.class;
    }

    // TestFactory interface

    public void reset()
    {
        transactionManager.clearThreadState();
    }

    public TestFactory(LockManager lockManager)
    {
        super(Configuration.defaultConfiguration());
        this.lockManager = lockManager;
        transactionManager(new TransactionManager(this));
        Transaction.initialize(this);
    }

    public void recordFactory(int erdoId, RecordFactory recordFactory)
    {
        recordFactories.put(erdoId, recordFactory);
    }

    // Object state

    private final LockManager lockManager;
    private AbstractSegmentFileManager segmentFileManager;

    public static class ForestRecoveryInMemory implements ForestRecovery
    {
        public Forest recoverForest(DatabaseImpl database) throws IOException
        {
            assert false;
            return null;
        }
    }
}
