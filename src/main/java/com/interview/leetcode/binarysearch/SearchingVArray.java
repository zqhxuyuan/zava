package com.interview.leetcode.binarysearch;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 下午6:16
 */
public class SearchingVArray {
    public static int min(int[] array){
        int low = 0;
        int high = array.length - 1;
        while(low < high){
            int mid = (low + high)/2;
            int cmp1 = mid > low? array[mid] - array[mid - 1] : -1;
            int cmp2 = mid < high? array[mid] - array[mid + 1] : -1;
            if(cmp1 < 0 && cmp2 < 0) return array[mid];  //smaller than prev and next, it's the min
            else if(cmp1 > 0){   //larger than prev, mid in the increasing part, search in left
                high = mid - 1;
            } else if(cmp2 > 0){ //larger than next, mid in the decreasing part, search in right
                low = mid + 1;
            } else {  //mid equals with mid+1 and mid-1, move low one step, handle duplication
                low++;
            }
        }
        return array[low];
    }

    public static int find(int[] array, int target){
        return find(array, target, 0, array.length - 1);
    }

    private static int find(int[] array, int target, int low, int high){
        if(low > high) return -1;
        int mid = (low + high) / 2;
        if(target == array[mid]) return array[mid];
        else if(target < array[mid]){
            int cmp1 = mid > low? array[mid] - array[mid - 1] : -1;
            int cmp2 = mid < high? array[mid] - array[mid + 1] : -1;
            if(cmp1 < 0 && cmp2 < 0) return -1; //mid is the min;
            else if(cmp1 > 0) return find(array, target, low, mid - 1); //larger than prev, mid in the increasing part, search in left
            else if(cmp2 > 0) return find(array, target, mid + 1, high); //larger than next, mid in the decreasing part, search in right
            else return find(array, target, low + 1, high);   //mid equals with mid+1 and mid-1, move low one step, handle duplication
        } else {
            int found = (array[low] >= target)? find(array, target, low, mid - 1): -1;  //binarysearch left
            if(found == -1) found = array[high] >= target? find(array, target, mid + 1, high): -1;  //no found, binarysearch right
            return found;
        }
    }


}
