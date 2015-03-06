package com.interview.algorithms.sort;

/**
 * Created_By: zouzhile
 * Date: 10/19/14
 * Time: 2:41 PM
 */
public class MergeSort {

    private int[] aux;

    private void sort(int[] array) {
        aux = new int[array.length];
        this.sort(array, 0, array.length - 1);
    }

    public void sort(int[] array, int lo, int hi) {
        if(lo >= hi)
            return;
        int mid = (lo + hi) / 2;
        sort(array, lo, mid);
        sort(array, mid+1, hi);
        merge(array, lo, mid, hi);
    }

    public void merge(int[] array, int lo, int mid, int hi) {
        for(int i = lo; i <= hi; i ++) {
            aux[i] = array[i];
        }
        int i = lo, j = mid + 1;
        for(int k = lo; k <= hi; k ++) {
            if(i > mid) array[k] = aux[j++];
            else if(j > hi) array[k] = aux[i++];
            else if(aux[i] < aux[j]) array[k] = aux[i++];
            else array[k] = aux[j++];
        }
    }

    public static void main(String[] args) {
        int[] array = new int[] {4, 5, 1, 2 ,3};
        MergeSort sorter = new MergeSort();
        sorter.sort(array);
        for(int a : array)
            System.out.print(a + " ");
    }
}
