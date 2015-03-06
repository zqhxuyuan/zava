/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.segmentfilemanager;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReferenceCountedSegmentFileManager extends SegmentFileManagerWrapper
{
    // AbstractSegmentFileManager interface

    @Override
    public synchronized void create(File file, long treeId, long segmentId) throws IOException
    {
        LOG.log(Level.FINE, "Creating {0} for use by {1}", new Object[]{file, treeId});
        FileUtil.createFile(file);
        List<Long> users = fileUsers.get(segmentId);
        assert users == null : file;
        users = new ArrayList<>();
        fileUsers.put(segmentId, users);
        users.add(treeId);
    }

    @Override
    public synchronized boolean delete(File file, long treeId, long segmentId)
    {
        boolean deleted;
        List<Long> users = fileUsers.get(segmentId);
        if (users != null) {
            LOG.log(Level.FINE, "Unregistering {0} used by {1}", new Object[]{file, treeId});
            // Cast to Object because List.remove(int x) removes the element at position x.
            // This call should normally return true. But there is a small window between
            // creating the file and registering it (in createSegmentFile, in this class). If
            // an InterruptedException occurs between those actions then we're probably here
            // while cleaning up, and users.remove could return false.
            users.remove((Object) treeId);
            if (users.isEmpty()) {
                LOG.log(Level.FINE, "Really deleting {0}", file);
                fileUsers.remove(segmentId);
                filesystem.delete(file, treeId, segmentId);
                deleted = true;
            } else {
                deleted = false;
            }
        } else {
            // must be deleting an obsolete file during recovery.
            // TODO: Don't assume recovery, but there's currently no way to check whether we are.
            filesystem.delete(file, treeId, segmentId);
            deleted = true;
        }
        return deleted;
    }

    @Override
    public synchronized void register(File file, long treeId, long segmentId)
    {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Registering {0} for use by {1}", new Object[]{file, treeId});
        }
        List<Long> users = fileUsers.get(segmentId);
        if (users == null) {
            users = new ArrayList<>();
            fileUsers.put(segmentId, users);
        }
        assert !users.contains(treeId) : treeId;
        users.add(treeId);
    }

    public void resetForTesting()
    {
        fileUsers.clear();
    }

    // ReferenceCountedSegmentFileManager interface

    public ReferenceCountedSegmentFileManager(Configuration configuration, AbstractSegmentFileManager filesystem)
    {
        super(configuration, filesystem);
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(ReferenceCountedSegmentFileManager.class.getName());

    // Object state

    private final Map<Long, List<Long>> fileUsers = new HashMap<>();
}
