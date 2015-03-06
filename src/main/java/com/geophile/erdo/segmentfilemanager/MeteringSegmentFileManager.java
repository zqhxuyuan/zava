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
import java.util.concurrent.atomic.AtomicInteger;

public class MeteringSegmentFileManager extends SegmentFileManagerWrapper implements AbstractSegmentFileManager.Stats
{
    // AbstractSegmentFileManager interface

    @Override
    public void register(File file, long treeId, long segmentId)
    {
        nLink.incrementAndGet();
        filesystem.register(file, treeId, segmentId);
    }

    @Override
    public void write(File file, long position, ByteBuffer buffer) throws IOException, InterruptedException
    {
        int pages = buffer.remaining() / pageSizeBytes;
        nWrite.addAndGet(pages);
        filesystem.write(file, position, buffer);
    }

    @Override
    public ByteBuffer readPage(File file, long position, ByteBuffer pageBuffer)
        throws IOException, InterruptedException
    {
        nRead.incrementAndGet();
        return filesystem.readPage(file, position, pageBuffer);
    }

    @Override
    public void flush(File file) throws IOException
    {
        nFlush.incrementAndGet();
        filesystem.flush(file);
    }

    @Override
    public void resetStats()
    {
        nRead.set(0);
        nWrite.set(0);
        nFlush.set(0);
        nLink.set(0);
        filesystem.resetStats();
    }

    // AbstractSegmentFileManager.Stats interface

    public int nRead()
    {
        return nRead.get();
    }

    public int nWrite()
    {
        return nWrite.get();
    }

    public int nFlush()
    {
        return nFlush.get();
    }

    public int nLink()
    {
        return nLink.get();
    }

    // MeteringSegmentFileManager interface

    public MeteringSegmentFileManager(Configuration configuration, AbstractSegmentFileManager filesystem)
    {
        super(configuration, filesystem);
    }

    // Object state

    private final AtomicInteger nRead = new AtomicInteger(0);
    private final AtomicInteger nWrite = new AtomicInteger(0);
    private final AtomicInteger nFlush = new AtomicInteger(0);
    private final AtomicInteger nLink = new AtomicInteger(0);
}
