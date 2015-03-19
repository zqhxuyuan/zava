/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.geophile.erdo.map;

import com.github.geophile.erdo.Configuration;
import com.github.geophile.erdo.RecordFactory;
import com.github.geophile.erdo.apiimpl.DatabaseImpl;
import com.github.geophile.erdo.map.diskmap.DiskPageCache;
import com.github.geophile.erdo.map.diskmap.tree.TreePositionPool;
import com.github.geophile.erdo.memorymonitor.MemoryMonitor;
import com.github.geophile.erdo.segmentfilemanager.AbstractSegmentFileManager;
import com.github.geophile.erdo.segmentfilemanager.pagememorymanager.PageMemoryManager;
import com.github.geophile.erdo.segmentfilemanager.pagememorymanager.SubAllocatingPageMemoryManager;
import com.github.geophile.erdo.transaction.LockManager;
import com.github.geophile.erdo.transaction.TimestampSet;
import com.github.geophile.erdo.transaction.TransactionManager;
import com.github.geophile.erdo.util.DefaultTestObserver;
import com.github.geophile.erdo.util.IdGenerator;

import java.io.IOException;
import java.util.List;

public abstract class Factory
{
    public final void registerRecordFactory(int erdoId, RecordFactory recordFactory)
    {
        if (recordFactory == null) {
            throw new IllegalArgumentException(String.format("No recordFactory provided for map with erdo id %s",
                                                             erdoId));
        }
        recordFactories.put(erdoId, recordFactory);
    }

    public final RecordFactory recordFactory(int erdoId)
    {
        return recordFactories.get(erdoId);
    }

    public final long nextTransactionTime()
    {
        return transactionTimeGenerator.nextId();
    }

    public final long nextTransactionTimestamp()
    {
        return transactionTimestampGenerator.nextId();
    }

    public final long newMapId()
    {
        return mapIdGenerator.nextId();
    }

    public abstract SealedMap newPersistentMap(DatabaseImpl database,
                                               TimestampSet timestamps,
                                               List<SealedMap> obsoleteTrees) throws IOException;

    public abstract SealedMap newTransientMap(DatabaseImpl database,
                                              TimestampSet timestamps,
                                              List<SealedMap> obsoleteTrees) throws IOException;

    public abstract AbstractSegmentFileManager segmentFileManager();

    public final MemoryMonitor memoryMonitor()
    {
        return memoryMonitor;
    }

    public final DiskPageCache diskPageCache()
    {
        return diskPageCache;
    }

    public abstract Class forestRecoveryClass();

    public final Configuration configuration()
    {
        return configuration;
    }

    public abstract LockManager lockManager();

    public final void transactionManager(TransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    public final TransactionManager transactionManager()
    {
        return transactionManager;
    }

    public final PageMemoryManager pageMemoryManager()
    {
        return pageMemoryManager;
    }

    public final void restoreTransactionTimestampGenerator(long timestamp)
    {
        transactionTimestampGenerator.restore(timestamp);
    }

    public final void restoreMapIdGenerator(long mapId)
    {
        mapIdGenerator.restore(mapId);
    }

    public TreePositionPool threadTreePositionPool()
    {
        throw new UnsupportedOperationException();
    }

    public DefaultTestObserver testObserver()
    {
        return testObserver;
    }

    protected Factory(Configuration configuration)
    {
        synchronized (getClass()) {
            assert !allocatedOnce;
            allocatedOnce = true;
        }
        this.memoryMonitor = new MemoryMonitor();
        this.configuration =  configuration;
        this.pageMemoryManager = new SubAllocatingPageMemoryManager(configuration);
        this.diskPageCache = new DiskPageCache(configuration);
        this.testObserver = new DefaultTestObserver();
    }

    // Class state

    protected static volatile boolean allocatedOnce = false;

    // Object state

    protected final Configuration configuration;
    protected final java.util.Map<Integer, RecordFactory> recordFactories = new java.util.HashMap<>();
    protected final MemoryMonitor memoryMonitor;
    protected TransactionManager transactionManager;
    private final IdGenerator mapIdGenerator = new IdGenerator();
    protected final IdGenerator transactionTimeGenerator = new IdGenerator();
    protected final IdGenerator transactionTimestampGenerator = new IdGenerator();
    protected final DiskPageCache diskPageCache;
    protected final PageMemoryManager pageMemoryManager;
    protected DefaultTestObserver testObserver;
}
