/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class ImmutableItemCacheTest
{
    @Test
    public void testReadMissing() throws InterruptedException, IOException
    {
        final int CACHE_SIZE = 10;
        ImmutableItemCache<Id, Item> cache = new ImmutableItemCache<>(CACHE_SIZE);
        for (int i = 0; i < CACHE_SIZE; i++) {
            Id id = id(i);
            Item item = cache.find(id, ITEM_READER);
            assertEquals(id, item.id());
        }
    }

    @Test
    public void testReadPresent() throws InterruptedException, IOException
    {
        final int CACHE_SIZE = 10;
        ImmutableItemCache<Id, Item> cache = new ImmutableItemCache<>(CACHE_SIZE);
        Item[] items = new Item[CACHE_SIZE];
        // Load cache
        for (int i = 0; i < CACHE_SIZE; i++) {
            Id id = id(i);
            Item item = cache.find(id, ITEM_READER);
            assertEquals(id, item.id());
            items[i] = item;
        }
        // Check that the identical items are retrieved
        for (int i = 0; i < CACHE_SIZE; i++) {
            Id id = id(i);
            Item item = cache.find(id, ITEM_READER);
            assertSame(items[i], item);
        }
    }

    @Test
    public void testOneEviction() throws InterruptedException, IOException
    {
        final int CACHE_SIZE = 10;
        EvictionObserver evictionObserver = new EvictionObserver();
        ImmutableItemCache<Id, Item> cache = new ImmutableItemCache<>(CACHE_SIZE, evictionObserver);
        Item[] items = new Item[CACHE_SIZE];
        // Load cache
        for (int i = 0; i < CACHE_SIZE; i++) {
            Id id = id(i);
            Item item = cache.find(id, ITEM_READER);
            assertEquals(id, item.id());
            items[i] = item;
        }
        items[0].okToEvict(true);
        // Add one more, forcing eviction
        Item forceEviction = cache.find(id(CACHE_SIZE), ITEM_READER);
        assertEquals(id(CACHE_SIZE), forceEviction.id());
        Map<Id, CacheEntry<Id, Item>> cacheContents = cache.cacheContents();
        // Cache should contain all of the original items except what was evicted, and the item that forced eviction.
        assertSame(forceEviction, cacheContents.get(id(CACHE_SIZE)));
        for (Item item : items) {
            if (item != forceEviction) {
                Item cacheItem = (Item) cacheContents.get(item.id());
                if (cacheItem == null) {
                    Item victim = (Item) evictionObserver.evicted;
                    assertEquals(victim.id(), item.id());
                } else {
                    assertSame(cacheItem, item);
                }
            }
        }
    }

    @Test
    public void testUnableToEvict() throws InterruptedException, IOException
    {
        final int CACHE_SIZE = 10;
        ImmutableItemCache<Id, Item> cache = new ImmutableItemCache<>(CACHE_SIZE);
        // Load cache and mark everything non-evictable
        int id = 0;
        while (id < CACHE_SIZE) {
            Item item = cache.find(id(id), ITEM_READER);
            item.okToEvict(false);
            id++;
        }
        try {
            cache.find(id(CACHE_SIZE), ITEM_READER);
            fail();
        } catch (ImmutableItemCacheError e) {
        }
    }

    @Test
    public void testWithControlledEviction() throws InterruptedException, IOException
    {
        final int CACHE_SIZE = 10;
        final int OPS = 1000;
        EvictionObserver evictionObserver = new EvictionObserver();
        ImmutableItemCache<Id, Item> cache = new ImmutableItemCache<>(CACHE_SIZE, evictionObserver);
        Random random = new Random(419);
        Set<Integer> expected = new HashSet<>();
        // Load cache and mark everything non-evictable
        int id = 0;
        while (id < CACHE_SIZE) {
            Item item = cache.find(id(id), ITEM_READER);
            item.okToEvict(false);
            item.recentAccess(false);
            expected.add(id);
            id++;
        }
        // System.out.println(String.format("Starting: %s", expected));
        // Operate on cache, tracking expected contents
        Map<Id, CacheEntry<Id, Item>> cacheContents = cache.cacheContents();
        for (int i = 0; i < OPS; i++) {
            assertEquals(CACHE_SIZE, cacheContents.size());
            // Make random item in cache evictable
            Iterator<CacheEntry<Id, Item>> iterator = cacheContents.values().iterator();
            int r = random.nextInt(CACHE_SIZE);
            for (int j = 0; j < r; j++) {
                iterator.next();
            }
            Item victim = (Item) iterator.next();
            // System.out.print(String.format("evict %s,  add %s -> ", victim.id().id, id));
            boolean removed = expected.remove(victim.id().value());
            assertTrue(removed);
            assertNotNull(victim);
            victim.okToEvict(true);
            // Get new cache item and check eviction
            Item newItem = cache.find(id(id), ITEM_READER);
            newItem.okToEvict(false);
            newItem.recentAccess(false);
            boolean added = expected.add(id);
            assertTrue(added);
            Item evicted = (Item) evictionObserver.evicted;
            assertSame(victim, evicted);
            id++;
            // System.out.println(expected);
        }
        List<Integer> expectedSorted = new ArrayList<>(expected);
        Collections.sort(expectedSorted);
        List<Integer> actualSorted = new ArrayList<>();
        for (Id actualId : cacheContents.keySet()) {
            actualSorted.add(actualId.value());
        }
        Collections.sort(actualSorted);
        assertEquals(expectedSorted, actualSorted);
    }

    private Id id(int id)
    {
        return new Id(id);
    }

    private static final ImmutableItemManager<Id, Item> ITEM_READER =
        new ImmutableItemManager<Id, Item>()
        {
            public Item getItemForCache(Id id)
            {
                return new Item(id);
            }

            @Override
            public void cleanupItemEvictedFromCache(Item item) throws IOException, InterruptedException
            {
            }
        };

    private static class EvictionObserver implements CacheEntryList.Observer<Id, Item>
    {
        public void adding(Item item)
        {
        }

        public void evicting(Item victim)
        {
            evicted = victim;
        }

        public CacheEntry<Id, Item> evicted = null;
    }
}
