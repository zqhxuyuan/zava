/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.segmentfilemanager.pagememorymanager;

import com.geophile.erdo.Configuration;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;

// Memory is organized into Slabs, each wrapping a ByteBuffer.

public class SubAllocatingPageMemoryManager extends PageMemoryManager
{
    // PageMemoryManager interface

    @Override
    public ByteBuffer takePageBuffer()
    {
        ByteBuffer pageBuffer;
        for (int s = 0; s < slabs.length; s++) {
            pageBuffer = slabs[s].takePageBuffer();
            if (pageBuffer != null) {
                observer.takePageBuffer(s, pageBuffer.arrayOffset());
                return pageBuffer;
            }
        }
        throw new PageMemoryManagerException("No pages available!");
    }

    @Override
    public void returnPageBuffer(ByteBuffer pageBuffer)
    {
        Slab slab = slabMap.get(pageBuffer.array());
        assert slab != null;
        observer.returnPageBuffer(slab.slabId(), pageBuffer.arrayOffset());
        slab.returnPageBuffer(pageBuffer);
    }

    public void reset()
    {
        slabMap.clear();
        for (int s = 0; s < slabs.length; s++) {
            slabs[s].clear();
        }
    }

    // SubAllocatingPageMemoryManager interface

    public SubAllocatingPageMemoryManager(Configuration configuration)
    {
        this(configuration, DEFAULT_OBSERVER);
    }

    public SubAllocatingPageMemoryManager(Configuration configuration, Observer observer)
    {
        super(configuration);
        this.observer = observer;
        int maxSlabSize = configuration.diskCacheSlabSizeBytes();
        int nSlabs = (int) ((cacheSize + maxSlabSize - 1) / maxSlabSize);
        this.slabs = new Slab[nSlabs];
        this.slabMap = new IdentityHashMap<>();
        long remaining = (cacheSize / pageSize) * pageSize;
        for (int slabId = 0; slabId < nSlabs; slabId++) {
            int slabSize = (int) Math.min(remaining, maxSlabSize);
            Slab slab = new Slab(slabId, slabSize, pageSize);
            this.slabs[slabId] = slab;
            this.slabMap.put(slab.byteArray(), slab);
            remaining -= slabSize;
        }
    }

    // Class state

    private static Observer DEFAULT_OBSERVER =
        new Observer()
        {
            @Override
            public void takePageBuffer(int slabId, int offset)
            {
            }

            @Override
            public void returnPageBuffer(int slabId, int offset)
            {
            }
        };

    // Object state

    private final Observer observer;
    private final Slab[] slabs;
    private final IdentityHashMap<byte[], Slab> slabMap;

    // Inner classes

    public interface Observer
    {
        void takePageBuffer(int slabId, int offset);

        void returnPageBuffer(int slabId, int offset);
    }
}
