package com.interview.leetcode.utils;

import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 上午7:21
 */
public class Interval {
    public int start;
    public int end;

    public Interval() {
    }

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public void print(){
        System.out.printf("[%d,%d]\n", start, end);
    }

    public static void print(List<Interval> intervals){
        for(Interval interval : intervals) interval.print();
    }
}
