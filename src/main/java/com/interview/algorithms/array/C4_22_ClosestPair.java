package com.interview.algorithms.array;

import com.interview.algorithms.sort.QuickSort;

/**
 * Closest Pair. Given an int array a[], find the closest two numbers A and B so that the absolute value |A-B|
 * is the smallest. The time complexity should be O(NlogN).
 * Created_By: zouzhile
 * Date: 1/16/14
 * Time: 4:12 PM
 */
public class C4_22_ClosestPair {

    class Pair {
        int value1, value2;

        public String toString(){
            return String.format("Closest Pair is (%s, %s)", value1, value2);
        }

    }

    public Pair find(int[] array) {
        System.out.print("Input Array: ");
        for(int value : array)
            System.out.print(value + " ");
        System.out.println();
        QuickSort sorter = new QuickSort();
        array = sorter.sort(array, 0, array.length - 1); // O(NLogN)

        Pair pair = new Pair();
        pair.value1 = array[0];
        pair.value2 = array[1];
        int diff = Math.abs(pair.value1 - pair.value2);

        /*
            array[i] <= array[i+1] and |array[i] - array[i+1]| is currently the smallest.

            Suppose array[i] * array[i+1] >= 0, then |array[i] - array[i+1]| < |array[i] - array[i+2]| is true.
            Suppose array[i] * array[i+1] < 0,
                 then we have array[i] < 0 and array[i+1] > 0
                 thus |array[i] - array[i+1]| < |array[i] - array[i+2]| is true.

         */
        for(int i = 1; i < array.length - 1; i ++) {  // O(N)
            if(Math.abs(array[i] - array[i+1]) < diff) {
                pair.value1 = array[i];
                pair.value2 = array[i+1];
                diff = Math.abs(pair.value1 - pair.value2);
            }
        }
        System.out.println();
        return pair;
    }

    public static void main(String[] args) {
        System.out.println(new C4_22_ClosestPair().find(new int[]{-9, -7, -3, 0, 2}));
        System.out.println(new C4_22_ClosestPair().find(new int[] {-9, -23, -3, 0, 1}));
        System.out.println(new C4_22_ClosestPair().find(new int[] {-9, -23, -3, 0, 1, 2}));
        System.out.println(new C4_22_ClosestPair().find(new int[] {-9, -23, -3, 0, 1, 0}));
    }
}
