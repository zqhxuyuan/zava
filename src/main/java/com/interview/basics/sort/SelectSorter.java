package com.interview.basics.sort;

public class SelectSorter<T extends Comparable<T>> extends Sorter<T>{
	/**
	 * every time switch the min element to ith element, 
	 * 	sort time is independent with the input, N^2/2
	 * 	N times switch, the smallest.
	 */
	@Override
	public T[] sort(T[] input) {
		for(int i = 0; i < input.length - 1; i++ ){
			//find the min element
			int min = i;
			for(int j = i + 1; j < input.length; j++){
				if(input[j].compareTo(input[min]) < 0){
					min = j;
				}
			}
			//switch between min and ith element
			swap(input, i, min);
		}
		return input;
	}

}
