package com.interview.books.ninechapter;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 下午12:15
 */
public class NC6_FizzBuzz {
    public void print(int a, int b, Map<Integer, String> words){
        String[] placeholder = new String[b - a + 1];
        Arrays.fill(placeholder, "");
        for(Integer divisor : words.keySet()){
            String str = words.get(divisor);
            int offset = divisor - (a % divisor);
            while(offset <= b){
                placeholder[offset] += str;
                offset += divisor;
            }
        }

        for(int i = a; i <= b; i++){
            System.out.println(i + ": " + placeholder[i - a]);
        }
    }

    public static void main(String[] args){
        Map<Integer, String> words = new TreeMap<>();
        words.put(2, "Hello");
        words.put(3, "Fizz");
        words.put(5, "Buzz");

        NC6_FizzBuzz printer = new NC6_FizzBuzz();
        printer.print(1, 30, words);
    }
}
