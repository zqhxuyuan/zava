/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

import org.junit.Test;

import java.util.Random;

public class CacheEntryListTest
{
    @Test
    public void test() throws InterruptedException
    {
        for (int i = 0; i < N; i++) {
            Item item = new Item(new Id(i));
            item.okToEvict(i == 0);
            clock.addItem(item);
        }
        TestThread[] threads = new TestThread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            threads[t] = new TestThread();
        }
        for (TestThread thread : threads) {
            thread.start();
        }
        for (TestThread thread : threads) {
            thread.join();
        }
    }

    private static final int THREADS = 10;
    private static final int N = 10;
    private static final int OPS = 100000;

    private static CacheEntryList.Observer OBSERVER =
        new CacheEntryList.Observer<Id, Item>()
        {
            @Override
            public void adding(Item item)
            {
            }

            @Override
            public void evicting(Item victim)
            {
                int n = random.nextInt(N) + 1;
                Item nextVictim = victim;
                for (int i = 0; i < n; i++) {
                    nextVictim = nextVictim.next();
                }
                if (nextVictim == victim) {
                    nextVictim = nextVictim.next();
                }
                nextVictim.okToEvict(true);
            }

            private final Random random = new Random();
        };

    private final CacheEntryList<Id, Item> clock = new CacheEntryList<Id, Item>(OBSERVER);

    private class TestThread extends Thread
    {
        public void run()
        {
            for (int i = 0; i < OPS; i++) {
                synchronized (clock) {
                    Item item = clock.takeItemToEvict();
                    item.recentAccess(true);
                    item.okToEvict(false);
                    clock.addItem(item);
                }
            }
        }
    }
}
