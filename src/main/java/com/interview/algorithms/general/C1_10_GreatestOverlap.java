package com.interview.algorithms.general;

import com.interview.utils.models.Range;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-6-23
 * Time: 下午8:44
 *
 * Given a set of ranges, find the two ranges with the greatest overlap.
 *
 * Solution:
 *      when the ranges are sorted by start as r1, r2, r3, etc...
 *      a.  when r1.end < r2.end: the overlap of (r1,r3) can be no larger than (r2,r3)
 *                  r1 ---------------------------- r1
 *                       r2 ----------------------------- r2
 *                              r3 ---------- r3 ------ r3 ------- r3    (r1,r3) < (r2,r3) when r1.end < r2.end
 *
 *      b. when r1.end > r2.end: the overlap of (r2,r3) can be no larger than (r1,r3)
 *                  r1 ------------------------------- r1
 *                       r2 -------------- r2
 *                            r3 ---- r3 ---- r3 ---------  r3           (r2,r3) < (r1,r3) when r1.end > r2.end
 *
 *      so the max overlap always goes to (max_end, r3), so keep tracing the max_end
 */



public class C1_10_GreatestOverlap {


    static class MaxOverlapRange{
        public Range r1;
        public Range r2;
        public int overlap = 0;
    }

    public static MaxOverlapRange getGreatestOverlap(List<Range> ranges) {
        Collections.sort(ranges, new Comparator<Range>() {
            @Override
            public int compare(Range range1, Range range2) {
                return range1.start > range2.start ? 1 : -1;
            }
        });


        Iterator<Range> rangesItr = ranges.iterator();
        // initialize result
        MaxOverlapRange maxOverlap = new MaxOverlapRange();
        Range current = rangesItr.next();
        while(rangesItr.hasNext()) {
            Range next = rangesItr.next();
            int overlap = getOverlap(current, next);
            if(overlap > maxOverlap.overlap) {
                maxOverlap.r1 = current;
                maxOverlap.r2 = next;
                maxOverlap.overlap = overlap;
            }
            if(next.end > current.end)
                current = next;
        }
        return maxOverlap;
    }

    private static int getOverlap(Range r1, Range r2) {
        if(r2.end <= r1.end)
            return r2.end - r2.start;

        if(r2.start >= r1.end)
            return 0;

        return r1.end - r2.start;
    }

}
