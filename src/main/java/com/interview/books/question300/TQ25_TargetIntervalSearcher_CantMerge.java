package com.interview.books.question300;

import com.interview.leetcode.utils.Interval;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 14-12-16
 * Time: 上午9:59
 */
public class TQ25_TargetIntervalSearcher_CantMerge {
    static Comparator<Interval> comparator = new Comparator<Interval>() {
        @Override
        public int compare(Interval o1, Interval o2) {
            if (o1.start < o2.start) return -1;
            else if (o1.start > o2.start) return 1;
            else return 0;
        }
    };

    Interval[] source;
    int[] maxEnd;

    //O(NlgN)
    public TQ25_TargetIntervalSearcher_CantMerge(Interval[] source){
        this.source = source;
        Arrays.sort(this.source, comparator);
        maxEnd = new int[source.length];
        findMaxEnd(source, maxEnd, 0, source.length - 1);
    }

    //Time: O(N)
    public Integer findMaxEnd(Interval[] source, int[] maxEnd, int low, int high){
        if(low > high) return null;
        else if(low == high) maxEnd[low] = source[low].end;
        else {
            int mid = low + (high - low)/2;
            Integer leftMaxEnd = findMaxEnd(source, maxEnd, low, mid - 1);
            Integer rightMaxEnd = findMaxEnd(source, maxEnd, mid + 1, high);
            maxEnd[mid] = source[mid].end;
            if(leftMaxEnd != null) maxEnd[mid] = Math.max(maxEnd[mid], leftMaxEnd);
            if(rightMaxEnd != null) maxEnd[mid] = Math.max(maxEnd[mid], rightMaxEnd);
        }
        return maxEnd[low];
    }

    public boolean isCover(Interval source, Interval target){
        if(source.start <= target.start && source.end >= target.end) return true;
        else return false;
    }

    //Time: O(lgN)
    public boolean cover(Interval target){
        //return cover(target, 0, source.length - 1);
        int low = 0;
        int high = source.length - 1;
        while(low <= high){
            if(low == high) return isCover(source[low], target);
            int mid = low + (high - low)/2;
            if(isCover(source[mid], target)) return true;
            else if(maxEnd[mid] < target.end) return false;
            else {
                int left = low + (mid - 1 - low)/2;
                int right = mid + 1 + (high - mid - 1)/2;
                if(left != mid && maxEnd[left] >= target.end){ //have left and maxEnd[left] >= target, left have cover.
                    high = mid - 1;
                } else if(source[mid].start <= target.start && right != mid){    //start <= cur.start, can go right.
                    low = mid + 1;
                } else {
                    break;
                }
            }
        }
        return false;
    }

    public static void main(String[] args){

        Interval[] source = new Interval[]{new Interval(2,3), new Interval(1,2), new Interval(8,10), new Interval(4,6)};
        //after merge: 1,3 4,6, 8,10
        TQ25_TargetIntervalSearcher_CantMerge searcher = new TQ25_TargetIntervalSearcher_CantMerge(source);

        System.out.println(searcher.cover(new Interval(1,6))) ;  //false
        System.out.println(searcher.cover(new Interval(1,3))) ;  //false
        System.out.println(searcher.cover(new Interval(5,6))) ;  //true
        System.out.println(searcher.cover(new Interval(9,10))) ; //true
    }
}
