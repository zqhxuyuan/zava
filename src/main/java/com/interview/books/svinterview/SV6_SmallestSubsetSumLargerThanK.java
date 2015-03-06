package com.interview.books.svinterview;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-12-5
 * Time: 下午7:18
 */
public class SV6_SmallestSubsetSumLargerThanK {

    public int find(int[] array, int K){
        int low = 0;
        int high = array.length - 1;
        while(low < high){
            int[] partition = partition(array, low, high);
            if(partition[1] <= K){
                high = partition[0];
            } else {
                low = partition[0] + 1;
            }
        }
        int sum = 0;
        for(int i = array.length - 1; i >= high; i--) sum += array[i];
        return sum > K? high : high - 1;
    }

    private int[] partition(int[] array, int low, int high){
        int pivot = low;
        int sum = 0;
        for(int j = low + 1; j <= high; j++){
            if(array[j] < array[low]) ArrayUtil.swap(array, ++pivot, j);
            else sum += array[j];
        }
        ArrayUtil.swap(array, pivot, low);
        sum += array[pivot];
        return new int[]{pivot, sum};
    }

//
//
//    public static int find(int[] array, int K){
//        return find(array, K, 0, array.length - 1);
//    }
//
//    public static int find(int[] array, int K, int low, int high){
//        if(low > high) return -1;
//        int pivot = low;
//        int sum = 0;
//        for(int j = low + 1; j <= high; j++){
//            if(array[j] > array[low]) {
//                sum += array[j];
//                ArrayUtil.swap(array, ++pivot, j);
//            }
//        }
//        ArrayUtil.swap(array, pivot, low);
//        sum += array[pivot];
//        if(sum < K) return find(array, K - sum, pivot + 1, high);
//        else {
//            int index = find(array, K, low, pivot - 1);
//            return index != -1 ? index : pivot;
//        }
//    }

    public static void main(String[] args){
        SV6_SmallestSubsetSumLargerThanK finder = new SV6_SmallestSubsetSumLargerThanK();
        int[] array = new int[]{5,2,1,7,3,4,9};
        int index = finder.find(array, 18);
        for(int i = array.length - 1; i >= index; i--)
            System.out.print(array[i] + " ");
        System.out.println();

        index = finder.find(array, 10);
        for(int i = array.length - 1; i >= index; i--)
            System.out.print(array[i] + " ");
    }
}
