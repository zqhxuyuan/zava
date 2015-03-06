/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.segmentfilemanager;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.util.FileUtil;
import com.geophile.erdo.util.IdGenerator;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SegmentFileManager extends AbstractSegmentFileManager
{
    // AbstractSegmentFileManager interface

    @Override
    public boolean delete(File file, long treeId, long segmentId)
    {
        FileUtil.deleteFile(file);
        return true;
    }

    @Override
    public void create(File file, long treeId, long segmentId) throws IOException
    {
        FileUtil.createFile(file);
        if (!file.createNewFile()) {
            throw new IOException(String.format("Unable to create file %s.", file));
        }
    }

    @Override
    public void register(File source, long treeId, long segmentId)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(File file, long position, ByteBuffer buffer) throws IOException, InterruptedException
    {
        long start = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = randomAccessFile(file);
        try {
            FileChannel channel = randomAccessFile.getChannel();
            channel.position(position);
            channel.write(buffer);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            long stop = System.currentTimeMillis();
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE,
                        "Wrote page at {0} of {1}: {2} msec",
                        new Object[]{position, file, stop - start});
            }
        } finally {
            randomAccessFile.close();
        }
    }

    @Override
    public ByteBuffer readPage(File file, long position, ByteBuffer pageBuffer)
        throws IOException, InterruptedException
    {
        long start = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = randomAccessFile(file);
        try {
            randomAccessFile.seek(position);
            randomAccessFile.readFully(pageBuffer.array(), pageBuffer.arrayOffset(), pageSizeBytes);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            long stop = System.currentTimeMillis();
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE,
                        "Read page at {0} of {1}: {2} msec",
                        new Object[]{position, file, stop - start});
            }
        } catch (EOFException e) {
            LOG.log(Level.SEVERE,
                    "Caught EOFException while reading {0}, position {1}",
                    new Object[]{file, position});
            LOG.log(Level.SEVERE, "stack", e);
            throw e;
        } finally {
            randomAccessFile.close();
        }
        return pageBuffer;
    }

    @Override
    public void flush(File file) throws IOException
    {
        long start = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = randomAccessFile(file);
        try {
            FileChannel channel = randomAccessFile.getChannel();
            channel.force(true);
            if (LOG.isLoggable(Level.FINE)) {
                long stop = System.currentTimeMillis();
                LOG.log(Level.FINE,
                        "Flushed {0}: {1} msec",
                        new Object[]{file, stop - start});
            }
        } finally {
            randomAccessFile.close();
        }
    }

    @Override
    public long newSegmentId()
    {
        return segmentIdGenerator.nextId();
    }

    @Override
    public void restoreSegmentIdGenerator(long lastSegmentId)
    {
        segmentIdGenerator.restore(lastSegmentId);
    }

    @Override
    public void resetStats()
    {
    }

    @Override
    public void resetForTesting()
    {
    }

    // SegmentFileManager interface

    public SegmentFileManager(Configuration configuration)
    {
        super(configuration);
    }

    // For use by this class

    private RandomAccessFile randomAccessFile(File file)
        throws FileNotFoundException
    {
        return new RandomAccessFile(file, MODE);
    }

    // Class state

    private static final String MODE = "rwd"; // Flush content but not metadata on each write.
    private static final Logger LOG = Logger.getLogger(SegmentFileManager.class.getName());

    // Object state

    private final IdGenerator segmentIdGenerator = new IdGenerator(0);
}
