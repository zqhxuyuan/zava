package com.interview.books.fgdsb;

import com.interview.books.question300.TQ6_GCDCalculator;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 下午10:49
 */
public class NLC26_MinimumCoverMatrix {
    public int min(char[][] matrix){
        int rows = 1;
        for(int i = 0; i < matrix.length; i++){
            rows = lcm(rows, minRange(matrix[i]));
        }

        int cols = 1;
        for(int j = 0; j < matrix[0].length; j++){
            char[] chars = new char[matrix.length];
            for(int i = 0; i < matrix.length; i++) chars[i] = matrix[i][j];
            cols = lcm(cols, minRange(chars));
        }
        return rows * cols;
    }

    public int lcm(int num1, int num2){
        if(num1 == num2) return num1;
        int gcd = TQ6_GCDCalculator.gcd(num1, num2);
        return num1 * num2 / gcd;
    }

    public int minRange(char[] chars){
        int[] next = new int[chars.length + 1];
        next[0] = 0;
        next[1] = 0;
        for(int i = 2; i <= chars.length; i++){
            int j = next[i-1];
            while(true){
                if(chars[j] ==chars[i-1]){
                    next[i] = j + 1;
                    break;
                } else if(j == 0){
                    next[j] = 0;
                    break;
                } else j = next[j];
            }
        }
        return chars.length - next[chars.length];
    }

    public static void main(String[] args){
        NLC26_MinimumCoverMatrix finder = new NLC26_MinimumCoverMatrix();
        char[][] matrix = new char[][]{
                "ABABA".toCharArray(),
                "ABABA".toCharArray()
        };
        System.out.println(finder.min(matrix)); //2
        matrix = new char[][]{
                "ABCABCA".toCharArray(),
                "ABDABDA".toCharArray(),
        };
        System.out.println(finder.min(matrix));  //6
        matrix = new char[][]{
                "ABCDABCA".toCharArray(),
                "ABDDABDA".toCharArray(),
                "ABCDABCA".toCharArray(),
        };
        System.out.println(finder.min(matrix));  //14
    }
}
