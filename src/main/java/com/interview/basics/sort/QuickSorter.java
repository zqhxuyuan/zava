package com.interview.basics.sort;

import java.util.Random;

public class QuickSorter<T extends Comparable<T>> extends Sorter<T>{

	@Override
	public T[] sort(T[] input) {
		input = shuffle(input);
		sort(input, 0, input.length - 1);
		return input;
	}
	
	protected void sort(T[] input, int lo, int hi){
		if(hi <= lo) return;
		//partition array and return the key index
		int key = partition(input, lo, hi);
		//sort the sub array
		sort(input, lo, key - 1);
		sort(input, key + 1, hi);

	}

	private int partition(T[] input, int lo, int hi){
		T key = input[lo];
		int i = lo, j = hi + 1;
		while(true){
			// find item on lo to swap
			while(++i < hi && input[i].compareTo(key) < 0);
			// find item on hi to swap
			while(--j > lo && input[j].compareTo(key) > 0);
			// check if pointers crossProduct
			if (i >= j) break;
			swap(input, i, j);
		}
		//with a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
		swap(input, lo, j);
		return j;
	}

	private T[] shuffle(T[] input){
		int N = input.length;
		Random random = new Random();
        for (int i = 0; i < N; i++) {
            int r = i + random.nextInt(N-i);     // between i and N-1
            swap(input, i, r);
        }
        return input;
	}

}
