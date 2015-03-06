package com.interview.flag.a;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午6:04
 */
public class A4_LongestConsecutiveSequence {
    /**
     matrix question:
     given matrix like :

     a b e d
     b c f e
     a b d d
     ….

     find the longest path of consecutive alphabets given a starting alphabet.
     You can move in all 8 directions. for eg. a->b(right)->c(down)->d(diagnal down)… len = 4 ,
     find max such len.

     */


    public String maxConsecutiveSeq(char[][] matrix){
        if(matrix.length == 0) return "";
        String maxSeq = "";
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix.length; j++){
                String seq = dfs(matrix, i, j, "", ' ');
                if(seq.length() > maxSeq.length()) maxSeq = seq;
            }
        }
        return maxSeq;
    }

    public String dfs(char[][] matrix, int row, int col, String prefix, char prev){
        if(row < 0 || row >= matrix.length || col < 0 || col >= matrix[0].length) return prefix;
        if(prefix.length() != 0 && matrix[row][col] != prev + 1) return prefix;
        String maxSeq = prefix + matrix[row][col];
        for(int i = row - 1; i <= row + 1; i++){
            for(int j = col - 1; j <= col + 1; j++){
                String seq = dfs(matrix, i, j, prefix + matrix[row][col], matrix[row][col]);
                if(seq.length() > maxSeq.length()) maxSeq = seq;
            }
        }
        return maxSeq;
    }

    public static void main(String[] args){
        A4_LongestConsecutiveSequence finder = new A4_LongestConsecutiveSequence();
        char[][] matrix = new char[][]{
               "abed".toCharArray(),
               "bcfe".toCharArray(),
               "abdd".toCharArray(),
        };
        System.out.println(finder.maxConsecutiveSeq(matrix));

    }
}
