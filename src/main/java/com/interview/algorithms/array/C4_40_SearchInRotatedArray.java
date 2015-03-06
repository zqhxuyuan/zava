package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-7-29
 * Time: 下午8:08
 */
public class C4_40_SearchInRotatedArray {
    public static int search(int[] array, int number){
        return search(array, number, 0, array.length -1);
    }

    private static int search(int[] array, int number, int low, int high){
        if(low > high) return -1;
        int mid = low + (high - low) / 2;
        if(array[mid] == number) return mid;
        else if(array[mid] < number){
            if(number <= array[high]) return search(array, number, mid + 1, high);
            else return search(array, number, low, mid - 1);
        } else {
            if(number >= array[low])  return search(array, number, low, mid - 1);
            else return search(array, number, mid + 1, high);
        }
//
//        else if(array[mid] <= array[high]){  //smaller goes to left, larger and larger than high goes to left, smaller than high goes to right
//            if(array[mid] > number || number > array[high]) return search(array, number, low, mid-1);
//            else return search(array, number, mid+1, high);
//        } else { //larger goes to right, smaller and smaller than high goes to right, larger than high goes to left.
//            if(array[mid] < number || number < array[high]) return search(array, number, mid+1, high);
//            else return search(array, number, low, mid-1);
//        }
    }
}
