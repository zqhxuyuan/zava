package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-31
 * Time: 下午4:30
 */
public class LOJ163_MissingRanges {
    //create a util method: getRange(start, end) to handle "2" and "4->49" different range representation.
    //use a begin, init as start, to scan the vals,
    //  if begin < vals[i], create a range(begin, vals[i] - 1);
    //  whatever, set begin = vals[i] + 1;
    //remember to set the last range after scan: if(begin <= end) create a range(begin, end);
    public List<String> findMissingRanges(int[] vals, int start, int end) {
        List<String> ranges = new ArrayList();
        int begin = start;
        for(int i = 0; i < vals.length; i++){
            if(begin < vals[i]) ranges.add(getRange(begin, vals[i] - 1));
            begin = vals[i] + 1;
        }
        if(begin <= end) ranges.add(getRange(begin, end));
        return ranges;
    }

    public String getRange(int start, int end){
        if(start == end) return start + "";
        else return start + "->" + end;
    }
}
