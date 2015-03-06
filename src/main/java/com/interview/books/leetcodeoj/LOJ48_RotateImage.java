package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午2:49
 */
public class LOJ48_RotateImage {
    //use layer to visit matrix layer by layer
    //matrix is
    //      (layer, layer) .....    (layer, last)
    //              ...                 ...
    //      (last, layer)  .....    (last, last)
    public void rotate(int[][] matrix) {
        if(matrix.length == 0) return;
        for(int layer = 0; layer < matrix.length/2; layer++){
            int last = matrix.length - 1 - layer;
            for(int i = 0; i < last - layer; i++){
                int temp = matrix[layer][layer + i];
                matrix[layer][layer+i]  = matrix[last - i][layer];
                matrix[last - i][layer] = matrix[last][last - i];
                matrix[last][last - i]  = matrix[layer + i][last];
                matrix[layer + i][last] = temp;
            }
        }
    }
}
