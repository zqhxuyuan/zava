package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/15/14
 * Time: 3:21 PM
 */
public class C1_57_NumbersFollowTheRule {

    public static int numbers(int A, int B){
        int max = Math.max(A, B);
        int min = Math.min(A, B);
        return 0;
    }

    public static List<Integer> correctAnswer(int A, int B){
        List<Integer> numbers = new ArrayList<>();
        int i = A + 1;
        while(i < B){
            char[] chars = String.valueOf(i).toCharArray();
            int sum = 0;
            for(int j = 0; j < chars.length; j++) sum += chars[j] - '0';
            if(sum / chars.length > 7) numbers.add(i);
            i++;
        }
        return numbers;
    }
}
