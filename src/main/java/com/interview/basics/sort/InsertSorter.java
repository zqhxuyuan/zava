package com.interview.basics.sort;

public class InsertSorter<T extends Comparable<T>> extends Sorter<T> {
	/**
	 * keep the left part ordered. every time insert ith element in the right place between 0 and i-1
	 * time: N-1 to N^2/2, average: N^2/4
	 * switch: 0 to N^2/2, average: N^2/4
	 */
	@Override
	public T[] sort(T[] input) {
		for(int i = 1; i < input.length; i++){
			//compare with the previous one, if smaller, switch with it.
			for(int j = i; j > 0 && input[j-1].compareTo( input[j]) > 0; j--){
				//add the condition input[j-1] > input[j] in the for loop;
				//cause if j-1th < jth, j-2th < jth(left part is ordered)
				swap(input, j, j-1);
			}
		}
		return input;
	}

}
