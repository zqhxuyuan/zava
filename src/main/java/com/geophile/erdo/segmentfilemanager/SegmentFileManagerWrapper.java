/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.segmentfilemanager;

import com.geophile.erdo.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class SegmentFileManagerWrapper extends AbstractSegmentFileManager
{
    // AbstractSegmentFileManager interface

    @Override
    public boolean delete(File file, long treeId, long segmentId)
    {
        return filesystem.delete(file, treeId, segmentId);
    }

    @Override
    public void create(File file, long treeId, long segmentId) throws IOException
    {
        filesystem.create(file, treeId, segmentId);
    }

    @Override
    public void register(File file, long treeId, long segmentId)
    {
        filesystem.register(file, treeId, segmentId);
    }

    @Override
    public void write(File file, long position, ByteBuffer buffer) throws IOException, InterruptedException
    {
        filesystem.write(file, position, buffer);
    }

    @Override
    public ByteBuffer readPage(File file, long position, ByteBuffer pageBuffer)
        throws IOException, InterruptedException
    {
        return filesystem.readPage(file, position, pageBuffer);
    }

    @Override
    public void flush(File file) throws IOException
    {
        filesystem.flush(file);
    }

    @Override
    public long newSegmentId()
    {
        return filesystem.newSegmentId();
    }

    @Override
    public void restoreSegmentIdGenerator(long lastSegmentId)
    {
        filesystem.restoreSegmentIdGenerator(lastSegmentId);
    }

    @Override
    public void resetStats()
    {
        filesystem.resetStats();
    }

    @Override
    public void resetForTesting()
    {
        filesystem.resetForTesting();
    }

    // SegmentFileManagerWrapper interface

    public SegmentFileManagerWrapper(Configuration configuration, AbstractSegmentFileManager filesystem)
    {
        super(configuration);
        this.filesystem = filesystem;
    }

    // Object state

    protected final AbstractSegmentFileManager filesystem;
}
