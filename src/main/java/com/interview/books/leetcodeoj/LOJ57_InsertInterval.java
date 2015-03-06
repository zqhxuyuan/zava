package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.Interval;

import java.util.Iterator;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 上午10:39
 */
public class LOJ57_InsertInterval {
    //mark overlapped interval to newInterval, and remove overlapped one in intervals.
    //use Iterator to enable remove during scan
    //check index == intervals.size(), in this case intervals.add(newInterval), other's intervals.add(index, newInterval);
    public List<Interval> insert(List<Interval> intervals, Interval newInterval) {
        Iterator<Interval> iterator = intervals.iterator();
        int index = 0;
        while(iterator.hasNext()){
            Interval current = iterator.next();
            if(current.end < newInterval.start){
                index++;
            } else if(current.start > newInterval.end){
                break;
            } else {
                newInterval.start = Math.min(newInterval.start, current.start);
                newInterval.end = Math.max(newInterval.end, current.end);
                iterator.remove();
            }
        }
        if(index == intervals.size()) intervals.add(newInterval);
        else intervals.add(index, newInterval);
        return intervals;
    }
}
