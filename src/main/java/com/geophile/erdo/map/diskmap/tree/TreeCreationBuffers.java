/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.Configuration;

import java.nio.ByteBuffer;

// Buffers used in the creation of new segments and pages. These are organized per-thread, and are expected to be
// used only by consolidation threads. A consolidation thread should only need one set of buffers, (i.e., one
// TreeCreationBuffers object), per level, at a time. WriteableTree has a list of TreeCreationBuffers, one for each
// level.

class TreeCreationBuffers
{
    @Override
    public String toString()
    {
        return String.format("TreeCreationBuffers(%s)", id);
    }

    public ByteBuffer segmentBuffer()
    {
        checkOwner();
        return segmentBuffer;
    }

    public ByteBuffer erdoIdsBuffer()
    {
        checkOwner();
        return erdoIdsBuffer;
    }

    public ByteBuffer timestampsBuffer()
    {
        checkOwner();
        return timestampsBuffer;
    }

    public ByteBuffer keysBuffer()
    {
        checkOwner();
        return keysBuffer;
    }

    public ByteBuffer recordsBuffer()
    {
        checkOwner();
        return recordsBuffer;
    }

    public byte[] zeros()
    {
        checkOwner();
        return ZEROS;
    }

    public void markInUse()
    {
        assert !inUse;
        checkOwner();
        segmentBuffer.clear();
        erdoIdsBuffer.clear();
        timestampsBuffer.clear();
        keysBuffer.clear();
        recordsBuffer.clear();
        inUse = true;
    }

    public void markFree()
    {
        assert inUse;
        inUse = false;
    }

    public TreeCreationBuffers(Configuration configuration, int id)
    {
        this.id = id;
        this.owner = Thread.currentThread();
        int pageSizeBytes = configuration.diskPageSizeBytes();
        int segmentSizeBytes = configuration.diskSegmentSizeBytes();
        segmentBuffer = ByteBuffer.allocate(segmentSizeBytes);
        erdoIdsBuffer = ByteBuffer.allocate(pageSizeBytes);
        timestampsBuffer = ByteBuffer.allocate(pageSizeBytes);
        keysBuffer = ByteBuffer.allocate(pageSizeBytes);
        recordsBuffer = ByteBuffer.allocate(pageSizeBytes);
        if (ZEROS == null) {
            synchronized (getClass()) {
                if (ZEROS == null) {
                    ZEROS = new byte[pageSizeBytes];
                }
            }
        }
        assert ZEROS.length == pageSizeBytes;
    }

    private void checkOwner()
    {
        assert owner == Thread.currentThread();
    }

    private static byte[] ZEROS;

    private final int id;
    private final Thread owner;
    private final ByteBuffer segmentBuffer;
    private final ByteBuffer erdoIdsBuffer;
    private final ByteBuffer timestampsBuffer;
    private final ByteBuffer keysBuffer;
    private final ByteBuffer recordsBuffer;
    private boolean inUse = false;
}
