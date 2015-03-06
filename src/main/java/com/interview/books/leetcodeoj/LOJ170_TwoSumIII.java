package com.interview.books.leetcodeoj;

import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-12-31
 * Time: 下午6:02
 */
public class LOJ170_TwoSumIII {
    private HashMap<Integer, Integer> numbers = new HashMap();

    public void add(int number) {
        if(numbers.containsKey(number)) numbers.put(number, numbers.get(number) + 1);
        else numbers.put(number, 1);
    }

    public boolean find(int value) {
        for(Map.Entry<Integer, Integer> entry : numbers.entrySet()){
            int number = entry.getKey();
            if(value - number == number){
                if(entry.getValue() > 1) return true;
            } else {
                if(numbers.containsKey(value - number)) return true;
            }
        }
        return false;
    }

}
