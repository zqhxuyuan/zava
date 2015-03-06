package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-10-21
 * Time: 上午8:58
 */
public class C4_70_PartialSortedArrayOffsetFinder {
    static class Indices {
        int begin;
        int end;
        int getLength(){
            return end - begin + 1;
        }
    }
    public static Indices find(int[] array){
        Indices asc = find(array, true);
        Indices dec = find(array, false);
        return (asc.getLength() > dec.getLength())? dec : asc;
    }

    private static Indices find(int[] array, boolean isAsc){
        Indices indices = new Indices();
        indices.begin = 0;
        indices.end = array.length - 1;
        int[] next = next(array, isAsc);
        while(indices.begin < indices.end && array[indices.begin] == next[indices.begin]) indices.begin++;
        int[] pre = pre(array, !isAsc);
        while(indices.end > indices.begin && array[indices.end] == pre[indices.end]) indices.end--;
        return indices;
    }

    private static int[] next(int[] array, boolean isMin){
        int[] next = new int[array.length];
        next[array.length - 1] = array[array. length - 1];
        for(int i = array.length - 2; i >= 0; i--){
            if(isMin)   next[i] = array[i] < next[i + 1]? array[i] : next[i+1];
            else        next[i] = array[i] > next[i + 1]? array[i]: next[i+1];
        }
        return next;
    }

    private static int[] pre(int[] array, boolean isMin){
        int[] pre = new int[array.length];
        pre[0] = array[0];
        for(int i = 1; i < array.length; i++){
            if(isMin) pre[i] = array[i] < pre[i-1]? array[i] : pre[i-1];
            else pre[i] = array[i] > pre[i-1]? array[i] : pre[i-1];
        }
        return pre;
    }
}
