package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 上午10:45
 */
public class LOJ5_LongestPalindromeSubstring {
    //for each position: find the palindrome which center is this char(odd) or this char and it's next char.(even)
    //1. position loop 0 ~ length - 1;
    //2. do while loop when charAt(i - len) == charAt(i + len), and calculate len when break using (len - 1);
    //3. remember to check index out of range before call charAt(i);
    public static String longestPalindrome(String str){
        if(str == null || str.length() == 0) return "";
        int maxLen = 1;
        int start = 0;
        int end = 0;
        for(int i = 0; i < str.length(); i++){
            int len = 1;
            while(i - len >= 0 && i + len < str.length() && str.charAt(i - len) == str.charAt(i + len)) len++;
            if(2 * (len - 1) + 1 > maxLen){
                maxLen = 2 * (len - 1) + 1;
                start = i - (len - 1);
                end = i + (len - 1);
            }
            len = 0;
            while(i - len >= 0 && i + 1 + len < str.length() && str.charAt(i - len) == str.charAt(i + 1 + len)) len++;
            if(2 * (len - 1) + 2 > maxLen){
                maxLen = 2 * (len - 1) + 2;
                start = i - (len - 1);
                end = i + 1 + (len - 1);
            }
        }
        return str.substring(start, end + 1);
    }
}
