package com.interview.books.svinterview;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-5
 * Time: 下午4:55
 */
public class SV4_ClosestNumberConvert {
    public static void convert(int[] base, int[] src){
        int[] sorted = src.clone();
        Arrays.sort(sorted);
        boolean[] used = new boolean[src.length];
        for(int i = 0; i < src.length; i++){
            //find the closest number larger than base[i]
            int offset = 0;
            while(offset < sorted.length && sorted[offset] < base[i] || used[offset]){
                offset++;
            }
            used[offset] = true;
            src[i] = sorted[offset];
            //if the found number is larger than base[i], copy rest number in increasing order
            if(sorted[offset] > base[i]){
                for(int j = 0; j < sorted.length; j++){
                    if(!used[j]) src[++i] = sorted[j];
                }
                return;
            }
        }

    }

    public static void main(String[] args){
        int[] base = new int[]{2,4,1,0};
        int[] src = new int[]{1,2,3,4};
        convert(base, src);
        ConsoleWriter.printIntArray(src);

        src = new int[]{4,3,3,0};
        convert(base, src);
        ConsoleWriter.printIntArray(src);
    }

}
