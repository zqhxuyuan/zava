package com.interview.algorithms.array;

import com.interview.utils.ConsoleReader;

/**
 * Created_By: zouzhile
 * Date: 2/21/14
 * Time: 10:03 AM
 *
 * You are given an unsorted array of integers that contain duplicate numbers.
 * Only one number is duplicated odd number of duplications,
 * other numbers are repeated even number of duplications. Find this number.
 */
public class C4_25_FindOddOccurrenceNumber {

    public static int find(int[] array) {
        int value = 0;
        for(int element : array)
            value = value ^ element;
        return value;
    }

    public static void main(String[] args) {
        ConsoleReader reader = new ConsoleReader();
        int[] array = reader.readIntItems();
        System.out.print("Odd occurrence number: " + find(array));
    }
}
