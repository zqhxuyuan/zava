/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import com.geophile.erdo.memorymonitor.MemoryTracker;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PackedArray implements MemoryTracker.Trackable<PackedArray>
{
    // MemoryTracker.Trackable interface

    public long sizeBytes()
    {
        return buffers.size() * BUFFER_CAPACITY + bounds.size();
    }

    // PackedArray interface

    public void at(int position, Transferrable transferrable)
    {
        int end = bounds.at(position);
        int endBuffer = end >>> 16;
        int endBufferOffset = end & 0xffff;
        int startBuffer = 0;
        int startBufferOffset = 0;
        if (position > 0) {
            int start = bounds.at(position - 1);
            startBuffer = start >>> 16;
            startBufferOffset = start & 0xffff;
        }
        assert startBuffer == endBuffer || startBuffer == endBuffer - 1;
        ByteBuffer buffer = buffers.get(endBuffer);
        if (startBuffer < endBuffer) {
            startBufferOffset = 0;
        }
        ByteBuffer element = buffer.duplicate();
        element.limit(endBufferOffset);
        element.position(startBufferOffset);
        transferrable.readFrom(element);
    }

    public void append(Transferrable transferrable)
    {
        // Copy transferrable into a ByteBuffer.
        ensureBuffer();
        try {
            currentBuffer.mark();
            transferrable.writeTo(currentBuffer);
        } catch (BufferOverflowException firstException) {
            currentBuffer = null;
            ensureBuffer();
            try {
                transferrable.writeTo(currentBuffer);
            } catch (BufferOverflowException secondException) {
                // Restore state to where we were before the first attempt to append transferrable.
                removeLastBuffer();
                currentBuffer.mark();
                throw new ElementTooLargeException(transferrable);
            }
        }
        // Record the end of the element just copied in.
        assert currentBuffer.position() <= BUFFER_CAPACITY : currentBuffer;
        bounds.append(((buffers.size() - 1) << 16) | currentBuffer.position());
    }

    public int size()
    {
        return bounds.size();
    }

    public PackedArray(MemoryTracker<PackedArray> memoryTracker)
    {
        this.memoryTracker = memoryTracker;
    }

    // For use by this class

    private void ensureBuffer()
    {
        if (currentBuffer == null) {
            currentBuffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            buffers.add(currentBuffer);
            memoryTracker.track(sizeBytes());
        }
    }

    private void removeLastBuffer()
    {
        buffers.remove(buffers.size() - 1);
        currentBuffer = buffers.get(buffers.size() - 1);
        memoryTracker.track(sizeBytes() - BUFFER_CAPACITY);
    }

    // Class state

    // BUFFER_CAPACITY must be < 1<<16 because bounds element stores buffer offset in 16 bits.
    private final static int BUFFER_CAPACITY = 65000;

    // Object state

    // PackedArray elements are packed into ByteBuffers. A single element is completely contained
    // within one ByteBuffer. A bound value stores an index into buffers in the high 16 bits. The
    // low 16 bits point into the indicated buffer.

    // In general, a PackedArray element is located by finding the bounds at either end, and using
    // these to construct a ByteBuffer. There are two subtleties. 1) the 0th PackedArray element
    // begins at 0, and this bound is not stored. 2) For the last element in a ByteBuffer, the end
    // of the element is described by the ByteBuffer's limit.

    private final List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
    private ByteBuffer currentBuffer; // Last element of buffers, and the buffer currently being loaded.
    private final IntArray bounds = new IntArray(null);
    // Memory tracking is not precise because this PackedArray and the bounds IntArray grow
    // independently of one another. Should be close enough.
    private final MemoryTracker<PackedArray> memoryTracker;

    public class ElementTooLargeException extends RuntimeException
    {
        public ElementTooLargeException(Transferrable transferrable)
        {
            this.transferrable = transferrable;
        }

        public Transferrable transferrable()
        {
            return transferrable;
        }

        private Transferrable transferrable;
    }
}
