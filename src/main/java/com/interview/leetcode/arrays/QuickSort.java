package com.interview.leetcode.arrays;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-11-29
 * Time: 下午5:08
 */
public class QuickSort {
    static class QuickSortThreeWay{
        protected void sort(int[] input, int low, int high) {
            if (low >= high) return;

            int i = low;
            int m = -1;
            for (int j = low + 1; j <= high; j++) {
                if (input[j] == input[low]) {
                    if (m == -1) m = i;
                    if (++m != j) ArrayUtil.swap(input, m, j);
                } else if (input[j] < input[low] && ++i != j) {
                    ArrayUtil.swap(input, i, j);
                    if (m > -1 && ++m< j) ArrayUtil.swap(input, m, j);
                }
            }
            if(low != i) ArrayUtil.swap(input, low, i);
            sort(input, low, i - 1);
            sort(input, m > -1 ? m + 1 : i + 1, high);
        }
    }


    static class QuickSortDualPivots{
        protected void sort(int[] input, int low, int high){
            if (low >= high) return;
            if(input[low] > input[high]) ArrayUtil.swap(input, low, high);

            int i = low;
            int m = -1;
            for (int j = low + 1; j < high; j++) {
                if (input[j] < input[low] && ++i != j) {
                    ArrayUtil.swap(input, i, j);
                    if (m > -1 && ++m < j) ArrayUtil.swap(input, m, j);
                } else if (input[j] >= input[low] && input[j] <= input[high]) {
                    if (m == -1) m = i;
                    if (++m != j) ArrayUtil.swap(input, m, j);
                }
            }
            if(low != i) ArrayUtil.swap(input, low, i);
            sort(input, low, i - 1);
            if(m != -1) {
                if(++m != high) ArrayUtil.swap(input, high, m);
                sort(input, i + 1, m - 1);
                sort(input, m + 1, high);
            } else {
                sort(input, i + 1, high);
            }
        }
    }
}
