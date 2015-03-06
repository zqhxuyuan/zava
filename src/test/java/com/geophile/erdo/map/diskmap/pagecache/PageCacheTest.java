/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.pagecache;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.immutableitemcache.CacheEntryList;
import com.geophile.erdo.immutableitemcache.ImmutableItemCache;
import com.geophile.erdo.immutableitemcache.ImmutableItemCacheError;
import com.geophile.erdo.immutableitemcache.ImmutableItemManager;
import com.geophile.erdo.segmentfilemanager.pagememorymanager.PageMemoryManager;
import com.geophile.erdo.segmentfilemanager.pagememorymanager.SubAllocatingPageMemoryManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;
import static org.junit.Assert.*;

public class PageCacheTest
{
    @Before
    public void before() throws IOException, InterruptedException
    {
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.diskCacheSizeBytes(CACHE_SIZE);
        configuration.diskCacheSlabSizeBytes(SLAB_SIZE);
        configuration.diskPageSizeBytes(PAGE_SIZE);
        cacheObserver = new CacheObserver();
        pageMemoryObserver = new PageMemoryObserver();
        cache = new ImmutableItemCache<>(CACHE_CAPACITY, cacheObserver);
        pageReader = new PageManager(configuration);
        random = new Random(419);
    }

    @Test
    public void testOutOfPages() throws IOException, InterruptedException
    {
        // Use all available pages
        for (int i = 0; i < CACHE_CAPACITY; i++) {
            int r = abs(random.nextInt());
            TestId id = new TestId(r, r);
            cache.find(id, pageReader);
        }
        // Page memory should be full
        assertEquals(pageMemoryObserver.pagesInUse, CACHE_CAPACITY);
        // Attempt to take next page should fail
        try {
            int r = abs(random.nextInt());
            TestId id = new TestId(r, r);
            cache.find(id, pageReader);
            fail();
        } catch (ImmutableItemCacheError e) {
        }
    }

    @Test
    public void testPageReplacement() throws IOException, InterruptedException
    {
        // Use all available pages
        for (int i = 0; i < CACHE_CAPACITY; i++) {
            int r = abs(random.nextInt());
            TestId id = new TestId(r, r);
            TestPage page = cache.find(id, pageReader);
        }
        assertEquals(pageMemoryObserver.pagesInUse, CACHE_CAPACITY);
        // Keep loading pages, evicting pages and reusing memory
        final int N = 1000;
        for (int i = 0; i < N; i++) {
            TestPage evictable = randomPage(cacheObserver.pages);
            evictable.okToEvict(true);
            int r = abs(random.nextInt());
            TestId id = new TestId(r, r);
            cache.find(id, pageReader);
            assertSame(evictable, cacheObserver.lastEvicted);
        }
        for (Map.Entry<TestId, TestPage> entry : cacheObserver.pages.entrySet()) {
            TestId id = entry.getKey();
            TestPage page = entry.getValue();
            byte signature = id.signature();
            ByteBuffer buffer = page.buffer();
            assertEquals(PAGE_SIZE, buffer.remaining());
            for (int i = 0; i < PAGE_SIZE; i++) {
                assertEquals(signature, buffer.get(buffer.position() + i));
            }
        }
    }

    private TestPage randomPage(Map<TestId, TestPage> pages)
    {
        Iterator<TestPage> iterator = pages.values().iterator();
        int r = random.nextInt(pages.size());
        for (int i = 0; i < r; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    private static final int PAGE_SIZE = 5;
    private static final int CACHE_CAPACITY = 10;
    private static final int CACHE_SIZE = CACHE_CAPACITY * PAGE_SIZE;
    private static final int SLAB_SIZE = CACHE_SIZE / 2;

    private CacheObserver cacheObserver;
    private PageMemoryObserver pageMemoryObserver;
    private ImmutableItemCache<TestId, TestPage> cache;
    private PageManager pageReader;
    private Random random;

    private class PageManager implements ImmutableItemManager<TestId, TestPage>
    {
        @Override
        public TestPage getItemForCache(TestId id) throws IOException, InterruptedException
        {
            ByteBuffer buffer = pageMemoryManager.takePageBuffer();
            assertEquals(PAGE_SIZE, buffer.remaining());
            byte signature = id.signature();
            for (int i = 0; i < PAGE_SIZE; i++) {
                buffer.put(signature);
            }
            buffer.position(0);
            return new TestPage(id, buffer);
        }

        @Override
        public void cleanupItemEvictedFromCache(TestPage testPage) throws IOException, InterruptedException
        {
            pageMemoryManager.returnPageBuffer(testPage.buffer());
        }

        public PageManager(Configuration configuration)
        {
            pageMemoryManager = new SubAllocatingPageMemoryManager(configuration, pageMemoryObserver);
        }

        private PageMemoryManager pageMemoryManager;
    }

    private static class CacheObserver implements CacheEntryList.Observer<TestId, TestPage>
    {
        @Override
        public void adding(TestPage page)
        {
            pages.put(page.id(), page);
        }

        @Override
        public void evicting(TestPage victim)
        {
            pages.remove(victim.id());
            lastEvicted = victim;
        }

        Map<TestId, TestPage> pages = new HashMap<TestId, TestPage>();
        TestPage lastEvicted;
    }

    private static class PageMemoryObserver implements SubAllocatingPageMemoryManager.Observer
    {
        @Override
        public void takePageBuffer(int slabId, int offset)
        {
            pagesInUse++;
            lastTakenSlabId = slabId;
            lastTakenOffset = offset;
        }

        @Override
        public void returnPageBuffer(int slabId, int offset)
        {
            pagesInUse--;
            lastReturnedSlabId = slabId;
            lastReturnedOffset = offset;
        }

        int pagesInUse = 0;
        int lastTakenSlabId;
        int lastTakenOffset;
        int lastReturnedSlabId;
        int lastReturnedOffset;
    }
}
