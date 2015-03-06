package com.interview.leetcode.binarysearch;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 下午4:41
 */
public class SearchingRotatedArray {

    public static int min(int[] array){
        int low = 0;
        int high = array.length - 1;
        while(low < high){
            while(low < high && array[low] == array[high]) high--;  //handle duplication
            int mid = (low + high) / 2;
            if(array[mid] > array[mid + 1]) return array[mid + 1];  //find max, return min
            else if(array[mid] > array[high]) low = mid + 1;
            else high = mid;
        }
        return array[0];
    }

    public static int max(int[] array){
        int low = 0;
        int high = array.length - 1;
        while(low < high){
            while(low < high && array[low] == array[high]) low++; //handle duplication
            int mid = (low + high) / 2;
            if(array[mid] > array[mid + 1]) return array[mid];   //return max
            else if(array[mid] > array[high]) low = mid + 1;
            else high = mid;
        }
        return array[array.length - 1];
    }

    public static int find(int[] array, int target){
        int low = 0;
        int high = array.length - 1;
        while(low <= high){
            while(low < high && array[low] == array[high]) high--; //if array[low] = array[high] move high to a element not equals to low, to avoid can't determine left or right in the following case
            int mid = (low + high) / 2;
            if(target == array[mid]) return mid;
            if(target < array[mid]){
                if(array[low] <= array[mid] && target < array[low]) low = mid + 1;   //when left part is in order, and target < array[low], should binarysearch in the right part
                else high = mid - 1;  //binarysearch in the left part
            } else {
                if(array[high] >= array[mid] && target > array[high]) high = mid - 1; //when right part is in order, and target > array[high], should binarysearch in the left part
                else low = mid + 1;   //binarysearch in the right part
            }
        }
        return -1;
    }
}
