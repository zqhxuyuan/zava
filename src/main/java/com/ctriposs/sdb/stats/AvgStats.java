package com.ctriposs.sdb.stats;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yqdong
 *
 */
public class AvgStats {

    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicLong total = new AtomicLong(0);
    private final AtomicLong min = new AtomicLong(0);
    private final AtomicLong max = new AtomicLong(0);

    public void addValue(long value) {
        count.getAndIncrement();
        total.getAndAdd(value);

        while (true) {
            long min = this.min.get();
            if (min == 0 || min > value) {
                if (this.min.compareAndSet(min, value)) {
                    break;
                }
            } else {
                break;
            }
        }

        while (true) {
            long max = this.max.get();
            if (max == 0 || max < value) {
                if (this.max.compareAndSet(max, value)) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    public int getCount() {
        return count.get();
    }

    public long getMin() {
        return min.get();
    }

    public long getMax() {
        return max.get();
    }

    public long getAvg() {
        int c = count.get();
        long t = total.get();
        return c == 0 ? 0 : t / c;
    }
}
