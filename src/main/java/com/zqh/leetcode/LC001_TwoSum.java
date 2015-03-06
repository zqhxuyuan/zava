package com.zqh.leetcode;

import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * https://oj.leetcode.com/problems/two-sum/
 *
 Given an array of integers, find two numbers such that they add up to a specific target number.

 The function twoSum should return indices of the two numbers such that they add up to the target,
 where index1 must be less than index2. Please note that your returned answers (both index1 and index2) are not zero-based.

 You may assume that each input would have exactly one solution.

 Input: numbers={2, 7, 11, 15}, target=9
 Output: index1=1, index2=2
 */
public class LC001_TwoSum {
    public static void main(String[] args) {
        LC001_TwoSum solution = new LC001_TwoSum();
        P(solution.twoSum(new int[]{2, 5, 7, 11, 15}, 12));
        P(solution.twoSum2(new int[]{2, 5, 7, 11, 15}, 12));
        P(solution.twoSum_hashmap(new int[]{2, 5, 7, 11, 15}, 12));
        P(solution.twoSum_pointer(new int[]{2, 5, 7, 11, 15}, 12));
    }
    public static void P(int[] result){
        for(int r : result){
            System.out.print(r + " ");
        }
        System.out.println();
    }

    //https://github.com/tg123
    public int[] twoSum(int[] numbers, int target) {
        HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
        for(int i = 0; i < numbers.length; i++){
            m.put(target - numbers[i], i);
            //  2      5     7     11            target
            // {7,0}, {4,1},{2,2},{-2,3},...     9
            // {10,0},{7,1},{5,2},{1,3}          12
        }

        for(int i = 0; i < numbers.length; i++){
            Integer v = m.get(numbers[i]);
            // T=9  i,v=0,2
            // T=12 i,v=0,null; 1,2
            if(v != null && v != i){
                return new int[]{i + 1, v + 1};
            }
        }
        throw new RuntimeException();
    }

    //https://github.com/mengli
    private void quickSort(int[] numbers, int[] indexs, int start, int end) {
        if (start < end) {
            int p = partition(numbers, indexs, start, end);
            quickSort(numbers, indexs, start, p - 1);
            quickSort(numbers, indexs, p + 1, end);
        }
    }

    private int partition(int[] numbers, int[] indexs, int start, int end) {
        swap(numbers, (start + end) / 2, end);
        swap(indexs, (start + end) / 2, end);
        int x = numbers[end];
        int i = start - 1;
        for (int k = start; k < end; k++) {
            if (numbers[k] < x) {
                i++;
                swap(numbers, i, k);
                swap(indexs, i, k);
            }
        }
        swap(numbers, i + 1, end);
        swap(indexs, i + 1, end);
        return i + 1;
    }

    private void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    public int[] twoSum2(int[] numbers, int target) {
        int len = numbers.length;
        int[] indexs = new int[len];
        int[] ret = new int[2];
        for (int i = 0; i < len; i++) {
            indexs[i] = i + 1;
        }
        quickSort(numbers, indexs, 0, len - 1);
        int start = 0;
        int end = len - 1;
        while (start < end) {
            if (numbers[start] + numbers[end] == target) {
                ret[0] = Math.min(indexs[start], indexs[end]);
                ret[1] = Math.max(indexs[start], indexs[end]);
                break;
            } else if (numbers[start] + numbers[end] > target) {
                end--;
            } else {
                start++;
            }
        }
        return ret;
    }

    //http://www.ninechapter.com/solutions/two-sum/
    public int[] twoSum_hashmap(int[] numbers, int target) {
        HashMap<Integer, Integer> hs = new HashMap<Integer, Integer>();
        for(int i=0; i<numbers.length; i++){
            hs.put(numbers[i], i+1);
        }

        int[] a = new int[2];

        for(int i=0; i<numbers.length ; i++){
            if ( hs.containsKey( target - numbers[i] )){
                int index1 = i+1;
                int index2 = hs.get(target - numbers[i]);
                if (index1 == index2){
                    continue;
                }
                a[0] = index1;
                a[1] = index2;
                return a;
            }
        }
        return a;
    }
    // Can’t use the sort method here, since the question asks for indexes.
    public int[] twoSum_pointer(int[] numbers, int target) {
        Arrays.sort(numbers);
        int left = 0;
        int right = numbers.length - 1;
        int[] rst = new int[2];

        while( left < right){
            int sum = numbers[left] +  numbers[right];
            if( sum == target){
                rst[0] = left + 1;
                rst[1] = right + 1;
                break;
            }else if( sum < target){
                left++;
            }else{
                right--;
            }
        }
        return rst;
    }
}
