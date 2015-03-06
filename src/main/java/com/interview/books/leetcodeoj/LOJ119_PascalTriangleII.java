package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午7:11
 */
public class LOJ119_PascalTriangleII {
    //copy the last element in new row, and scan backward: row.set(i, row.get(i) + row.get(i-1));
    //remember rowIndex--;
    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList();
        if(rowIndex < 0) return row;
        row.add(1);
        while(rowIndex > 0){
            row.add(row.get(row.size() - 1));
            for(int i = row.size() - 2; i >= 1; i--){
                row.set(i, row.get(i) + row.get(i-1));
            }
            rowIndex--;
        }
        return row;
    }
}
