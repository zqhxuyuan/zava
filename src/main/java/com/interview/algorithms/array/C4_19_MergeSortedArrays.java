package com.interview.algorithms.array;

public class C4_19_MergeSortedArrays {
	
	public static int[] merge(int[] arr1, int[] arr2){
        int N = arr1.length + arr2.length;
        int[] merged = new int[N];

        int i = 0;
        int j = 0;
        for(int k = 0; k < N; k++){
            if(i >= arr1.length) merged[k] = arr2[j++];
            else if(j >= arr2.length) merged[k] = arr1[i++];
            else if(arr1[i] < arr2[j]) merged[k] = arr1[i++];
            else merged[k] = arr2[j++];
        }

        return merged;
	}
}
