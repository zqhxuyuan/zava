package com.interview.leetcode.matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-1
 * Time: 下午9:45
 *
 * Given a matrix contains true and false, write code to find the max sub matrix which border is black (true)
 *
 */
public class MaxMatrixWithBlackBorder {
    static class Matrix{
        int x;
        int y;
        int width;
        int height;
    }

    static class Range{
        int start;
        int end;
        public Range(int start, int end){
            this.start = start;
            this.end = end;
        }
    }

    public static Matrix maxMatrix(boolean[][] matrix){
        Matrix max = new Matrix();
        for(int i = 0; i < matrix.length - 1; i++){
            List<Range> borders = border(matrix, i);
            for(Range r : borders){
                Matrix m = maxMatrix(matrix, i, r);
                if(m != null && m.width * m.height > max.width * max.height) max = m;
            }
        }
        return max;
    }

    public static List<Range> border(boolean[][] matrix, int row){
        List<Range> ranges = new ArrayList<Range>();
        int start = -1;
        for(int j = 0; j < matrix[row].length; j++){
            if(start == -1){
                if(matrix[row][j]) start = j;
            } else {
                if(!matrix[row][j]){
                    ranges.add(new Range(start, j));
                    start = -1;
                }
            }
        }
        if(start != -1 && start != matrix[row].length - 1)
            ranges.add(new Range(start, matrix[row].length - 1));
        return ranges;
    }

    public static Matrix maxMatrix(boolean[][] matrix, int row, Range range){
        Matrix max = new Matrix();
        int maxrow = row + 1;
        while(maxrow < matrix.length && matrix[maxrow][range.start] && matrix[maxrow][range.end]) maxrow++;
        for(int i = maxrow - 1; i > row; i--){
            for(int j = range.start; j <= range.end; j++){
                if(!matrix[i][j]) break;
            }
            max.x = row;
            max.y = range.start;
            max.width = range.end - range.start;
            max.height = i - row;
            return max;
        }
        return null;
    }
}
