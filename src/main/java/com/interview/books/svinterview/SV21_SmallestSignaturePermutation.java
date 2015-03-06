package com.interview.books.svinterview;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午9:05
 */
public class SV21_SmallestSignaturePermutation {
    public int[] find(String signature){
        int len = signature.length();
        int[] numbers = new int[len];
        int option = 1;
        int begin = 0;
        for(int i = 0; i <= len; i++){
            if(i < len && signature.charAt(i) == 'D') continue;
            else {
                if(i < len){
                    numbers[i] = option++;
                }
                for(int j = i - 1; j >= begin; j--){
                    numbers[j] = option++;
                }
                begin = i + 1;
            }
        }
        return numbers;
    }

    public static void main(String[] args){
        SV21_SmallestSignaturePermutation finder = new SV21_SmallestSignaturePermutation();
        int[] numbers = finder.find("DDIIDI");
        ConsoleWriter.printIntArray(numbers);
        numbers = finder.find("DDIDDD");
        ConsoleWriter.printIntArray(numbers);
        numbers = finder.find("IIDIID");
        ConsoleWriter.printIntArray(numbers);
    }
}
