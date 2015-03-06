package com.interview.algorithms.bit;

/**
 * Created_By: stefanie
 * Date: 14-10-14
 * Time: 上午9:26
 *
 *
 * number:  000110001
 * next:    000110010
 * previous:000101100
 *
 * number:  000110100
 * next:    000111000
 * previous:000110001
 *
 */
public class C16_3_NextNumberSameBit {
    public static int next(int number){
        char[] numBin = ("0" + Integer.toBinaryString(number)).toCharArray();
        int first1 = numBin.length - 1;
        while(numBin[first1] != '1') first1--;
        int first0 = first1 - 1;
        while(first0 >= 0 && numBin[first0] != '0') first0--;

        numBin[first0] = '1';
        int i = 1;
        while(i <= numBin.length - first1) numBin[first0 + i++] = '0';
        i = first0 + i;
        while(i < numBin.length) numBin[i++] = '1';
        return Integer.parseInt(String.copyValueOf(numBin), 2);
    }

    public static int previous(int number){
        char[] numBin = ("0" + Integer.toBinaryString(number)).toCharArray();
        int first1 = numBin.length - 1;
        while(numBin[first1] != '0') first1--;
        int first0 = first1 - 1;
        while(first0 >= 0 && numBin[first0] != '0') first0--;

        numBin[first0] = '1';
        int i = 1;
        while(i <= numBin.length - first1) numBin[first0 + i++] = '1';
        i = first0 + i;
        while(i < numBin.length) numBin[i++] = '1';
        return Integer.parseInt(String.copyValueOf(numBin), 2);
    }
}
