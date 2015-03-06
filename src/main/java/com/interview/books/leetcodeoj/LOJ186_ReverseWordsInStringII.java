package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 15-2-4
 * Time: 上午9:30
 */
public class LOJ186_ReverseWordsInStringII {
    //reverse the entire string, then reverse by word.
    //1. handle edge cases: if(s == null || s.length <= 1) return;
    //2. handle the last word
    //3. when found a ' ', next word start from i+1
    public void reverseWords(char[] s) {
        if(s == null || s.length <= 1) return;
        reverse(s, 0, s.length - 1);
        int begin = 0;
        for(int i = 0; i <= s.length; i++){
            if(i == s.length || s[i] == ' ') {
                reverse(s, begin, i - 1);
                begin = i + 1;
            }
        }
    }

    public void reverse(char[] s, int start, int end){
        while(start < end){
            char temp = s[start];
            s[start] = s[end];
            s[end] = temp;
            start++;
            end--;
        }
    }
}
