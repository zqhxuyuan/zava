package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午3:21
 */
public class LOJ17_LetterCombinationOfAPhoneNumber {
    //1.use String to hold all the options
    //2.init global class variable carefully
    //3.for recursive permutation generation, be carefully about when to return, if(offset >= digits.length())
    List<String> sols;
    String[] map;
    public List<String> letterCombinations(String digits) {
        sols = new ArrayList();
        map = initMap();
        decode(digits, 0, "");
        return sols;
    }

    private void decode(String digits, int offset, String prefix){
        if(offset >= digits.length()) {
            sols.add(prefix);
            return;
        }
        int digit = digits.charAt(offset) - '0';
        String options = map[digit];
        for(int i = 0; i < options.length(); i++){
            decode(digits, offset + 1, prefix + options.charAt(i));
        }
    }

    public String[] initMap(){
        return new String[]{"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
    }
}
