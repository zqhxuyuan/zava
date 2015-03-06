/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Cache of items that are never updated. Because the items never change, there are no dirty items that need to
// be written back anywhere, which keeps things extremely simple.
// ID: The key of the items in the cache.
// ITEM: What's in the cache.

public class ImmutableItemCache<ID, ITEM extends CacheEntry<ID, ITEM>>
{
    public ITEM find(ID id, ImmutableItemManager<ID, ITEM> itemManager) throws InterruptedException, IOException
    {
        ITEM item;
        boolean cleanupPlaceholder = false;
        CacheEntry<ID, ITEM> entry = cache.get(id);
        if (entry != null && !entry.placeholder()) {
            entry.recentAccess(true);
            item = entry.item();
        } else {
            try {
                synchronized (this) {
                    while ((entry = cache.get(id)) != null && entry.placeholder()) {
                        wait();
                    }
                    if (entry == null) {
                        assert cacheSize <= cacheCapacity;
                        if (cacheSize == cacheCapacity) {
                            // Cache is full. Evict something.
                            ITEM victim = clock.takeItemToEvict();
                            assert victim != null;
                            CacheEntry<ID, ITEM> removed = cache.remove(victim.id());
                            assert removed == victim : String.format("replaced: %s, entry: %s", removed, entry);
                            itemManager.cleanupItemEvictedFromCache(victim);
                            cacheSize--;
                        }
                        assert cacheSize < cacheCapacity;
                        entry = ItemPlaceholder.<ID, ITEM>forCurrentThread();
                        CacheEntry<ID, ITEM> replaced = cache.put(id, entry);
                        assert replaced == null;
                        cacheSize++;
                    } else {
                        assert !entry.placeholder();
                        assert entry.id().equals(id);
                        entry.recentAccess(true);
                    }
                }
                if (entry.placeholder()) {
                    // This thread wrote a placeholder. Read the cache item outside the lock, since this
                    // is probably slow.
                    assert entry.owner() == Thread.currentThread();
                    item = itemManager.getItemForCache(id);
                    // Inside the lock, replace the placeholder with the item, and notify waiters.
                    synchronized (this) {
                        CacheEntry<ID, ITEM> replaced = cache.put(id, item);
                        assert replaced == entry : String.format("replaced: %s, entry: %s", replaced, entry);
                        clock.addItem(item);
                        item.recentAccess(true);
                        notifyAll();
                    }
                } else {
                    entry.recentAccess(true);
                    item = entry.item();
                }
            } catch (RuntimeException | Error e) {
                cleanupPlaceholder = true;
                throw e;
            } finally {
                if (cleanupPlaceholder) {
                    synchronized (this) {
                        entry = cache.get(id);
                        if (entry != null && entry.placeholder()) {
                            cache.remove(id);
                        }
                    }
                }
            }
        }
        assert item.id().equals(id);
        return item;
    }

    public int size()
    {
        return cacheSize;
    }

    public ImmutableItemCache(int cacheCapacity)
    {
        this(cacheCapacity,
             new CacheEntryList.Observer<ID, ITEM>()
             {
                 public void adding(ITEM item)
                 {
                 }

                 public void evicting(ITEM victim)
                 {
                 }
             });
    }

    public ImmutableItemCache(int cacheCapacity, CacheEntryList.Observer<ID, ITEM> observer)
    {
        this.cacheCapacity = cacheCapacity;
        this.cache = new ConcurrentHashMap<>(cacheCapacity);
        this.clock = new CacheEntryList<>(observer);
    }

    // For testing

    Map<ID, CacheEntry<ID, ITEM>> cacheContents()
    {
        return cache;
    }

    public void clear()
    {
        cache.clear();
        cacheSize = 0;
        clock.clear();
    }

    // Object state

    private final int cacheCapacity;
    private final Map<ID, CacheEntry<ID, ITEM>> cache;
    private volatile int cacheSize = 0; // Maintained here because cache.size() is O(n)
    private final CacheEntryList<ID, ITEM> clock; // The "clock" of the clock algorithm
}
