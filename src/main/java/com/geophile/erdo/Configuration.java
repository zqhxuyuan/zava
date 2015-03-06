/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.ConfigurationImpl;

import java.util.Map;

/**
 * Provides access to a database's configuration. When a database is being created, the default configuration is used.
 * To use a different configuration, call {@link #defaultConfiguration()} to obtain an initial configuration and then
 * apply changes. When a database is being opened, some aspects of configuration can be overridden. To do this, call
 * {@link #emptyConfiguration()} to obtain an initial configuration and then apply changes. See
 * {@link com.geophile.erdo.Database#useDatabase(java.io.File, Configuration)} for a list of configuration settings
 * that can be modified.
 */

public abstract class Configuration
{
    /**
     * Returns the default database configuration. Starting point for customizing the configuration.
     * @return The default configuration.
     */
    public static Configuration defaultConfiguration()
    {
        return ConfigurationImpl.defaultConfiguration();
    }

    /**
     * Returns an empty configuration. Starting point for creating configuration overrides when opening a database.
     * @return An empty configuration.
     */
    public static Configuration emptyConfiguration()
    {
        return ConfigurationImpl.emptyConfiguration();
    }

    /**
     * Returns the size of a page.
     * @return The size of a page, in bytes.
     */
    public abstract int diskPageSizeBytes();

    /**
     * Sets the size of a disk page.
     * @param pageSizeBytes The size of a page, in bytes.
     */
    public abstract void diskPageSizeBytes(int pageSizeBytes);

    /**
     * Returns the size of a segment.
     * @return The size of a segment, in bytes.
     */
    public abstract int diskSegmentSizeBytes();

    /**
     * Sets the size of a segment.
     * @param segmentSizeBytes The size of a segment, in bytes.
     */
    public abstract void diskSegmentSizeBytes(int segmentSizeBytes);

    /**
     * Returns the size of the disk page cache.
     * @return The size of the disk page cache, in bytes.
     */
    public abstract long diskCacheSizeBytes();

    /**
     * Sets the size of the disk page cache.
     * @param cacheSizeBytes The size of the disk page cache, in bytes.
     */
    public abstract void diskCacheSizeBytes(long cacheSizeBytes);

    /**
     * Returns the size of a single slab of the disk page cache.
     * @return The size of a single slab of the disk page cache, in bytes.
     */
    public abstract int diskCacheSlabSizeBytes();

    /**
     * Sets the size of a single slab of the disk page cache.
     * @param cacheSlabSizeBytes The size of a single slab of the disk page cache, in bytes.
     */
    public abstract void diskCacheSlabSizeBytes(int cacheSlabSizeBytes);

    /**
     * Returns the number of threads for doing asynchronous consolidations.
     * @return The number of threads for doing asynchronous consolidations.
     */
    public abstract int consolidationThreads();

    /**
     * Sets the number of threads for doing asynchronous consolidation.
     * @param consolidationThreads The number of threads for doing asynchronous consolidations.
     */
    public abstract void consolidationThreads(int consolidationThreads);

    /**
     * Returns the minimum size of a durable map. If consolidation estimates that a consolidated durable map will
     * be below this size, the consolidation will not be carried out.
     * @return The minimum size of a durable map, in bytes.
     */
    public abstract int consolidationMinSizeBytes();

    /**
     * Sets the minimum size of a durable map. If consolidation estimates that a consolidated durable map will
     * be below this size, the consolidation will not be carried out.
     * @param consolidationMinSizeBytes The minimum size of a durable map, in bytes.
     */
    public abstract void consolidationMinSizeBytes(int consolidationMinSizeBytes);

    /**
     * Returns the maximum permitted total size of maps that are committed but not yet durable. If the size of such maps
     * exceeds this size, then commits are throttled.
     * @return The maximum permitted total size of maps, in bytes, that are committed but not yet durable.
     */
    public abstract int consolidationMaxPendingCommittedSizeBytes();

    /**
     * Sets the maximum permitted total size of maps that are committed but not yet durable. If the size of such maps
     * exceeds this size, then commits are throttled.
     * @param consolidationPendingCommittedSizeBytes The maximum permitted total size of maps, in bytes,
     *     that are committed but not yet durable.
     */
    public abstract void consolidationMaxPendingCommittedSizeBytes(int consolidationPendingCommittedSizeBytes);

    /**
     * Returns the minimum number of maps that can be consolidated. If the number of candidate maps is below
     * this value, the consolidation will not be carried out.
     * @return The minimum number of maps that can be consolidated.
     */
    public abstract int consolidationMinMapsToConsolidate();

    /**
     * Sets the minimum number of maps that can be consolidated. If the number of candidate maps is below
     * this value, the consolidation will not be carried out.
     * @param consolidationMinMapsToConsolidate The minimum number of maps that can be consolidated.
     */
    public abstract void consolidationMinMapsToConsolidate(int consolidationMinMapsToConsolidate);

    /**
     * Returns the ime to wait before the application is considered idle, and a consolidation is started. Once
     * a consolidation is
     * started due to inactivity, the consolidation will be completed (unless the database is closed).
     * @return Time, in seconds, to wait before the application is considered idle, and a consolidation is started.
     */
    public abstract int consolidationIdleTimeSec();

    /**
     * Sets the time to wait before the application is considered idle, and a consolidation is started. Once
     * a consolidation is
     * started due to inactivity, the consolidation will be completed (unless the database is closed).
     * @param consolidationIdleTimeSec Time, in seconds, to wait before the application is considered idle,
     *     and a consolidation is started.
     */
    public abstract void consolidationIdleTimeSec(int consolidationIdleTimeSec);

    /**
     * Returns the maximum size of a map whose keys are kept in memory.
     * @return The maximum size of a map whose keys are kept in memory.
     */
    public abstract long keysInMemoryMapLimit();

    /**
     * Sets the maximum size of a map whose keys are kept in memory.
     * @param keysInMemoryMapLimit The maximum size of a map whose keys are kept in memory.
     */
    public abstract void keysInMemoryMapLimit(long keysInMemoryMapLimit);

    /**
     * Returns the bloom filter error rate.
     * @return The bloom filter error rate.
     */
    public abstract double keysBloomFilterErrorRate();

    /**
     * Sets the bloom filter error rate.
     * @param keysBloomFilterErrorRate The bloom filter error rate.
     */
    public abstract void keysBloomFilterErrorRate(double keysBloomFilterErrorRate);

    /**
     * Return the configuration as a map.
     * @return the configuration as a map.
     */
    public abstract Map<String, String> toMap();
}
