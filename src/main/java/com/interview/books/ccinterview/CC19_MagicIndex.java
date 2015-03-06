package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午6:09
 */
public class CC19_MagicIndex {
    public int index(int[] array){
        int low = 0;
        int high = array.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(mid == array[mid]) return mid;
            else if(mid > array[mid]) low = mid + 1;
            else high = low - 1;
        }
        return -1;
    }

    public int indexDedup(int[] array){
        return indexDedup(array, 0, array.length - 1);
    }

    private int indexDedup(int[] array, int low, int high){
        if(low > high) return -1;
        int mid = low + (high - low)/2;
        if(mid == array[mid]) return mid;

        int left = indexDedup(array, low, Math.min(mid - 1, array[mid]));
        if(left >= 0) return left;

        return indexDedup(array, Math.max(mid + 1, array[mid]), high);
    }


    public static void main(String[] args){
        CC19_MagicIndex finder = new CC19_MagicIndex();
        int[] array = new int[]{-1,0,1,2,3,5,9,10};
        System.out.println(finder.index(array));

        array = new int[]{1,3,5,7,10};
        System.out.println(finder.index(array));

        array = new int[]{1,2,3,4,4,8,10,14};
        System.out.println(finder.index(array));
        System.out.println(finder.indexDedup(array));
    }
}
