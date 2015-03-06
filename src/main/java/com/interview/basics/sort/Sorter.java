package com.interview.basics.sort;
/**
 * sort algorithm interface
 * @author stefanie
 *
 */
public abstract class Sorter<T extends Comparable<T>> {
	/**
	 * sort input int array and return the sorted int array
	 * @param input
	 * @return
	 */
	public abstract T[] sort(T[] input);
	/**
	 * swap the ith and jth element in int array input
	 * @param input
	 * @param i
	 * @param j
	 */
	public void swap(T[] input, int i, int j){
		T temp = input[i];
		input[i] = input[j];
		input[j] = temp;
	}
}
