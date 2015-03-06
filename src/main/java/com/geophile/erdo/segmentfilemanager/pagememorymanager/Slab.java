/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.segmentfilemanager.pagememorymanager;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * A Slab provides the memory from which pages are allocated. Initially, the entire slab is unused. Initial allocations
 * are done from the beginning. Later, page buffers are returned and kept in a list. When a page buffer is requested,
 * a returned page buffer is used if available.
 */

class Slab
{
    public synchronized ByteBuffer takePageBuffer()
    {
        ByteBuffer buffer = available.isEmpty() ? takeNew() : takeUsed();
        if (buffer != null) {
            assert buffer.position() == 0;
            assert buffer.remaining() == pageSize;
        }
        return buffer;
    }

    public void returnPageBuffer(ByteBuffer pageBuffer)
    {
        available.add(pageBuffer);
    }

    public Slab(int slabId, int slabSize, int pageSize)
    {
        this.slabId = slabId;
        this.pageSize = pageSize;
        long start = System.currentTimeMillis();
        this.slab = ByteBuffer.allocate(slabSize);
        long stop = System.currentTimeMillis();
        LOG.log(Level.INFO,
                "Allocated slab {0} of size {1} in {2} msec",
                new Object[]{slabId, slabSize, stop - start});
    }

    public int slabId()
    {
        return slabId;
    }

    public byte[] byteArray()
    {
        return slab.array();
    }

    public void clear()
    {
        available.clear();
        slab.clear();
    }

    private ByteBuffer takeNew()
    {
        ByteBuffer pageBuffer = null;
        if (slab.remaining() >= pageSize) {
            pageBuffer = slab.slice();
            pageBuffer.limit(pageSize);
            slab.position(slab.position() + pageSize);
        }
        return pageBuffer;
    }

    private ByteBuffer takeUsed()
    {
        ByteBuffer buffer = available.remove();
        buffer.position(0);
        return buffer;
    }

    private static final Logger LOG = Logger.getLogger(Slab.class.getName());

    private final int slabId;
    private final int pageSize;
    private final ByteBuffer slab;
    private Queue<ByteBuffer> available = new ArrayDeque<>();
}
