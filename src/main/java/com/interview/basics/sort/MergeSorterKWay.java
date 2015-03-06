package com.interview.basics.sort;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/17/14
 * Time: 2:30 PM
 */
public class MergeSorterKWay<T extends Comparable<T>> extends Sorter<T> {
    public int k;
    public MergeSorterKWay(int k){
        this.k = k;
    }
    @Override
    public T[] sort(T[] input) {
        int N = input.length;
        T[] aux = (T[]) new Comparable[N];
        sort(input, aux, 0, N-1);
        return input;
    }

    protected void sort(T[] input, T[] aux, int low, int high){
        if(high <= low) return;
        int step = 1;
        if(high - low + 1 > k){
            step = (high - low + 1) / k;
            for(int i = 0; i < k; i++){
                sort(input, aux, low + i * step, i == k - 1? high : low + (i + 1) * step - 1);
            }
        }
        merge(input, aux, low, high, step);
    }

    protected void merge(T[] input, T[] aux, int low, int high, int step) {
        for(int i = low; i <= high; i++)    aux[i] = input[i];
        int[] offsets = new int[k];
        for(int i = 0; i < k; i++) offsets[i] = low + i * step;
        for(int j = low; j <= high; j++){
            int offset = -1;
            int min = -1;
            for(int i = 0; i < k; i++){
                int top = low + (i + 1) * step - 1;
                if(offsets[i] <= (i == k - 1 || top > high ? high : top)
                        && (min == -1 || aux[offsets[i]].compareTo(aux[min]) < 0)){
                    min = offsets[i];
                    offset = i;
                }
            }
            input[j] = aux[min];
            offsets[offset]++;
        }
    }
}
