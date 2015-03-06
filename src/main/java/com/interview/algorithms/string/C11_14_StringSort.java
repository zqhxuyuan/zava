package com.interview.algorithms.string;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/8/14
 * Time: 3:19 PM
 */
public class C11_14_StringSort {

    public static String sort(String str){
        int[] counter = new int[256];
        for(int i = 0; i < str.length(); i++){
            counter[str.charAt(i)]++;
        }

        int index = 0;
        char[] chars = new char[str.length()];
        for(int i = 0; i < 256; i++){
            for(int j = 0; j < counter[i]; j++){
                chars[index++] = (char) i;
            }
        }
        return String.valueOf(chars);
    }
}
