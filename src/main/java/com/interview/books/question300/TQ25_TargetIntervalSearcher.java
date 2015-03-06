package com.interview.books.question300;

import com.interview.leetcode.utils.Interval;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 14-12-16
 * Time: 上午10:55
 */
public class TQ25_TargetIntervalSearcher {
    static Comparator<Interval> comparator = new Comparator<Interval>() {
        @Override
        public int compare(Interval o1, Interval o2) {
            if (o1.start < o2.start) return -1;
            else if (o1.start > o2.start) return 1;
            else return 0;
        }
    };

    Interval[] source;
    int length;
    //O(NlgN)
    public TQ25_TargetIntervalSearcher(Interval[] source){
        this.source = source;
        Arrays.sort(this.source, comparator);
        length = merge(this.source);
    }

    //Time: O(N)
    public int merge(Interval[] source){
        if(source.length <= 1) return source.length;
        int cur = 0;
        Interval current = source[0];
        for(int i = 1; i < source.length; i++){
            if(source[i].start > current.end){ //no overlap
                source[cur++] = current;
                current = source[i];
            } else {
                current.end = Math.max(current.end, source[i].end); //update end
            }
        }
        source[cur++] = current;
        return cur;
    }

    public boolean isCover(Interval source, Interval target){
        if(source.start <= target.start && source.end >= target.end) return true;
        else return false;
    }

    public boolean cover(Interval target){
        int low = 0;
        int high = length - 1;
        while(low <= high){
            if(low == high) return isCover(source[low], target);
            int mid = low + (high - low)/2;
            if(isCover(source[mid], target)) return true;
            else if(target.end < source[mid].start) high = mid - 1;
            else if(target.start > source[mid].end) low = mid + 1;
            else return false;
        }
        return false;
    }

    public static void main(String[] args){

        Interval[] source = new Interval[]{new Interval(2,3), new Interval(1,2), new Interval(8,10), new Interval(4,6)};
        //after merge: 1,3 4,6, 8,10
        TQ25_TargetIntervalSearcher searcher = new TQ25_TargetIntervalSearcher(source);

        System.out.println(searcher.cover(new Interval(1,6))) ; //false
        System.out.println(searcher.cover(new Interval(1,3))) ; //true
        System.out.println(searcher.cover(new Interval(5,6))) ; //true
        System.out.println(searcher.cover(new Interval(9,10))); //true
    }
}
