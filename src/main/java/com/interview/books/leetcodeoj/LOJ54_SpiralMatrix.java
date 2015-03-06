package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午5:59
 */
public class LOJ54_SpiralMatrix {
    //visit by layer: top, right, bottom and left.
    //int board = Math.min(rows, cols) + 1;
    //if rows != cols, layer should loop from [0 to board/2 - 1]
    //when layer == board/2 - 1 and Math.min(rows, cols) % 2 == 1, do loop the last two round on bottom and left.
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> numbers = new ArrayList();
        if(matrix.length == 0) return numbers;
        int rows = matrix.length;
        int cols = matrix[0].length;
        int board = Math.min(rows, cols) + 1;
        for(int layer = 0; layer < board/2; layer++){
            numbers.add(matrix[layer][layer]);
            for(int i = layer + 1; i < cols - layer; i++) numbers.add(matrix[layer][i]);
            for(int i = layer + 1; i < rows - layer; i++) numbers.add(matrix[i][cols-layer-1]);
            if(layer == board/2 - 1 && Math.min(rows, cols) % 2 == 1) break;
            for(int i = cols-layer-2; i >= layer; i--) numbers.add(matrix[rows-layer-1][i]);
            for(int i = rows-layer-2; i > layer; i--) numbers.add(matrix[i][layer]);
        }
        return numbers;
    }
}
