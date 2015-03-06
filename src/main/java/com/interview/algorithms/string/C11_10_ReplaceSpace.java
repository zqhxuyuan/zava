package com.interview.algorithms.string;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/4/14
 * Time: 4:28 PM
 *
 * Write a method to replace all spaces in a string with "%20"
 *
 * 1. Count the space number, and allocate a new char array
 * 2. Backward copy the char to the new array, insert %20 when encouter space
 *
 * Time: O(N)   Space: O(N)
 */
public class C11_10_ReplaceSpace {

    public String replace(String str){
        char[] chars = str.toCharArray();
        int spaceCount = 0;
        for(int i = 0; i < chars.length; i++){
            if(chars[i] == ' ') spaceCount++;
        }
        if(spaceCount > 0){
            int size = chars.length + 2 * spaceCount;
            char[] newChars = new char[size];
            for(int i = chars.length - 1; i >= 0; i--){
                if(chars[i] == ' '){
                    newChars[--size] = '0';
                    newChars[--size] = '2';
                    newChars[--size] = '%';
                } else {
                    newChars[--size] = chars[i];
                }
            }
            return String.copyValueOf(newChars);
        } else {
            return str;
        }

    }
}
