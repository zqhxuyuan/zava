package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午4:37
 */
public class LOJ154_FindMinInRotatedSortedArray {
    //max element is array[i] > array[i + 1] && array[i - 1] > array[i](default); min element is array[i + 1];
    //do binary search low = 0 and high = array.length - 1,
    //if(array[mid] > array[mid + 1]) return array[mid + 1];
    //else if(array[mid] > array[high]) breaking point in high part, so low = mid + 1;
    //else breaking point in low part, so high = mid;
    //if no breaking point found, the min element is array[0];
    //de dup by checking array[low] == array[high], do high--;
    public int findMin(int[] array) {
        int low = 0;
        int high = array.length - 1;
        while(low < high){
            while(low < high && array[low] == array[high]) high--;
            if(low >= high) break;
            int mid = low + (high - low)/2;
            if(array[mid] > array[mid + 1]) return array[mid + 1];
            else if(array[mid] > array[high]) low = mid + 1;
            else high = mid;
        }
        return array[0];
    }
}
