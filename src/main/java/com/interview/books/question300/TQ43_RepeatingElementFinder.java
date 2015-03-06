package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 15-1-20
 * Time: 下午2:49
 */
public class TQ43_RepeatingElementFinder {
    public int find(int[] array){
        return find(array, 0, array.length - 1);
    }

    private int find(int[] array, int low, int high){
        if(low >= high) return -1;
        int mid = low + (high - low)/2;
        if(array[mid] == array[mid + 1]){
            if(mid == low || array[mid - 1] != array[mid]) return mid;
            else {
                return searchLow(array, low, mid - 1, array[mid]);
            }
        } else {
            int found = find(array, low, mid);
            if(found >= 0) return found;
            else return find(array, mid + 1, high);
        }
    }

    private int searchLow(int[] array, int low, int high, int key){
        while(low < high){
            int mid = low + (high - low)/2;
            if(array[mid] >= key) high = mid;
            else low = mid + 1;
        }
        return low;
    }

    public static void main(String[] args){
        TQ43_RepeatingElementFinder finder = new TQ43_RepeatingElementFinder();
        int[] array = new int[]{1,3,4,5,7,7,8,9,12,16,17,31};
        System.out.println(finder.find(array));  //4
        array = new int[]{1,3,4,5,7,8,9,12,16,17,17,31};
        System.out.println(finder.find(array));  //9
    }
}
