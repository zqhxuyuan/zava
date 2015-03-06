package com.interview.algorithms.array;

/**
 * Created_By: zouzhile
 * Date: 9/1/13
 * Time: 3:57 PM
 */
public class C4_21_ArrayElementFinder {

    /*
    Problem:
        Given a sorted array, write an algorithm to determine whether a given value target exists in the array
    Solution:
        Binary search
     */
    public static boolean C4_21_A_findElement(int[] array, int start, int end, int target) {
        int i = (start + end) / 2;
        if(start > end)
            return false;

        if(array[i] == target) {
            return true;
        } else if (target < array[i]) {
            return C4_21_A_findElement(array, start, i - 1, target);
        } else {
            return C4_21_A_findElement(array, i + 1, end, target);
        }

    }

    /*
    Problem:
        For an array whose values first increase and then decrease,
        write an algorithm to determine whether a given value target exists in the array
    Solution:
        Binary Search, but we need to differ between ascending and descending side
     */
    public static boolean C4_21_B_findElement(int[] array, int start, int end, int target) {
        int i = (start + end) / 2;
        if(start > end)
            return false;

        if(array[i] == target) {
            return true;
        } else if (array[i-1] <= array[i] && array[i+1] <= array[i]) {
            // special case: array[i] is the largest element and not equal to target
            return C4_21_B_findElement(array, start, i - 1, target) || C4_21_B_findElement(array, i+1, end, target);
        } else if (target < array[i]) {
            if(array[i-1] <= array[i]) // ascending side
                return C4_21_B_findElement(array, start, i - 1, target);
            else { // descending side
                return C4_21_B_findElement(array, i + 1, end, target);
            }
        } else {
            if(array[i-1] <= array[i]) // ascending side
                return C4_21_B_findElement(array, i + 1, end, target);
            else {
                return C4_21_B_findElement(array, start, i - 1, target);
            }
        }
    }

    /*
    Problem:
        There are two arrays A1 and A2 sorted in ascending order. The largest value of A2 is smaller than the smallest value of A1.
        Array A3 is formed by appending A2 after A1. Write an algorithm to check whether a given value target exists in array A3.
    Solution:
        Comparing current element C with the last element L, i.e. the largest element of A2:
              if C > L, then C belongs to A1
              if C < L, then C belongs to A2
              if C == L, then C is the first or last element of A3.

        Look on right side of C if
              C > L and (target > C or target < L)   (when C is in A1)     ---- Condition 1
              C < L and (target > C and target < L)   (when C is in A2)    ---- Condition 2
        Look on left side of C if
              C > L and (target < C and target > L)  (when C is in A1)     ---- Condition 3
              C < L and (target < C or target > L)   (when C is in A2)     ---- Condition 4
        else
              return false if target != last (current == traversed to the part of A3 on the edge and still can't find target)

    Note:
        The binary search is search from central to sides of the array.

     */
    public static boolean C4_21_C_findElement(int[] array, int start, int end, int target) {
        if(start > end)
            return false;

        int i = (start + end) / 2;
        int current = array[i];
        int last = array[array.length - 1];

        if(target == current)
            return true;

        if(current > last) {
             if (target > current || target < last)
                 return C4_21_C_findElement(array, start + 1, end, target);
             else
                 return C4_21_C_findElement(array, start, end - 1, target);
        } else if (current < last) {
             if (target > current && target < last)
                 return C4_21_C_findElement(array, start + 1, end, target);
             else
                 return C4_21_C_findElement(array, start, end - 1, target);
        } else {  // current == last
             // since we checked "target == current" on line 94,
             // This means current == last && target != current, return false.
             return false;
        }
    }

    public static void main(String[] args) {
        int[] c4_21_a_array = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 21, 31, 41, 51, 61, 71};
        System.out.println("C4_21_A, found 21 ? : " + C4_21_A_findElement(c4_21_a_array, 0, c4_21_a_array.length - 1, 21));
        System.out.println("C4_21_A, found 99 ? : " + C4_21_A_findElement(c4_21_a_array, 0, c4_21_a_array.length - 1, 99));
        System.out.println();

        int[] c4_21_b_array = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 21, 31, 41, 51, 61, 71, 62, 52, 42, 32, 22, 12};
        System.out.println("C4_21_B, found 21 ? : " + C4_21_B_findElement(c4_21_b_array, 0, c4_21_b_array.length - 1, 21));
        System.out.println("C4_21_B, found 99 ? : " + C4_21_B_findElement(c4_21_b_array, 0, c4_21_b_array.length - 1, 99));
        System.out.println();

        int[] c4_21_c_array = new int[] {8, 8, 8, 9, 10, 21, 31, 41, 51, 61, 71, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8};
        System.out.println("C4_21_C, found 8 ? : " + C4_21_C_findElement(c4_21_c_array, 0, c4_21_c_array.length - 1, 8));
        System.out.println("C4_21_C, found 21 ? : " + C4_21_C_findElement(c4_21_c_array, 0, c4_21_c_array.length - 1, 21));
        System.out.println("C4_21_C, found 99 ? : " + C4_21_C_findElement(c4_21_c_array, 0, c4_21_c_array.length - 1, 99));
    }
}
