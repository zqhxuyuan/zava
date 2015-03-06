/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class ImmutableItemCacheMultithreadedTest
{
    // Ignored because observer doesn't seem to work right in a multithreaded test, which this is.
    @Ignore
    @Test
    public void test() throws InterruptedException, IOException
    {
        assertTrue(CACHE_SIZE >= 2 * THREADS);
        CacheObserver cacheObserver = new CacheObserver();
        cache = new ImmutableItemCache<>(CACHE_SIZE, cacheObserver);
        // Load cache and mark one item evictable for each thread
        int id = 0;
        while (id < CACHE_SIZE) {
            Item item = cache.find(id(id), ITEM_READER);
            item.recentAccess(false);
            if (id < THREADS) {
                item.okToEvict(true);
                cacheObserver.expectedVictim = item;
            } else {
                item.okToEvict(false);
            }
            id++;
        }
        System.out.println("Cache loaded");
        TestThread[] threads = new TestThread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            threads[t] = new TestThread(t);
        }
        for (TestThread thread : threads) {
            thread.start();
        }
        List<Throwable> crashes = new ArrayList<>();
        for (TestThread thread : threads) {
            thread.join();
            if (thread.termination != null) {
                crashes.add(thread.termination);
            }
        }
        for (Throwable crash : crashes) {
            crash.printStackTrace();
        }
        assertTrue(crashes.isEmpty());
        List<Integer> expectedSorted = new ArrayList<>();
        for (Item item : cacheObserver.expected) {
            expectedSorted.add(item.id().value());
        }
        Collections.sort(expectedSorted);
        List<Integer> actualSorted = new ArrayList<>();
        for (Id actualId : cache.cacheContents().keySet()) {
            actualSorted.add(actualId.value());
        }
        Collections.sort(actualSorted);
        assertEquals(expectedSorted, actualSorted);
    }

    private Id id(int id)
    {
        return new Id(id);
    }

    private static final int CACHE_SIZE = 20;
    private static final int MAX_ID = CACHE_SIZE * 2;
    private static final int OPS = 10000;
    private static final int THREADS = 10;
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

    private ImmutableItemCache<Id, Item> cache;

    private static class CacheObserver implements CacheEntryList.Observer<Id, Item>
    {
        public void adding(Item item)
        {
            if (expected.isEmpty()) {
                item.okToEvict(true);
                expectedVictim = item;
            }
            expected.add(item);
/*
            System.out.println(String.format("%s: Cache after adding %s: %s",
                                             Thread.currentThread() .getName(), item, describeExpected()));
*/
        }

        public void evicting(Item victim)
        {
            assertNotNull(victim);
            assertNotNull(expectedVictim);
            assertEquals(expectedVictim.id().value(), victim.id().value());
            boolean removed = expected.remove(victim);
            assertTrue(removed);
            if (expected.isEmpty()) {
                expectedVictim = null;
            } else {
                // Pick another entry to evict
                int n = random.nextInt(CACHE_SIZE) + 1;
                expectedVictim = victim;
                for (int i = 0; i < n; i++) {
                    expectedVictim = expectedVictim.next();
                }
                if (victim.id().equals(expectedVictim.id())) {
                    expectedVictim = expectedVictim.next();
                }
                expectedVictim.okToEvict(true);
            }
/*
            System.out.println(String.format("%s: Cache after evicting %s: %s, next victim: %s",
                                             Thread.currentThread().getName(), victim, describeExpected(),
                                             expectedVictim));
*/
        }

        private String describeExpected()
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append('[');
            List<Item> sorted = new ArrayList<>(expected);
            Collections.sort(sorted);
            boolean first = true;
            for (Item item : sorted) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(", ");
                }
                buffer.append(item.id().value());
                if (item.okToEvict()) {
                    buffer.append('*');
                }
            }
            buffer.append(']');
            return buffer.toString();
        }

        private final Random random = new Random(419);
        public volatile Item expectedVictim;
        public Set<Item> expected = new HashSet<>();
    }

    private class TestThread extends Thread
    {
        public void run()
        {
            try {
                // Operate on cache, tracking expected contents
                for (int i = 0; i < OPS; i++) {
                    // Pick random id to read
                    Id id = id(random.nextInt(MAX_ID));
/*
                    System.out.println(String.format("%s: OP %s: find %s", getName(), i, id));
*/
                    // Get new cache item and check eviction
                    cache.find(id, ITEM_READER);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                termination = e;
            }
        }

        public TestThread(int threadId)
        {
            setName("t" + threadId);
            this.random = new Random(123456 + threadId);
        }

        private final Random random;
        public Throwable termination;
    }
}
