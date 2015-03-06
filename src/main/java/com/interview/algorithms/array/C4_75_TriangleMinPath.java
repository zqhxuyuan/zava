package com.interview.algorithms.array;

import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 下午6:01
 */
public class C4_75_TriangleMinPath {

    public static int minPath(List<List<Integer>> triangle) {
        if(triangle.size() == 0) return 0;
        int[] path = new int[triangle.size()];
        path[0] = triangle.get(0).get(0);
        for(int row = 1; row < triangle.size(); row++){
            List<Integer> layer = triangle.get(row);
            int prev = path[0];
            for(int col = 0; col <= row; col++){
                if(col == 0 || col == row) path[col] = prev + layer.get(col);
                else {
                    int current = path[col];
                    path[col] = Math.min(prev, current) + layer.get(col);
                    prev = current;
                }
            }
        }
        int min = path[0];
        for(int i = 1; i < path.length; i++){
            if(path[i] < min) min = path[i];
        }
        return min;
    }
}
