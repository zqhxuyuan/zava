package com.interview.books.leetcodeoj;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-19
 * Time: 下午3:25
 */
public class LOJ31_NextPermutation {
    //find the first element not in non_decreasing order backwards, then find the min element in left larger than current.
    //1. check non_decreasing order: while(offset >= 0 && num[offset] >= num[offset + 1]) offset--;
    //2. find min element as the replaced num: while(replaceIdx >= 0 && num[replaceIdx] <= num[offset]) replaceIdx--;
    public void nextPermutation(int[] num) {
        int offset = num.length - 2;
        while(offset >= 0 && num[offset] >= num[offset + 1]) offset--;
        if(offset < 0){ //decreasing order
            Arrays.sort(num);
        } else {
            int replaceIdx = num.length - 1;
            while(replaceIdx >= 0 && num[replaceIdx] <= num[offset]) replaceIdx--;
            int temp = num[offset];
            num[offset] = num[replaceIdx];
            num[replaceIdx] = temp;
            Arrays.sort(num, offset + 1, num.length);
        }
    }

    public static void main(String[] args){
        int[] num = new int[]{1, 1};
        LOJ31_NextPermutation finder = new LOJ31_NextPermutation();
        finder.nextPermutation(num);
        ConsoleWriter.printIntArray(num);
    }
}
