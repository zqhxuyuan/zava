package com.interview.leetcode.arrays;

import com.interview.leetcode.utils.Interval;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 上午7:20
 *
 * Given a collection of intervals:
 * 1. merge all overlapping intervals.  {@link #merge}
 *      For example, given [1,3],[2,6],[8,10],[15,18], return [1,6],[8,10],[15,18].
 * 2. the given intervals are non-overlapping, insert a new interval into the intervals (merge if necessary).     {@link #merge}
 *      You may assume that the intervals were initially sorted according to their start times.
 *      Example 1: given intervals [1,3],[6,9], insert and merge [2,5] in as [1,5],[6,9].
 *      Example 2: given [1,2],[3,5],[6,7],[8,10],[12,16], insert and merge [4,9] in as [1,2],[3,10],[12,16].
 *      This is because the new interval [4,9] overlaps with [3,5],[6,7],[8,10].
 * 3. given a interval, search if there is a interval in the collection cover the given interval.
 *      Example 1: given [1,2],[3,10],[12,16], search [4,6] should return true, search [4,15] should return false.
 *
 * Tricks:
 *  1. using start point, to sort or partition the interval.
 *  2. define a clear case when will overlap and when would not
 *          overlap:    cur.start <= it.end || cur.end >= it.start
 *          no overlap: cur.start > it.end || cur.end < it.start
 *  3. binary search tree extension for intervals
 */
public class IntervalOperation {
    static Comparator<Interval> comparator = new Comparator<Interval>(){
        @Override
        public int compare(Interval o1, Interval o2) {
            if(o1.start == o2.start)    return o1.end - o2.end;
            else return o1.start - o2.start;
        }
    };

    /**
     * 1. at first, sort the interval based on start point
     * 2. keep a current interval, and if have overlap (next.start <= cur.end), update cur.end to the max(cur.end, next.end)
     *      if no overlap found, add cur to merged list and assign next to cur.
     */
    public static List<Interval> merge(List<Interval> intervals) {
        List<Interval> merged = new ArrayList<Interval>();
        if(intervals == null || intervals.size() == 0) return merged;
        Collections.sort(intervals, comparator);
        Interval current = intervals.get(0);
        for(int i = 1; i < intervals.size(); i++){
            Interval interval = intervals.get(i);
            if(interval.start <= current.end) current.end = Math.max(current.end, interval.end);
            else {
                merged.add(current);
                current = interval;
            }
        }
        merged.add(current);
        return merged;
    }

    /**
     * iterate on the intervals,
     *  1. no overlapping and place before newInterval (cur.end < newInterval.start) continue
     *  2. have overlapping, update newInterval.start to min and newInterval.end to max and remove cur from list
     *  3. no overlapping and place after newInterval (cur.start > newInterval.end), so should place newInterval before cur
     * Remember to check the newInterval as the last element
     */
    public static List<Interval> insert(List<Interval> intervals, Interval newInterval) {
        if(intervals == null) intervals = new ArrayList<Interval>();

        int offset = 0;
        Iterator<Interval> itr = intervals.iterator();
        while(itr.hasNext()){
            Interval cur = itr.next();
            if(cur.end < newInterval.start) offset++;
            else if(cur.start > newInterval.end) break;
            else {
                newInterval.start = Math.min(newInterval.start, cur.start);
                newInterval.end = Math.max(newInterval.end, cur.end);
                itr.remove();
            }
        }
        if(offset >= intervals.size()) intervals.add(newInterval);
        else intervals.add(offset, newInterval);
        return intervals;
    }

    /**
     * create a interval binary search tree
     *  creating the tree by hold the maxEnd in its sub-tree
     *  during binarysearch, if left.maxEnd > target.end, it should have a sol in a node in left can cover it.
     *                    else search in right
     *  build tree: O(nlogn)
     *  binarysearch:  O(lgn)
     */
    public static Interval search(List<Interval> intervals, Interval target){
        IntervalBSTNode root = createIntervalBST(intervals);
        return search(root, target);
    }


    static class IntervalBSTNode{
        Interval interval;
        IntervalBSTNode left;
        IntervalBSTNode right;
        int maxEnd;

        IntervalBSTNode(Interval interval) {
            this.interval = interval;
            this.maxEnd = interval.end;
        }
    }


    protected static IntervalBSTNode createIntervalBST(List<Interval> intervals) {
        IntervalBSTNode root = null;
        for(Interval interval : intervals) root = insert(root, interval);
        return root;
    }

    protected static IntervalBSTNode insert(IntervalBSTNode node, Interval interval){
        if(node == null) return new IntervalBSTNode(interval);
        if(interval.start == node.interval.start && interval.end == node.interval.end) return node;
        else if(interval.start <= node.interval.start)  node.left = insert(node.left, interval);
        else node.right = insert(node.right, interval);
        if(node.maxEnd < interval.end) node.maxEnd = interval.end;
        return node;
    }

    protected static Interval search(IntervalBSTNode node, Interval interval){
        if(node == null) return null;
        else if(node.interval.start <= interval.start && node.interval.end >= interval.end) return node.interval;
        else if(node.left != null && node.left.maxEnd >= interval.end) return search(node.left, interval);
        return search(node.right, interval);
    }

    class MissingRange {

        public List<String> findMissingRanges(int[] vals, int start, int end) {
            List<String> miss = new ArrayList<>();
            if(vals == null || vals.length == 0){
                miss.add(getRange(start, end));
                return miss;
            }
            if(vals[0] != start) miss.add(getRange(start, vals[0] - 1));
            for(int i = 1; i < vals.length; i++){
                if(vals[i] == vals[i - 1] + 1) continue;
                miss.add(getRange(vals[i - 1] + 1, vals[i] - 1));
            }
            if(vals[vals.length - 1] != end)  miss.add(getRange(vals[vals.length - 1] + 1, end));
            return miss;
        }

        public String getRange(int begin, int end){
            return begin == end? begin + "" : begin + "->" + end;
        }
    }

}
