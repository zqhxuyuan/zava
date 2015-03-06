package com.interview.flag.a;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午9:04
 */
public class A7_LongestPalindromeWithSwap {
    public static int longest(String str){
        char[] marker = new char[256];
        for(char ch : str.toCharArray()) marker[ch]++;
        int total = 0;
        int maxOdd = 0;
        for(int i = 0; i < 256; i++){
            if(marker[i] > 0){
                if(marker[i] % 2 == 0) total += marker[i];
                else maxOdd = Math.max(maxOdd, marker[i]);
            }
        }
        total += maxOdd;
        return total;
    }

    public static void main(String[] args){
        String str = "aaaaaaaabbbbbbdcccccddefg";
        //19
        System.out.println(longest(str));
    }
}
