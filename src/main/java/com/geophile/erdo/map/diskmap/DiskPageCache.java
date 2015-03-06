/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.immutableitemcache.ImmutableItemCache;
import com.geophile.erdo.immutableitemcache.ImmutableItemManager;
import com.geophile.erdo.map.diskmap.tree.TreePosition;
import com.geophile.erdo.map.diskmap.tree.TreeSegment;
import com.geophile.erdo.util.IdentitySet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiskPageCache extends ImmutableItemCache<PageId, DiskPage>
{
    public DiskPage page(TreeSegment segment, int pageNumber, ImmutableItemManager<PageId, DiskPage> diskPageReader)
        throws IOException, InterruptedException
    {
        PageId pageId = new PageId(segment.segmentId(), pageNumber);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "read {0}", pageId);
        }
        return find(pageId, diskPageReader);
    }

    public DiskPageCache(Configuration configuration)
    {
        super(cacheSlots(configuration));
        LOG.log(Level.INFO, "cache slots: {0}", cacheSlots(configuration));
    }

    // For use by this class

    private static int cacheSlots(Configuration configuration)
    {
        long cacheSizeBytes = configuration.diskCacheSizeBytes();
        int pageSizeBytes = configuration.diskPageSizeBytes();
        return (int) (cacheSizeBytes / pageSizeBytes);
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(DiskPageCache.class.getName());
}
