/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.UsageError;
import com.geophile.erdo.config.ConfigurationKeys;
import com.geophile.erdo.config.ConfigurationMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConfigurationImpl extends Configuration implements DefaultConfiguration
{
    // Configuration interface

    public static Configuration defaultConfiguration()
    {
        ConfigurationImpl configuration = new ConfigurationImpl();
        configuration.diskPageSizeBytes(DEFAULT_DISK_PAGE_SIZE_BYTES);
        configuration.consolidationMaxPendingCommittedSizeBytes(DEFAULT_CONSOLIDATION_MAX_PENDING_COMMITTED_SIZE_BYTES);
        configuration.diskCacheSizeBytes(DEFAULT_DISK_CACHE_SIZE_BYTES);
        configuration.diskCacheSlabSizeBytes(DEFAULT_DISK_CACHE_SLAB_SIZE_BYTES);
        configuration.diskSegmentSizeBytes(DEFAULT_DISK_SEGMENT_SIZE_BYTES);
        configuration.consolidationThreads(DEFAULT_CONSOLIDATION_THREADS);
        configuration.consolidationMinSizeBytes(DEFAULT_CONSOLIDATION_MIN_SIZE_BYTES);
        configuration.consolidationMinMapsToConsolidate(DEFAULT_CONSOLIDATION_MIN_MAPS_TO_CONSOLIDATE);
        configuration.consolidationIdleTimeSec(DEFAULT_CONSOLIDATION_IDLE_TIME_SEC);
        configuration.keysInMemoryMapLimit(DEFAULT_KEYS_IN_MEMORY_MAP_LIMIT);
        configuration.keysBloomFilterErrorRate(DEFAULT_KEYS_BLOOM_FILTER_ERROR_RATE);
        return configuration;
    }

    public static Configuration emptyConfiguration()
    {
        return new ConfigurationImpl();
    }

    @Override
    public int diskPageSizeBytes()
    {
        return map.intValue(ConfigurationKeys.DISK_PAGE_SIZE_BYTES);
    }

    @Override
    public void diskPageSizeBytes(int pageSizeBytes)
    {
        map.value(ConfigurationKeys.DISK_PAGE_SIZE_BYTES, pageSizeBytes);
    }

    @Override
    public int diskSegmentSizeBytes()
    {
        return map.intValue(ConfigurationKeys.DISK_SEGMENT_SIZE_BYTES);
    }

    @Override
    public void diskSegmentSizeBytes(int segmentSizeBytes)
    {
        map.value(ConfigurationKeys.DISK_SEGMENT_SIZE_BYTES, segmentSizeBytes);
    }

    @Override
    public long diskCacheSizeBytes()
    {
        return map.longValue(ConfigurationKeys.DISK_CACHE_SIZE_BYTES);
    }

    @Override
    public void diskCacheSizeBytes(long cacheSizeBytes)
    {
        map.value(ConfigurationKeys.DISK_CACHE_SIZE_BYTES, cacheSizeBytes);
    }

    @Override
    public int diskCacheSlabSizeBytes()
    {
        return map.intValue(ConfigurationKeys.DISK_CACHE_SLAB_SIZE_BYTES);
    }

    @Override
    public void diskCacheSlabSizeBytes(int cacheSlabSizeBytes)
    {
        map.value(ConfigurationKeys.DISK_CACHE_SLAB_SIZE_BYTES, cacheSlabSizeBytes);
    }

    @Override
    public int consolidationThreads()
    {
        return map.intValue(ConfigurationKeys.CONSOLIDATION_THREADS);
    }

    @Override
    public void consolidationThreads(int consolidationThreads)
    {
        map.value(ConfigurationKeys.CONSOLIDATION_THREADS, consolidationThreads);
    }

    @Override
    public int consolidationMinSizeBytes()
    {
        return map.intValue(ConfigurationKeys.CONSOLIDATION_MIN_SIZE_BYTES);
    }

    @Override 
    public void consolidationMinSizeBytes(int consolidationMinSizeBytes)
    {
        map.value(ConfigurationKeys.CONSOLIDATION_MIN_SIZE_BYTES, consolidationMinSizeBytes);
    }

    @Override 
    public int consolidationMaxPendingCommittedSizeBytes()
    {
        return map.intValue(ConfigurationKeys.CONSOLIDATION_MAX_PENDING_COMMITTED_SIZE_BYTES);
    }

    @Override 
    public void consolidationMaxPendingCommittedSizeBytes(int consolidationMaxPendingCommittedSizeBytes)
    {
        map.value(ConfigurationKeys.CONSOLIDATION_MAX_PENDING_COMMITTED_SIZE_BYTES,
                  consolidationMaxPendingCommittedSizeBytes);
    }

    @Override
    public int consolidationMinMapsToConsolidate()
    {
        return map.intValue(ConfigurationKeys.CONSOLIDATION_MIN_MAPS_TO_CONSOLIDATE);
    }

    @Override
    public void consolidationMinMapsToConsolidate(int consolidationMinMapsToConsolidate)
    {
        map.value(ConfigurationKeys.CONSOLIDATION_MIN_MAPS_TO_CONSOLIDATE, consolidationMinMapsToConsolidate);
    }

    @Override
    public int consolidationIdleTimeSec()
    {
        return map.intValue(ConfigurationKeys.CONSOLIDATION_IDLE_TIME_SEC);
    }

    @Override
    public void consolidationIdleTimeSec(int consolidationIdleTimeSec)
    {
        map.value(ConfigurationKeys.CONSOLIDATION_IDLE_TIME_SEC, consolidationIdleTimeSec);
    }

    @Override
    public long keysInMemoryMapLimit()
    {
        return map.longValue(ConfigurationKeys.KEYS_PER_MAP_LIMIT);
    }

    @Override
    public void keysInMemoryMapLimit(long keysPerMapLimit)
    {
        map.value(ConfigurationKeys.KEYS_PER_MAP_LIMIT, keysPerMapLimit);
    }

    @Override
    public double keysBloomFilterErrorRate()
    {
        return map.doubleValue(ConfigurationKeys.KEYS_BLOOM_FILTER_ERROR_RATE);
    }

    @Override
    public void keysBloomFilterErrorRate(double keysBloomFilterErrorRate)
    {
        map.value(ConfigurationKeys.KEYS_BLOOM_FILTER_ERROR_RATE, keysBloomFilterErrorRate);
    }

    @Override
    public Map<String, String> toMap()
    {
        return map.toMap();
    }

    // ConfigurationImpl interface

    public void read(File file) throws IOException
    {
        map.read(file);
        checkConfiguration();
    }

    public void write(File file) throws IOException
    {
        checkConfiguration();
        map.write(file);
    }

    public void override(Configuration overrideConfiguration)
    {
        Map<String, String> override = ((ConfigurationImpl) overrideConfiguration).map.toMap();
        for (Map.Entry<String, String> entry : override.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!ConfigurationKeys.SETTABLE_ON_DATABASE_OPEN.contains(key)) {
                throw new UsageError(
                        String.format("The value of the configuration property %s " +
                                      "can only be set on database creation.",
                                      key));
            }
            map.toMap().put(key, value);
        }
    }

    public ConfigurationImpl()
    {
        this.map = new ConfigurationMap();
    }

    // For use by this class

    private void checkConfiguration()
    {
        if (!powerOf2(diskPageSizeBytes())) {
            throw new UsageError(
                String.format("%s must be a power of 2: %s",
                              ConfigurationKeys.DISK_PAGE_SIZE_BYTES, diskPageSizeBytes()));
        }
        if (!powerOf2(diskSegmentSizeBytes())) {
            throw new UsageError(
                String.format("%s must be a power of 2: %s",
                              ConfigurationKeys.DISK_PAGE_SIZE_BYTES, diskPageSizeBytes()));
        }
        if (!(diskSegmentSizeBytes() > diskPageSizeBytes())) {
            throw new UsageError(
                String.format("%s must exceed %s. %s = %s, %s = %s",
                              ConfigurationKeys.DISK_SEGMENT_SIZE_BYTES,
                              ConfigurationKeys.DISK_PAGE_SIZE_BYTES,
                              ConfigurationKeys.DISK_SEGMENT_SIZE_BYTES,
                              diskSegmentSizeBytes(),
                              ConfigurationKeys.DISK_PAGE_SIZE_BYTES,
                              diskPageSizeBytes()));
        }
    }

    boolean powerOf2(long x)
    {
        long powerOf2 = 1;
        while (powerOf2 < x) {
            powerOf2 *= 2;
        }
        return powerOf2 == x;
    }

    // Object state

    final ConfigurationMap map;
}