package com.interview.flag.o;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-1-30
 * Time: 下午12:29
 */
public class O20_InplaceInterleavingArray {

    public void interleaving(int[] array){
        interleaving(array, 0, array.length - 1);
    }

    private void interleaving(int[] array, int low, int high){
        if(high - low < 2) return;
        //A1 A2 B1 B2;
        int mid = rotate(array, low, high);
        interleaving(array, low, mid);
        interleaving(array, mid + 1, high);
    }

    private int rotate(int[] array, int low, int high){
        int len = (high - low + 1)/2;
        int start = low + len/2;
        int mid = start + (len - len/2) - 1;
        int end = mid + len/2;
        reverse(array, start, end);
        reverse(array, start, mid);
        reverse(array, mid + 1, end);
        return mid;
    }

    private void reverse(int[] array, int low, int high){
        for(int i = 0; i < (high - low + 1)/2; i++){
            int temp = array[low + i];
            array[low + i] = array[high - i];
            array[high - i] = temp;
        }
    }

    public static void main(String[] args){
        O20_InplaceInterleavingArray interleaver = new O20_InplaceInterleavingArray();
        int[] array = new int[]{1,3,5,7,2,4,6,8};
        interleaver.interleaving(array);
        ConsoleWriter.printIntArray(array);
    }
}
