package com.interview.basics.sort;

public class ShellSorter<T extends Comparable<T>> extends Sorter<T>{

	@Override
	public T[] sort(T[] input) {
		int N = input.length;
		// 3x+1 increment sequence:  1, 4, 13, 40, 121, 364, 1093, ...
        int h = 1;
        while (h < N/3) h = 3*h + 1;

        while (h >= 1) {
            // h-sort the array
            for (int i = h; i < N; i++) {
                for (int j = i; j >= h && (input[j].compareTo(input[j-h]) < 0); j -= h) {
                    swap(input, j, j-h);
                }
            }
            h /= 3;
        }
        return input;
	}

}
