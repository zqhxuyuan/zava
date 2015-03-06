package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午7:27
 */
public class LOJ120_Triangle {
    //DP, loop top-down,
    //state: path[i] is the path from root to i-th element in current layer
    //initialize: path[0] = triangle.get(0).get(0);
    //function: loop layer from 1 to triangle.size() - 1, current = triangle.get(i)
    //      for j from current.size() - 1 to 0:
    //          j == current.size() - 1 path[j] = path[j-1] + current.get(i);
    //          j == 0                  path[j] = path[0] + current.get(i);
    //          other                   path[j] = Math.min(path[j], path[j-1]) + current.get(j);
    //result: min value in path[*]
    public int minimumTotal(List<List<Integer>> triangle) {
        if(triangle == null || triangle.size() == 0) return 0;
        int layer = triangle.size();
        int[] path = new int[layer];
        path[0] = triangle.get(0).get(0);
        for(int i = 1; i < layer; i++){
            List<Integer> current = triangle.get(i);
            for(int j = current.size() - 1; j >= 0; j--){
                if(j == current.size() - 1) path[j] = path[j - 1] + current.get(j);
                else if(j == 0)             path[j] = path[j] + current.get(j);
                else                        path[j] = Math.min(path[j - 1], path[j]) + current.get(j);
            }
        }
        int min = path[0];
        for(int i = 1; i < path.length; i++){
            if(path[i] < min) min = path[i];
        }
        return min;
    }

    public static void main(String[] args){
        List<List<Integer>> triangle = new ArrayList();
        List<Integer> row = new ArrayList<Integer>();
        row.add(-1);
        triangle.add(row);
        row = new ArrayList<>();
        row.add(2);
        row.add(3);
        triangle.add(row);
        row = new ArrayList<>();
        row.add(1);
        row.add(-1);
        row.add(-3);
        triangle.add(row);
        LOJ120_Triangle finder = new LOJ120_Triangle();
        System.out.println(finder.minimumTotal(triangle));
    }
}
