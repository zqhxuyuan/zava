/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.forest.Forest;
import com.geophile.erdo.forest.ForestRecovery;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.diskmap.tree.TreePositionPool;
import com.geophile.erdo.map.testarraymap.TestArrayMap;
import com.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;
import com.geophile.erdo.segmentfilemanager.ReferenceCountedSegmentFileManager;
import com.geophile.erdo.segmentfilemanager.SegmentFileManager;
import com.geophile.erdo.transaction.LockManager;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.transaction.TransactionManager;
import com.geophile.erdo.util.TestObserver;

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
        return new TestArrayMap(this, timestamps, true);
    }

    @Override
    public SealedMap newTransientMap(DatabaseImpl database, TimestampSet timestamps, List<SealedMap> obsoleteTrees) throws IOException
    {
        return new TestArrayMap(this, timestamps, false);
    }

    @Override
    public AbstractSegmentFileManager segmentFileManager()
    {
        if (segmentFileManager == null) {
            assert configuration != null;
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
        transactionTimeGenerator.restore(-1L);
        transactionTimestampGenerator.restore(-1L);
        DatabaseImpl.reset();
        allocatedOnce = false;
        diskPageCache.clear();
        pageMemoryManager.reset();
        testObserver = new TestObserver();
    }

    public TestFactory()
    {
        this(Configuration.defaultConfiguration());
    }

    public TestFactory(Configuration configuration)
    {
        super(configuration);
        transactionManager(new TransactionManager(this));
        Transaction.initialize(this);
        lockManager = new LockManager();
        reset();
    }

    public void recordFactory(int erdoId, RecordFactory recordFactory)
    {
        recordFactories.put(erdoId, recordFactory);
    }

    @Override
    public TreePositionPool threadTreePositionPool()
    {
        return TREE_POSITION_POOL.get();
    }

    // Class state

    private static ThreadLocal<TreePositionPool> TREE_POSITION_POOL =
        new ThreadLocal<TreePositionPool>()
        {
            @Override
            protected TreePositionPool initialValue()
            {
                return new TreePositionPool();
            }
        };

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
