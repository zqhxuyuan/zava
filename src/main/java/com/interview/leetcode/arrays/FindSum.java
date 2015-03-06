package com.interview.leetcode.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-12
 * Time: 下午9:21
 *
 * Given an array S of N integers, find all unique combination of M elements in the array which gives the sum of K.
 *
 * 1. When M is 2: 2 Sum   {@link #sum2(int[], int)}
 * 2. When M is 3: 3 Sum   {@link #sum3(int[], int)}
 * 3. When M is 4: 4 Sum   {@link #sum4(int[], int)}
 * 4. find the sum of 3 elements which is closest to given K. {@link #closest3(int[], int)}
 *
 * From LeetCode:
 * https://oj.leetcode.com/problems/3sum/
 * https://oj.leetcode.com/problems/4sum/
 * https://oj.leetcode.com/problems/3sum-closest/
 *
 * Tricks:
 *  1. Simplify the question by settle one or more variables, and find the others. 3sum or 4sum
 *  2. Sort the array at first to find a quick and straight forward solution
 *  3. Skip the duplicate number when scanning array to avoid create duplicate solution.
 */
public class FindSum {

    /**
     * For binarysearch problem, if could use additional space,
     * using HashSet or HashMap to reduce the binarysearch time complexity to O(1)
     *
     * only visit once, put visited data in map.
     * Hash table: O(n) runtime, O(n) space
     */
    public int[] sum2One(int[] numbers, int target) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for(int i = 0; i < numbers.length; i++){
            int expected = target - numbers[i];
            if(map.containsKey(expected)){
                return new int[]{map.get(expected) + 1, i + 1};
            }
            map.put(numbers[i], i);
        }
        return new int[]{-1, -1};
    }

    public static List<List<Integer>> sum2(int[] num, int key) {
        Arrays.sort(num);
        return sum2(num, 0, key);
    }

    /**
     * Scan the sorted num from begin, to find 2 elements sum is key  O(N)
     * have 2 indexes begin and end, when the sum(begin, end)
     * == key, found a answer
     * to avoid duplication
     * end move left to the one which is not the same value of the old end
     * begin move right to the one which is not the same value of the old begin
     * > key, end move left one step
     * < key, begin move right one step
     * have a loop until the 2 indexes meet.
     */
    private static List<List<Integer>> sum2(int[] num, int begin, int key) {
        List<List<Integer>> sols = new ArrayList<>();
        int end = num.length - 1;
        while (begin < end) {
            int sum = num[begin] + num[end];
            if (sum == key) {
                List<Integer> sol = new ArrayList<>();
                sol.add(num[begin++]);
                sol.add(num[end--]);
                sols.add(sol);
                while (begin < end && num[begin] == num[begin - 1]) begin++;  //avoid duplication
                while (begin < end && num[end] == num[end + 1]) end--;  //avoid duplication
            } else if (sum > key) end--;
            else begin++;
        }
        return sols;
    }

    /**
     * specify one element, find the other 2 elements in the right part.  O(N^2)
     * every time select a new one which is different with previous one to avoid duplication
     */
    public static List<List<Integer>> sum3(int[] num, int key) {
        Arrays.sort(num);
        List<List<Integer>> sols = new ArrayList<>();
        for (int i = 0; i < num.length - 2; i++) {
            if (i > 0 && num[i] == num[i - 1]) continue;
            int target = key - num[i];
            List<List<Integer>> subsols = sum2(num, i + 1, target);
            for (List<Integer> sol : subsols) {
                sol.add(0, num[i]);
                sols.add(sol);
            }
        }
        return sols;
    }

    /**
     * specify two element, find the other 2 elements in the right part.    O(N^3)
     * every time select a new one which is different with previous one to avoid duplication
     */
    public static List<List<Integer>> sum4(int[] num, int key) {
        Arrays.sort(num);
        List<List<Integer>> sols = new ArrayList<>();
        for (int i = 0; i < num.length - 3; i++) {
            if (i > 0 && num[i] == num[i - 1]) continue;
            for (int j = i + 1; j < num.length - 2; j++) {
                if (j > i + 1 && num[j] == num[j - 1]) continue;
                int target = key - num[i] - num[j];
                List<List<Integer>> subsols = sum2(num, j + 1, target);
                for (List<Integer> sol : subsols) {
                    sol.add(0, num[j]);
                    sol.add(0, num[i]);
                    sols.add(sol);
                }
            }
        }
        return sols;
    }

    /**
     * keep tracking the closest for every combination found using 2 sum  O(N^2)
     */
    public static int closest3(int[] num, int key) {
        Arrays.sort(num);
        int closest = Integer.MAX_VALUE;
        for (int i = 0; i < num.length - 2; i++) {
            int j = i + 1;
            int k = num.length - 1;
            while (j < k) {
                int sum = num[i] + num[j] + num[k];
                if (sum == key) return sum;
                else {
                    if (Math.abs(sum - key) < Math.abs(closest)) closest = sum - key;
                    if (key > sum) j++;
                    else k--;
                }
            }
        }
        return closest + key;
    }
}
