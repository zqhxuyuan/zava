package com.interview.flag.o;

/**
 * Created_By: stefanie
 * Date: 15-1-15
 * Time: 上午11:00
 */
public class O11_MinPrefixToPalindrome {
    public String getPrefix(String S){
        StringBuffer buffer = new StringBuffer();
        int end = S.length() - 1;
        int tail = end;
        int head = 0;
        while(tail > head){
            while(tail > head && S.charAt(tail) == S.charAt(head)){
                tail--;
                head++;
            }
            if(tail <= head) break; //is already a palindrome

            while(end >= tail)buffer.append(S.charAt(end--));
            tail--;
            head = 0;
        }
        return buffer.toString();
    }

    public static void main(String[] args){
        O11_MinPrefixToPalindrome finder = new O11_MinPrefixToPalindrome();
        System.out.println(finder.getPrefix("ababc")); //cb
        System.out.println(finder.getPrefix("abacabc")); //cbac
    }
}
