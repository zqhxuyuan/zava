package com.interview.books.svinterview;

import com.interview.leetcode.utils.Interval;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 14-12-5
 * Time: 下午3:08
 */
public class SV2_OverlapInterval {

    public static int overlapNumber(Interval[] intervals){
        if (intervals.length < 2) return 0;
        Comparator<Interval> comparator = new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                if(o1 == null && o2 == null) return 0;
                if(o1 == null && o2 != null) return -1;
                if(o1 != null && o2 == null) return 1;
                return o1.start - o2.start;
            }
        };
        Arrays.sort(intervals, comparator);
        int overlap = 0;
        Interval cur = intervals[0];
        for(int j = 1; j < intervals.length; j++){
            if(intervals[j].start > cur.end){      //no overlap
                cur = intervals[j];
            } else {
                overlap++;
                cur.end = Math.max(cur.end, intervals[j].end);
            }
        }
        return overlap != 0? overlap + 1: 0;
    }

    public static void main(String[] args){
        Interval[] intervals = new Interval[4];
        intervals[0] = new Interval(1,5);
        intervals[1] = new Interval(10,15);
        intervals[2] = new Interval(5,10);
        intervals[3] = new Interval(20,30);
        System.out.println(overlapNumber(intervals));
    }
}
