package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午5:47
 */
public class LOJ118_PascalsTriangle {
    //keep prev list and generate current list: 0, i-1 + i, size() - 1;
    //assign current to prev, numRows--;
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> triangle = new ArrayList();
        if(numRows <= 0) return triangle;
        List<Integer> prev = new ArrayList();
        prev.add(1);
        triangle.add(prev);
        while(numRows > 1){
            List<Integer> current = new ArrayList();
            current.add(prev.get(0));
            for(int i = 1; i < prev.size(); i++){
                current.add(prev.get(i - 1) + prev.get(i));
            }
            current.add(prev.get(prev.size() - 1));
            triangle.add(current);
            prev = current;
            numRows--;
        }
        return triangle;
    }
}
