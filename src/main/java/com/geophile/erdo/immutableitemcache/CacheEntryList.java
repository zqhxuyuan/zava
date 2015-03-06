/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;


// TODO: Could be package private except for referenceCountDistribution(), which is not essential
public class CacheEntryList<ID, ITEM extends CacheEntry<ID, ITEM>>
{
    public int size()
    {
        return size;
    }

    public void addItem(ITEM item)
    {
        assert item != null;
        assert !item.placeholder();
        observer.adding(item);
        item.recentAccess(true);
        // In a non-empty list, the new item goes behind clockHand. Since it's newest, it's probably
        // best to consider it for eviction last.
        switch (size) {
            case 0:
                assert clockHand == null;
                assert previous == null;
                clockHand = item;
                item.next(clockHand);
                break;
            case 1:
                assert clockHand != null;
                assert previous == null;
                clockHand.next(item);
                item.next(clockHand);
                previous = item;
                break;
            default:
                assert clockHand != null;
                assert previous != null;
                item.next(clockHand);
                previous.next(item);
                previous = item;
                break;
        }
        size++;
    }

    public ITEM takeItemToEvict()
    {
        ITEM victim = null;
        int count = 0;
        int limit = 2 * size() + 1; // In a second pass, recentAccess will be false for all entries
        if (clockHand != null) {
            ITEM c = clockHand;
            do {
                c.recentAccess(false);
                previous = c;
                c = c.next();
                if (++count > limit) {
                    logReferenceCounts();
                    throw new ImmutableItemCacheError("Unable to evict from cache!");
                }
            } while (c.recentAccess() || !c.okToEvict());
            clockHand = c;
            victim = clockHand;
            observer.evicting(victim);
            // Remove the victim
            switch (size) {
                case 0:
                    assert false;
                    break;
                case 1:
                    clockHand = null;
                    previous = null;
                    break;
                case 2:
                    clockHand = previous;
                    clockHand.next(clockHand);
                    previous = null;
                    break;
                default:
                    clockHand = clockHand.next();
                    previous.next(clockHand);
                    break;
            }
            size--;
        }
        return victim;
    }

    public CacheEntryList(Observer<ID, ITEM> observer)
    {
        this.observer = observer;
    }

    // For testing

    public Map<Integer, Integer> referenceCountDistribution()
    {
        Map<Integer, Integer> map = new TreeMap<>();
        if (clockHand != null) {
            ITEM c = clockHand;
            do {
                int referenceCount = c.referenceCount();
                Integer occurrences = map.get(referenceCount);
                if (occurrences == null) {
                    occurrences = 0;
                }
                map.put(referenceCount, occurrences + 1);
                c = c.next();
            } while (c != clockHand);
        }
        return map;
    }

    void clear()
    {
        size = 0;
        clockHand = null;
        previous = null;
    }

    // For use by this class

    private void logReferenceCounts()
    {
        LOG.log(Level.SEVERE,
                "Unable to evict from cache of size {0}. Reference count distibution:",
                size());
        Map<Integer, Integer> distribution = referenceCountDistribution();
        for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
            int referenceCount = entry.getKey();
            int occurrences = entry.getValue();
            LOG.log(Level.SEVERE, "    {0}: {1}", new Object[]{referenceCount, occurrences});
        }
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(CacheEntryList.class.getName());

    // Object state

    private final Observer<ID, ITEM> observer;
    private volatile int size = 0;
    // clockHand is the clock hand of the clock algorithm. It is null only if and only if size = 0.
    // previous trails the clock hand, so that the linked list can be maintained. It is null if and only
    // if size = 1.
    private volatile ITEM clockHand;
    private volatile ITEM previous;

    // Inner classes

    public interface Observer<ID, ITEM extends CacheEntry<ID, ITEM>>
    {
        void adding(ITEM item);
        void evicting(ITEM victim);
    }
}
