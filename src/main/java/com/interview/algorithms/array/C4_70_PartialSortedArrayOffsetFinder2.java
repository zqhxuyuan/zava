package com.interview.algorithms.array;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/21/14
 * Time: 12:58 PM
 */
public class C4_70_PartialSortedArrayOffsetFinder2 {
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
        return asc.getLength() < dec.getLength()? asc : dec;
    }

    private static Indices find(int[] array, boolean isAsc){
        Indices offset = new Indices();
        findMiddle(array, offset, isAsc);
        shink(array, offset, isAsc);
        return offset;
    }

    private static void findMiddle(int[] array, Indices offset, boolean isAsc){
        offset.begin = 0;
        offset.end = array.length - 1;
        if(isAsc){
            while(offset.begin < offset.end && array[offset.begin] <= array[offset.begin + 1]) offset.begin++;
        } else {
            while(offset.begin < offset.end && array[offset.begin] >= array[offset.begin + 1]) offset.begin++;
        }
        offset.begin++;
        if(isAsc){
            while(offset.begin < offset.end && array[offset.end] >= array[offset.end - 1]) offset.end--;
        } else {
            while(offset.begin < offset.end && array[offset.end] <= array[offset.end - 1]) offset.end--;
        }
        offset.end--;
    }

    private static void shink(int[] array, Indices offset, boolean isAsc){
        int min = array[offset.begin - 1];
        int max = array[offset.begin - 1];
        for(int i = offset.begin + 1; i <= offset.end + 1; i++){
            if(array[i] < min) min = array[i];
            else if(array[i] > max) max = array[i];
        }
        if(isAsc){
            while(offset.begin > 0 && array[offset.begin - 1] > min) offset.begin--;
            while(offset.end < array.length - 1 && array[offset.end + 1] < max) offset.end++;
        } else {
            while(offset.begin > 0 && array[offset.begin - 1] < max) offset.begin--;
            while(offset.end < array.length - 1 && array[offset.end + 1] > min) offset.end++;
        }

    }
}
