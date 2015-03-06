package com.interview.basics.sort;

public class MergeSorter<T extends Comparable<T>> extends Sorter<T>{

	@Override
	public T[] sort(T[] input) {
		int N = input.length;
		T[] aux = (T[]) new Comparable[N];
		sort(input, aux, 0, N-1);
		return input;
	}

	private void sort(T[] input, T[] aux, int lo, int hi) {
		if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(input, aux, lo, mid);
        sort(input, aux, mid + 1, hi);
        merge(input, aux, lo, mid, hi);
	}

	private void merge(T[] input, T[] aux, int lo, int mid, int hi) {
		//copy input array to aux
		for(int k = lo; k <= hi; k++)   aux[k] = input[k];
		int i = lo, j = mid + 1;
		for(int k = lo; k <= hi; k++){
			if      (i > mid)           input[k] = aux[j++];	//left part is all copied, still copy right part
            else if (j > hi)            input[k] = aux[i++];	//right part is all copied, still copy left part
            else if (aux[j].compareTo(aux[i]) < 0) 	input[k] = aux[j++];	//right element is smaller, copy right part
            else   						input[k] = aux[i++];	//left element is smaller, copy left part
		}
		
	}

}
