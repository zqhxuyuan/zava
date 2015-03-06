/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.util.Interval;

import java.util.SortedMap;
import java.util.TreeMap;

// Maps an interval of timestamps to a map containing the updates from the transactions with those timestamps.

public class TransactionOwners
{
    public synchronized TransactionUpdates find(Long timestamp)
    {
        assert timestamp != null;
        return intervalToUpdates.get(new Interval(timestamp));
    }

    public synchronized void add(TransactionUpdates updates)
    {
        for (Interval interval : updates.timestamps()) {
            TransactionUpdates replacedMap = intervalToUpdates.put(interval, updates);
            assert replacedMap == null : replacedMap;
        }
    }

    public synchronized void remove(TransactionUpdates updates)
    {
        for (Interval interval : updates.timestamps()) {
            TransactionUpdates removedMap = intervalToUpdates.remove(interval);
            assert removedMap == updates : interval;
        }
    }

    public synchronized TransactionOwners copy()
    {
        return new TransactionOwners(new TreeMap<>(intervalToUpdates));
    }

    // Intervals should cover [0, expectedMax] without overlap or holes.
    public synchronized void checkCoverage(long expectedMax)
    {
        long expectedMin = 0L;
        long observedMax = -1L;
        for (Interval interval : intervalToUpdates.keySet()) {
            if (interval.min() != expectedMin) {
                reportCorruption();
            }
            observedMax = interval.max();
            expectedMin = observedMax + 1;
        }
        if (observedMax != expectedMax) {
            reportCorruption();
        }
    }

    public TransactionOwners()
    {
        this(new TreeMap<Interval, TransactionUpdates>());
    }

    // For use by this class

    private void reportCorruption()
    {
        throw new Error(intervalToUpdates.keySet().toString());
    }

    private TransactionOwners(SortedMap<Interval, TransactionUpdates> intervalToUpdates)
    {
        this.intervalToUpdates = intervalToUpdates;
    }

    // Object state

    private final SortedMap<Interval, TransactionUpdates> intervalToUpdates;
}
