package com.interview.books.ninechapter;

import com.interview.basics.search.SegmentTree;

/**
 * Created_By: stefanie
 * Date: 15-1-19
 * Time: 下午8:07
 */
public class NC8_SubarrayGCD_SegmentTree {
    SegmentTree.Operator<Integer> operator = new SegmentTree.Operator<Integer>() {
        @Override
        public Integer operate(Integer i, Integer j) {
            if (j > i) return operate(j, i);
            while (i % j != 0) {
                int mod = i % j;
                i = j;
                j = mod;
            }
            return j;
        }

        @Override
        public Integer init(int idx, Integer[] input) {
            return input[idx];
        }
    };
    SegmentTree<Integer> tree;
    public NC8_SubarrayGCD_SegmentTree(Integer[] A) {
        tree = new SegmentTree(A, operator);
    }

    public Integer gcd(int low, int high){
        return tree.query(low, high);
    }

    public static void main(String[] args){
        Integer[] array = new Integer[]{2,6,12,24,18,78};
        NC8_SubarrayGCD_SegmentTree gcder = new NC8_SubarrayGCD_SegmentTree(array);
        System.out.println(gcder.gcd(0, 5));  //2
        System.out.println(gcder.gcd(2, 3));  //12
        System.out.println(gcder.gcd(1, 4));  //6
    }

}
