/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.util.Interval;

import java.util.*;

public class TimestampSet implements Iterable<Interval>
{
    // Object interface

    public synchronized String toString()
    {
        flush();
        StringBuilder buffer = new StringBuilder();
        if (empty()) {
            buffer.append("-");
        } else {
            boolean first = true;
            for (Interval interval : intervals) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(',');
                }
                buffer.append(interval.min());
                if (interval.max() > interval.min()) {
                    buffer.append('-');
                    buffer.append(interval.max());
                }
            }
        }
        return buffer.toString();
    }

    // Iterable interface

    public Iterator<Interval> iterator()
    {
        return intervals().iterator();
    }

    // TimestampSet interface

    public static TimestampSet consolidate(List<TimestampSet> transactionTimestampsList)
    {
        List<Interval> intervals = new ArrayList<>();
        for (TimestampSet transactionTimestamps : transactionTimestampsList) {
            transactionTimestamps.flush();
            intervals.addAll(transactionTimestamps.intervals);
        }
        Collections.sort(intervals, INTERVAL_COMPARATOR);
        TimestampSet consolidated = new TimestampSet();
        for (Interval interval : intervals) {
            consolidated.append(interval.min(), interval.max());
        }
        return consolidated;
    }

    public TimestampSet()
    {
    }

    public TimestampSet(long timestamp)
    {
        intervals.add(new Interval(timestamp));
    }

    public void append(Long timestamp)
    {
        append(timestamp, timestamp);
    }

    public synchronized void append(long minTimestamp, long maxTimestamp)
    {
        if (min == -1L && max == -1L) {
            min = minTimestamp;
            max = maxTimestamp;
        } else if (minTimestamp == max + 1) {
            max = maxTimestamp;
        } else if (minTimestamp > max + 1) {
            intervals.add(new Interval(min, max));
            min = minTimestamp;
            max = maxTimestamp;
        } else if (minTimestamp <= max) {
            assert false;
        }
    }

    public TimestampSet union(TimestampSet that)
    {
        List<TimestampSet> transactionTimestampsList = new ArrayList<>();
        transactionTimestampsList.add(this);
        transactionTimestampsList.add(that);
        return consolidate(transactionTimestampsList);
    }

    public TimestampSet minus(TimestampSet that)
    {
        TimestampSet minus = new TimestampSet();
        this.flush();
        that.flush();
        Iterator<Interval> keepIterator = intervals.iterator();
        Iterator<Interval> removeIterator = that.intervals.iterator();
        Interval keep = keepIterator.hasNext() ? keepIterator.next() : null;
        Interval remove = removeIterator.hasNext() ? removeIterator.next() : null;
        while (keep != null && remove != null) {
            if (keep.min() < remove.min()) {
                if (keep.max() >= remove.min()) {
                    // keep:    |-----------------|
                    // remove:         |---...
                    if (keep.min() <= remove.min() - 1) {
                        minus.append(keep.min(), remove.min() - 1);
                    }
                    keep = new Interval(remove.min(), keep.max());
                } else {
                    // keep:    |-----|
                    // remove:         |---...
                    minus.append(keep.min(), keep.max());
                    keep = keepIterator.hasNext() ? keepIterator.next() : null;
                }
            } else { // keep.min() >= remove.min()
                if (keep.min() > remove.max()) {
                    // keep:            |-----------------|
                    // remove: |---|
                    remove = removeIterator.hasNext() ? removeIterator.next() : null;
                } else {
                    // keep:            |-----------------|
                    // remove: |-------------...
                    if (remove.max() + 1 <= keep.max()) {
                        keep = new Interval(remove.max() + 1, keep.max());
                    } else {
                        keep = keepIterator.hasNext() ? keepIterator.next() : null;
                    }
                }
            }
        }
        while (keep != null) {
            minus.append(keep.min(), keep.max());
            keep = keepIterator.hasNext() ? keepIterator.next() : null;
        }
        return minus;
    }

    public synchronized boolean empty()
    {
        flush();
        return intervals.isEmpty();
    }

    public synchronized long minTimestamp()
    {
        flush();
        assert !intervals.isEmpty();
        return intervals.get(0).min();
    }

    public synchronized long maxTimestamp()
    {
        flush();
        assert !intervals.isEmpty();
        return intervals.get(intervals.size() - 1).max();
    }

    public synchronized long maxDeletionTimestamp()
    {
        flush();
        long maxDeletionTimestamp = -1L;
        if (!intervals.isEmpty()) {
            Interval firstInterval = intervals.get(0);
            if (firstInterval.min() == 0) {
                maxDeletionTimestamp = firstInterval.max();
            }
        }
        return maxDeletionTimestamp;
    }

    public synchronized TimestampSet copy()
    {
        TimestampSet copy = new TimestampSet();
        copy.min = min;
        copy.max = max;
        copy.intervals.addAll(intervals);
        return copy;
    }

    // For use by this package

    synchronized List<Interval> intervals()
    {
        flush();
        return new ArrayList<>(intervals);
    }

    // For use by this class

    private void flush()
    {
        if (min != -1L && max != -1L) {
            intervals.add(new Interval(min, max));
            min = -1L;
            max = -1L;
        }
    }

    // Class state

    private static final Comparator<Interval> INTERVAL_COMPARATOR =
        new Comparator<Interval>()
        {
            public int compare(Interval x, Interval y)
            {
                long xMin = x.min();
                long xMax = x.max();
                long yMin = y.min();
                long yMax = y.max();
                if (xMax < yMin) {
                    return -1;
                } else if (xMin > yMax) {
                    return 1;
                } else if (xMin <= yMin && yMax <= xMax ||
                           yMin <= xMin && xMax <= yMax) {
                    assert false;
                    return 0;
                } else {
                    assert false;
                    return 0;
                }
            }
        };

    // Object state

    private long min = -1L;
    private long max = -1L;
    private List<Interval> intervals = new ArrayList<>();
}
