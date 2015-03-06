/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

interface DefaultConfiguration
{
    final int DEFAULT_DISK_PAGE_SIZE_BYTES = 32768;
    final int DEFAULT_DISK_SEGMENT_SIZE_BYTES = 4 * (1 << 20); // 4M
    final int DEFAULT_DISK_CACHE_SIZE_BYTES = 10 * (1 << 20); // 10M
    final int DEFAULT_DISK_CACHE_SLAB_SIZE_BYTES = 1 << 27; // 128M
    final int DEFAULT_CONSOLIDATION_THREADS = 3;
    final int DEFAULT_CONSOLIDATION_MIN_SIZE_BYTES = DEFAULT_DISK_SEGMENT_SIZE_BYTES;
    final int DEFAULT_CONSOLIDATION_MAX_PENDING_COMMITTED_SIZE_BYTES = 100 * (1 << 20); // 100M
    final int DEFAULT_CONSOLIDATION_MIN_MAPS_TO_CONSOLIDATE = 5;
    final int DEFAULT_CONSOLIDATION_IDLE_TIME_SEC = 5;
    final long DEFAULT_KEYS_IN_MEMORY_MAP_LIMIT = 500000;
    final double DEFAULT_KEYS_BLOOM_FILTER_ERROR_RATE = 0.001;
}