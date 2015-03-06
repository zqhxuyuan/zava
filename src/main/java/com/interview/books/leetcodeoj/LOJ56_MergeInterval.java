package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午7:25
 */
public class LOJ56_MergeInterval {
    public List<Interval> merge(List<Interval> intervals) {
        if(intervals.size() < 2) return intervals;
        Comparator<Interval> comparator = new Comparator<Interval>(){
            public int compare(Interval o1, Interval o2){
                return o1.start - o2.start;
            }
        };
        Collections.sort(intervals, comparator);
        List<Interval> merged = new ArrayList();
        Interval current = intervals.get(0);
        for(int i = 1; i < intervals.size(); i++){
            Interval interval = intervals.get(i);
            if(interval.start <= current.end){
                current.end = Math.max(current.end, interval.end);
            } else {
                merged.add(current);
                current = interval;
            }
        }
        merged.add(current);
        return merged;
    }
}
