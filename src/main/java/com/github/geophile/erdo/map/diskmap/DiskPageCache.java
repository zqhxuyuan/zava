/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.geophile.erdo.map.diskmap;

import com.github.geophile.erdo.Configuration;
import com.github.geophile.erdo.immutableitemcache.ImmutableItemCache;
import com.github.geophile.erdo.immutableitemcache.ImmutableItemManager;
import com.github.geophile.erdo.map.diskmap.tree.TreeSegment;

import java.io.IOException;
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
