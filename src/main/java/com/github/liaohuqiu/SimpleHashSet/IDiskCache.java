package com.github.liaohuqiu.SimpleHashSet;

import java.io.File;
import java.io.IOException;

public interface IDiskCache {

    /**
     * Check if has this key
     *
     * @param key
     * @return
     */
    public boolean has(String key);

    /**
     * clear all data
     */
    public void clear() throws IOException;

    /**
     * close the cache
     */
    public void close() throws IOException;

    /**
     * flush data to dish
     */
    public void flush() throws IOException;

    /**
     * @param key
     * @return
     * @throws IOException
     */
    public CacheEntry getEntry(String key) throws IOException;

    /**
     * begin edit an {@CacheEntry }
     *
     * @param key
     * @return
     * @throws IOException
     */
    public CacheEntry beginEdit(String key) throws IOException;

    /**
     * abort edit
     *
     * @param cacheEntry
     */
    public void abortEdit(CacheEntry cacheEntry);

    public void commitEdit(CacheEntry cacheEntry) throws IOException;

    /**
     * delete if key exist, under edit can not be deleted
     *
     * @param key
     * @return
     */
    public boolean delete(String key) throws IOException;

    public long getCapacity();

    public long getSize();

    public File getDirectory();
}
