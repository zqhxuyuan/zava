package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-7-19
 * Time: 下午4:57
 *
 * Write code to reserve the word sequence. input as I am Stefanie output is Stefanie am I
 *
 *  The simplest way is reverse the whole string, then reverse each word.
 */
public class C11_19_StringWordReverse {

    public static String wordReverse(String sens){
        char[] sensArray = sens.toCharArray();
        reverse(sensArray, 0, sensArray.length - 1);
        int begin = 0;
        int end = 0;
        while(end < sensArray.length) {
            while (end < sensArray.length && sensArray[end] != ' ') end++;
            reverse(sensArray, begin, end - 1);
            begin = ++end;
        }
        return String.copyValueOf(sensArray);
    }

    public static void reverse(char[] str, int begin, int end){
        while(begin < end){
            char temp = str[begin];
            str[begin] = str[end];
            str[end] = temp;
            begin++;
            end--;
        }
    }
}
