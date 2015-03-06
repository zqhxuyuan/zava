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

public abstract class AbstractSegmentFileManager
{
    /**
     * Delete the file, which belongs to the tree identified by treeId, and contains the state of the segment
     * represented by segmentId. If the segment is linked by more than one tree, only the reference to the segment,
     * not the segment itself.
     *
     * @param file The file to be deleted (or unlinked)
     * @param treeId Identifies the tree containing the file
     * @param segmentId Identifies the segment whose state is contained in the file
     * @return true if the file was actually deleted, false otherwise (i.e., the segment is still used by another tree).
     */
    public abstract boolean delete(File file, long treeId, long segmentId);

    public abstract void create(File file, long treeId, long segmentId) throws IOException;

    public abstract void register(File file, long treeId, long segmentId);

    public abstract void write(File file, long position, ByteBuffer buffer)
        throws IOException, InterruptedException;

    public abstract ByteBuffer readPage(File file, long position, ByteBuffer pageBuffer)
        throws IOException, InterruptedException;

    public abstract void flush(File file) throws IOException;

    public abstract long newSegmentId();

    public abstract void restoreSegmentIdGenerator(long lastSegmentId);

    public abstract void resetStats();

    public abstract void resetForTesting();

    // For use by subclasses

    protected AbstractSegmentFileManager(Configuration configuration)
    {
        this.configuration = configuration;
        this.pageSizeBytes = configuration.diskPageSizeBytes();
    }

    // Object state

    protected final Configuration configuration;
    protected final int pageSizeBytes;

    // Inner classes

    public interface Stats
    {
        int nRead();
        int nWrite();
        int nFlush();
    }
}
