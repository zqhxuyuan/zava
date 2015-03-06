package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 上午11:01
 */
public class LOJ93_RestoreIPAddress {
    //based on permutation
    //1.offset == chars.length && count == 0, prefix is a valid solution
    //2.calculate available char, if available < count || available > count * 3, it's not a valid solution
    //3.permutate on different solution: of 1 ~ 3 chars, and offset + i <= chars.length on loop condition
    //4.invalid option: option.length() > 1 && option.charAt(0) == '0' and Integer.parseInt(option) > 255
    List<String> sols;
    public List<String> restoreIpAddresses(String s) {
        sols = new ArrayList<String>();
        restoreIPAddresses(s.toCharArray(), 0, "", 4);
        return sols;
    }

    public void restoreIPAddresses(char[] chars, int offset, String prefix, int count){
        if(offset == chars.length && count == 0){
            sols.add(prefix);
            return;
        }
        int available = chars.length - offset;
        if(available < count || available > count * 3) return;
        for(int i = 1; i < 4 && offset + i <= chars.length; i++){
            String option = String.valueOf(chars, offset, i);
            if(!valid(option)) break;
            if(prefix.length() > 0) option = prefix + "." + option;
            restoreIPAddresses(chars, offset + i, option, count - 1);
        }
    }

    public boolean valid(String option){
        if(option.length() > 1 && option.charAt(0) == '0') return false;
        if(Integer.parseInt(option) > 255) return false;
        return true;
    }
}
