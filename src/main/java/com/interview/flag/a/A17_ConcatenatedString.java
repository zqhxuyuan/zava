package com.interview.flag.a;

import com.interview.algorithms.general.C1_59_PrimeNumber;

import java.util.HashSet;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 15-1-7
 * Time: 上午11:43
 */
public class A17_ConcatenatedString {
    static Set<Integer> primes = new HashSet();
    static {
        int[] numbers = C1_59_PrimeNumber.generate(100);
        for(int i = 0; i < 100; i++) primes.add(numbers[i]);
    }

    public boolean isMultiple(String s){

        if (s == null || s.length() <= 2) return false;

        int len = s.length();
        int begin = 0;
        for (int i = 1; i < len; i++) {
            if (s.charAt(i) == s.charAt(begin)) begin++;
            else if(s.charAt(i) == s.charAt(0)) begin = 1;
            else begin = 0;
        }

        int pattern = len - begin;
        if(pattern == 1){
            return !primes.contains(len);
        } else if(pattern == 2){
            return !primes.contains(len/2);
        } else {
            if((len % 2 == 0 && begin >= len/2 - 1) || (len % 2 != 0 && begin >= len/2)){
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args){
        A17_ConcatenatedString checker = new A17_ConcatenatedString();
        System.out.println(checker.isMultiple("abcabcabc"));   //true
        System.out.println(checker.isMultiple("bcdbcdbcde"));  //false
        System.out.println(checker.isMultiple("abcdabcd"));    //true
        System.out.println(checker.isMultiple("xyz"));         //false
        System.out.println(checker.isMultiple("aaaaaaaaaa"));  //true
        System.out.println(checker.isMultiple("bcdbcbcdbc"));  //true
        System.out.println(checker.isMultiple("aaaaaaa"));     //false
    }
}
