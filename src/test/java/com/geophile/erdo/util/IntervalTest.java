/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import junit.framework.Assert;
import org.junit.Test;

import java.util.*;

public class IntervalTest
{
    @Test
    public void testComparisons()
    {
        // One interval inside another
        check(interval(4, 4), interval(4, 4), 0);
        check(interval(4, 4), interval(3, 4), 0);
        check(interval(4, 4), interval(4, 5), 0);
        check(interval(3, 5), interval(4, 4), 0);
        check(interval(3, 4), interval(4, 4), 0);
        check(interval(4, 5), interval(4, 4), 0);
        check(interval(3, 5), interval(4, 4), 0);
        // Disjoint intervals
        check(interval(3, 4), interval(4, 5), -1);
        check(interval(4, 5), interval(3, 4), 1);
        // Not comparable intervals
        checkNotComparable(interval(3, 5), interval(4, 6));
        checkNotComparable(interval(4, 6), interval(3, 5));
    }

    @Test
    public void testMapOfDisjointIntervals()
    {
        for (int intervalSize = 1; intervalSize <= 5; intervalSize++) {
            List<Interval> control = new ArrayList<Interval>();
            SortedMap<Interval, Interval> map = new TreeMap<Interval, Interval>();
            for (int i = 0; i < 100; i++) {
                Interval interval = interval(i * intervalSize, (i + 1) * intervalSize - 1);
                control.add(interval);
                map.put(interval, interval);
            }
            Assert.assertEquals(control.size(), map.size());
            Iterator<Interval> controlScan = control.iterator();
            Iterator<Interval> mapScan = map.keySet().iterator();
            while (controlScan.hasNext() && mapScan.hasNext()) {
                Interval controlInterval = controlScan.next();
                Interval mapInterval = mapScan.next();
                Assert.assertEquals(controlInterval, mapInterval);
                mapInterval = map.get(controlInterval);
                Assert.assertEquals(controlInterval, mapInterval);
            }
            Assert.assertTrue(!controlScan.hasNext());
            Assert.assertTrue(!mapScan.hasNext());
        }
    }

    private void check(Interval x, Interval y, int expected)
    {
        Assert.assertEquals(expected, sign(x.compareTo(y)));
    }

    private void checkNotComparable(Interval x, Interval y)
    {
        try {
            x.compareTo(y);
            Assert.assertTrue(false);
        } catch (AssertionError e) {
            // expected
        }
    }

    private Interval interval(long min, long max)
    {
        return new Interval(min, max);
    }

    private int sign(int x)
    {
        return x < 0 ? -1 : x > 0 ? 1 : 0;
    }
}
