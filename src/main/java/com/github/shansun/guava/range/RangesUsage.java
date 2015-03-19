package com.github.shansun.guava.range;

import com.google.common.collect.DiscreteDomains;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

/**
 * 数值区间
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-4
 */
public class RangesUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(Ranges.lessThan(10));

        System.out.println(Ranges.closed(1, 12));

        Range<Integer> range = Ranges.open(1, 20);

        for (Integer i : range.asSet(DiscreteDomains.integers())) {
            System.out.print(i);
        }
    }

}