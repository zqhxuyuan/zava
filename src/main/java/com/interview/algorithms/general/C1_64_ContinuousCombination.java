package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/25/14
 * Time: 10:40 AM
 */
public class C1_64_ContinuousCombination {

    public static boolean have(int n){
        //if n is not the power of 2, it could be find a combination
        if((n & n - 1) == 0) return false;
        else return true;
    }

    public static List<Integer> find(int n){
        List<Integer> combination = new ArrayList<>();
        return combination;
    }

    //not correct.
    private static boolean find(int n, List<Integer> c){
        if(n == 1 || n == 2) return false;
        if(n / 2 != 0){
            c.add(n/2);
            c.add(n/2 + 1);
            return true;
        } else if(n / 3 == 0){
            c.add(n/3 - 1);
            c.add(n/3);
            c.add(n/3 + 1);
            return true;
        } else {
            int n3 = n / 3;
            return find(n3, c) && find(n3 + 1, c);
        }
    }
}
