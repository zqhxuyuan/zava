package com.interview.books.question300;

import com.interview.leetcode.utils.Interval;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 上午10:59
 */
public class TQ4_IntervalMaxOverlap {
    static Comparator<Interval> comparator = new Comparator<Interval>() {
        @Override
        public int compare(Interval o1, Interval o2) {
            return o1.start - o2.start;
        }
    };

    public int maxOverlap(Interval[] intervals){
        if(intervals.length == 0) return 0;
        Arrays.sort(intervals, comparator);
        int maxOveralp = 0;
        int maxEnd = intervals[0].end;
        for(int i = 1; i < intervals.length; i++){
            int overlap = Math.min(maxEnd, intervals[i].end) - intervals[i].start;
            maxOveralp = Math.max(maxOveralp, overlap);
            maxEnd = Math.max(maxEnd, intervals[i].end);
        }
        return maxOveralp;
    }

    public static void main(String[] args){
        TQ4_IntervalMaxOverlap finder = new TQ4_IntervalMaxOverlap();
        Interval[] intervals = new Interval[5];
        intervals[3] = new Interval(1,5);
        intervals[2] = new Interval(3,4);
        intervals[0] = new Interval(4,11);
        intervals[1] = new Interval(5,8);
        intervals[4] = new Interval(7,11);
        System.out.println(finder.maxOverlap(intervals));
    }
}
