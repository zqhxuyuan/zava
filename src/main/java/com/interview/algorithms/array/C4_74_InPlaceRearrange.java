package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-11-3
 * Time: 下午9:51
 */
public class C4_74_InPlaceRearrange {

    public static String rearrange(String str){
        char[] chars = str.toCharArray();
        int n = chars.length / 2;
        for(int i = n - 1; i > 0; i--) {
            for(int j = i; j < 2 * n - i; j += 2) {
                swap(chars, j, j + 1);
            }
        }
        return String.valueOf(chars);
    }

    private static void swap(char[] chars, int i, int j) {
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
    }
}
