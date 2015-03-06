package com.interview.books.topcoder.array;

import com.interview.basics.search.SegmentTree;

/**
 * Created_By: stefanie
 * Date: 15-1-19
 * Time: 下午3:56
 *
 * Range Minimum Query is a very useful algorithm.
 *
 * Problem:
 *  Given an array A[0, N-1] find the position of  the element with the minimum value between two given indices.
 */
public class TCT_A1_RangeMinimumQuery {
    SegmentTree.Operator<Integer> operator = new SegmentTree.Operator<Integer>() {
        @Override
        public Integer operate(Integer a, Integer b) {
            return Math.min(a, b);
        }

        @Override
        public Integer init(int idx, Integer[] input) {
            return input[idx];
        }
    };
    SegmentTree<Integer> tree;
    public TCT_A1_RangeMinimumQuery(Integer[] array){
        tree = new SegmentTree(array, operator);
    }

    public Integer getMin(int low, int high){
        return tree.query(low, high);
    }

    public static void main(String[] args){
        Integer[] array = new Integer[]{2,4,3,2,3,5,1,2,5};
        TCT_A1_RangeMinimumQuery query = new TCT_A1_RangeMinimumQuery(array);
        System.out.println(query.getMin(1,5)); //2
        System.out.println(query.getMin(1,6)); //1
        System.out.println(query.getMin(4,5)); //3
    }

}
