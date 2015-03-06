package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 上午3:39
 */
public class C4_80_SearchforARange {
    public static int[] search(int[] array, int target){
        int[] range = new int[2];
        int low = 0, high = array.length - 1, mid;
        while(low + 1 < high){  //binarysearch for the low bound
            mid = (low + high) / 2;
            if(array[mid] >= target) high = mid;
            else low = mid;
        }
        if(array[low] == target) range[0] = low;
        else if(array[high] == target) range[0] = high;
        else {
            range[0] = range[1] = -1;
            return range;
        }
        if(range[0] + 1 >= array.length){   //if range[0] is already the end of array
            range[1] = range[0];
            return range;
        }

        low = range[0] + 1;  //binarysearch for the high bound
        high = array.length - 1;
        while(low + 1 < high){
            mid = (low + high) / 2;
            if(array[mid] <= target) low = mid;
            else high = mid;
        }
        if(array[high] == target) range[1] = high;
        else if(array[low] == target) range[1] = low;
        else range[1] = range[0];
        return range;
    }
}
