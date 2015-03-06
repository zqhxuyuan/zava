package com.interview.flag.g;

/**
 * Created by stefanie on 1/28/15.
 */
public class G47_LargestRectangleWithBadget {
    
    public int[] maxRectangle(int[][] matrix, int K){
        int[] rows = new int[2];
        int[] cols = new int[2];
        int max = 0;
        
        for(int i = 0; i < matrix.length; i++){
            int[] sum = new int[matrix[0].length];
            for(int j = i; j < matrix.length; j++){
                for(int k = 0; k < matrix[0].length; k++) sum[k] += matrix[j][k];
                int[] current = findClosest(sum, K, j-i+1, max); 
                if(current != null){
                    max = current[0];
                    rows[0] = i;
                    rows[1] = j;
                    cols[0] = current[1];
                    cols[1] = current[2];
                }
            }
        }
        return new int[]{max, rows[0], cols[0], rows[1], cols[1]};
    }
    
    private int[] findClosest(int[] array, int K, int rows, int max){
        int[] range = new int[]{max, -1, -1};
        int sum = 0;
        int begin = 0;
        for(int i = 0; i < array.length; i++){
            sum += array[i];
            if(sum > K){        //shrink beginning
                while(sum > K) sum -= array[begin++];
            }
            int area = (i - begin + 1) * rows;
            if(area > range[0]) {
                range[0] = area;
                range[1] = begin;
                range[2] = i;
            }
        }
        return range[0] == max? null : range;
    }
    
    public static void main(String[] args){
        G47_LargestRectangleWithBadget finder = new G47_LargestRectangleWithBadget();
        int[][] matrix = new int[][]{
                {4,2,1,3},
                {3,2,0,0},
                {1,2,1,1}
        };
        int[] rectangle = finder.maxRectangle(matrix, 15);
        System.out.printf("Found max rectangle area: %d, from (%d, %d) to (%d, %d)\n", rectangle[0], 
                rectangle[1], rectangle[2], rectangle[3], rectangle[4]);
        
    }
}
