package com.interview.algorithms.string;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/4/14
 * Time: 3:24 PM
 *
 * Design an algorithm and write code to remove the duplicate characters in a string without using any additional buffer.
 *
 * 1. cleanByScan use no additional storage, scan the str by char and make sure every char is not appeared before during the scan.
 *    swap the current char with tail char to remove the duplicated chars.
 *      Time: O(N^2), Space: O(1)
 * 2. cleanByScanOnce use an additonal O(256) storage to make a boolean flag of every char.
 *      Time: O(N),   Space: O(256)
 * 3. could use radix sort, which is O(N) and space O(1) see C4_55
 *
 */
public class C11_9_RemoveDuplicateChar {
    public String cleanByScan(char[] str){
        if(str == null) return null;
        if(str.length < 2) return String.valueOf(str);
        int tail = 1;
        for(int i = 1; i < str.length; i++){
            int j;
            for(j = 0; j < tail && str[i] != str[j]; j++) {}
            if(j == tail)  str[tail++] = str[i];
        }
        return String.valueOf(str, 0, tail);
    }

    public String cleanByScanOnce(char[] str){
        if(str == null) return null;
        if(str.length < 2) return String.valueOf(str);
        boolean[] char_set = new boolean[256];
        int tail = 0;
        for(int i = 0; i < str.length; i++){
            if(!char_set[str[i]]) {
                str[tail++] = str[i];
                char_set[str[i]] = true;
            }
        }
        return String.valueOf(str, 0, tail);
    }
}
