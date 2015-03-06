package com.interview.flag.o;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-1
 * Time: 下午10:23
 */
public class O2_MaxContinousSeq {
    //Time: O(N), Space O(1)
    public int[] getMaxCountSeq(int[] array){
        int[] peak = new int[array.length];
        int maxIdx = 0;
        peak[0] = 1;
        for(int i = 1; i < array.length; i++){
            if(array[i] < array[i-1]) peak[i] = 1;
            else {
                if(array[i] >= array[maxIdx]){
                    peak[i] = peak[maxIdx] + i - maxIdx;
                    maxIdx = i;
                } else {
                    peak[i] = peak[i - 1] + 1;
                }
            }
        }
        return peak;
    }

    public static void main(String[] args){
        int[] array = new int[]{3, 5, 6, 4, 5, 6, 4, 5, 1, 2, 1, 1, 1, 9, 8, 7};
        O2_MaxContinousSeq builder = new O2_MaxContinousSeq();
        int[] seq = builder.getMaxCountSeq(array);
        //1, 2, 3, 1, 2, 6, 1, 2, 1, 2, 1, 2, 3, 14, 1, 1
        ConsoleWriter.printIntArray(seq);
    }
}
