package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 15-1-25
 * Time: 下午11:10
 */
public class TQ70_SearchInVArray {

    public int find(int[] array, int target){
        return find(array, target, 0, array.length - 1);
    }

    private int find(int[] array, int target, int low, int high){
        if(low > high) return -1;
        int mid = (low + high) / 2;
        if(target == array[mid]) return mid;
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

    public static void main(String[] args){
        TQ70_SearchInVArray searcher = new TQ70_SearchInVArray();
        int[] array = new int[]{6,4,2,1,3,7,8,9,10};
        System.out.println(searcher.find(array, 4)); //1
        System.out.println(searcher.find(array, 7)); //5
        System.out.println(searcher.find(array, 5)); //-1
        System.out.println(searcher.find(array, 6)); //0
        System.out.println(searcher.find(array, 11)); //-1
    }
}
