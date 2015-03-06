package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午9:09
 */
public class LOJ125_ValidPalindrome {
    //use while(front < back && !validChar(s.charAt(front))) front++; to omit the invalid char
    //remember to check after two while: if(front >= back) return true;
    public boolean isPalindrome(String s) {
        if(s == null || s.length() <= 1) return true;
        s = s.toLowerCase();
        int front = 0;
        int back = s.length() - 1;
        while(front < back){
            while(front < back && !validChar(s.charAt(front))) front++;
            while(front < back && !validChar(s.charAt(back))) back--;
            if(front >= back) return true;
            if(s.charAt(front) != s.charAt(back)) return false;
            front++;
            back--;
        }
        return true;
    }

    public boolean validChar(char ch){
        if((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')) return true;
        else return false;
    }

    public static void main(String[] args){
        LOJ125_ValidPalindrome validator = new LOJ125_ValidPalindrome();
        System.out.println(validator.isPalindrome("ab"));
    }
}
