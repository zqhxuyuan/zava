/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.config;

import java.util.Arrays;
import java.util.List;

public interface ConfigurationKeys
{
    // Disk
    String DISK_PAGE_SIZE_BYTES = "disk.pageSizeBytes";
    String DISK_SEGMENT_SIZE_BYTES = "disk.segmentSizeBytes";
    String DISK_CACHE_SIZE_BYTES = "disk.cacheSizeBytes";
    String DISK_CACHE_SLAB_SIZE_BYTES = "disk.cacheSlabSizeBytes";
    // Consolidation
    String CONSOLIDATION_THREADS = "consolidation.threads";
    String CONSOLIDATION_MIN_SIZE_BYTES = "consolidation.minSizeBytes";
    String CONSOLIDATION_MAX_PENDING_COMMITTED_SIZE_BYTES = "consolidation.maxPendingCommittedSizeBytes";
    String CONSOLIDATION_MIN_MAPS_TO_CONSOLIDATE = "consolidation.minMapsToConsolidate";
    String CONSOLIDATION_IDLE_TIME_SEC = "consolidation.idleTimeSec";
    // Keys
    String KEYS_PER_MAP_LIMIT = "keys.perMapLimit";
    String KEYS_BLOOM_FILTER_ERROR_RATE = "keys.bloomFilterErrorRate";

    List<String> SETTABLE_ON_DATABASE_OPEN = Arrays.<String>asList(
            DISK_CACHE_SIZE_BYTES,
            DISK_CACHE_SLAB_SIZE_BYTES,
            CONSOLIDATION_THREADS,
            CONSOLIDATION_MIN_SIZE_BYTES,
            CONSOLIDATION_MAX_PENDING_COMMITTED_SIZE_BYTES,
            CONSOLIDATION_MIN_MAPS_TO_CONSOLIDATE,
            CONSOLIDATION_IDLE_TIME_SEC);
}
