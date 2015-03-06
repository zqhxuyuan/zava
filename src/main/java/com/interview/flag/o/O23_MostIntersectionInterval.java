package com.interview.flag.o;

import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.leetcode.utils.Interval;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 15-2-3
 * Time: 上午11:12
 */
public class O23_MostIntersectionInterval {

    Comparator<Interval> sortComparator = new Comparator<Interval>() {
        @Override
        public int compare(Interval o1, Interval o2) {
            if(o1.start == o2.start) return o1.end - o1.end;
            else return o1.start - o2.start;
        }
    };
    Comparator<Interval> heapComparator = new Comparator<Interval>() {
        @Override
        public int compare(Interval o1, Interval o2) {
            if(o1.end == o2.end) return o1.start - o2.start;
            else return o1.end - o2.end;
        }
    };

    public int[] maxIntersection(Interval[] intervals){
        int max = 0;
        int[] interval = new int[3];
        Arrays.sort(intervals, sortComparator);
        PriorityQueue<Interval> heap = new PriorityQueue(intervals.length, heapComparator);
        BinarySearchTree<Integer> passed = new BinarySearchTree();

        for(int i = 0; i <= intervals.length; i++){
            int end = i == intervals.length? Integer.MAX_VALUE : intervals[i].start;
            while(!heap.isEmpty() && heap.peek().end < end){
                Interval it = heap.poll();
                int count = heap.size() + (passed.size() - passed.rank(it.start));
                if(count > max){
                    max = count;
                    interval[0] = max;
                    interval[1] = it.start;
                    interval[2] = it.end;
                }
                passed.insert(it.end);
            }
            if(i < intervals.length){
                heap.add(intervals[i]);
            }
        }
        return interval;
    }

    public static void main(String[] args){
        O23_MostIntersectionInterval finder = new O23_MostIntersectionInterval();
        Interval[] intervals = new Interval[5];
        intervals[0] = new Interval(0, 4);
        intervals[1] = new Interval(1, 3);
        intervals[2] = new Interval(4, 6);
        intervals[3] = new Interval(2, 8);
        intervals[4] = new Interval(5, 8);

        int[] interval = finder.maxIntersection(intervals);
        System.out.printf("Max overlap with %d other interval: (%d, %d)\n", interval[0], interval[1], interval[2]);

    }
}
