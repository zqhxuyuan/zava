package com.interview.algorithms.array;

import com.interview.utils.ConsoleReader;

/**
 * Find the uniq amount of absolute values in a given sorted array
 * 
 * @author zouzhile
 *
 */
public class C4_15_UniqAbsoluteValueCounter {

    public int count(int[] array) {
        int count = 0;
        int begin = 0;
        int end = array.length - 1;

        while(begin <= end){
            int left = Math.abs(array[begin]);
            int right = Math.abs(array[end]);

            count++;

            if(left >= right){
                do {
                    begin++;
                } while(begin <= end && array[begin] == array[begin - 1]);
            }

            if(left <= right) {
                do {
                    end--;
                } while (begin <= end && array[end] == array[end + 1]);
            }
        }
        return count;
    }

	public static void main(String[] args) {
		ConsoleReader reader = new ConsoleReader();
		System.out.println("Count the amount of unique absolute values in the given int array");
		System.out.println("===============================================================================");
		int [] array = reader.readSortedIntItems();
		C4_15_UniqAbsoluteValueCounter counter = new C4_15_UniqAbsoluteValueCounter();
		int count = counter.count(array);
		System.out.println("The amount of unique absolute value: " + count);
	}

}
