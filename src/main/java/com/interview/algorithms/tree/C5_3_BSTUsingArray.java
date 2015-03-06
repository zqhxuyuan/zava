package com.interview.algorithms.tree;

import com.interview.basics.sort.QuickSorter;

public class C5_3_BSTUsingArray<T extends Comparable>{
    T[] input;

	static QuickSorter SORTER = new QuickSorter();

	public C5_3_BSTUsingArray(T[] input) {
		//sort before set
		this.input = input;
		SORTER.sort(input);
	}

	
	public int searchIterative(int item) {
		int N = this.input.length - 1;
		int k = N/2;
		while(true){
			int ck = k;
			if(k <= 0 || k >= N) 		            return -1;
			else if(input[k].compareTo(item) == 0)	return k;
			else if(input[k].compareTo(item) < 0)	k = k/2;
			else k = k + (N-k)/2;
			N = ck;
		}
	}

	public int search(int item){
		return search(item, 0, input.length - 1);
	}
	
	private int search(int item, int lo, int hi){
		if(hi < lo) return -1;
		int mid = lo + (hi - lo)/2;
		if(input[mid].compareTo(item) == 0) 		return mid;
		else if(input[mid].compareTo(item) > 0)	    return search(item, lo, mid - 1);
		else                                        return search(item, mid + 1, hi);
	}
	
}
