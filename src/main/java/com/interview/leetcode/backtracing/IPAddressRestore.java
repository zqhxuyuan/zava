package com.interview.leetcode.backtracing;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-24
 * Time: 下午3:53
 */
public class IPAddressRestore {
    List<String> sols;
    public List<String> restoreIpAddresses(String s) {
        sols = new ArrayList<String>();
        restoreIPAddresses(s.toCharArray(), 0, "", 4);
        return sols;
    }

    public void restoreIPAddresses(char[] chars, int offset, String prefix, int count){
        if(count == 0 && offset == chars.length){
            sols.add(prefix);
            return;
        }
        int available = chars.length - offset;
        if(available < count || available > count * 3) return;
        for(int i = 1; i < 4 && offset + i <= chars.length; i++){
            String option = String.valueOf(chars, offset, i);
            if(!valid(option)) break;
            String next = option;
            if(prefix.length() > 0) next = prefix + "." + next;
            restoreIPAddresses(chars, offset + i, next, count - 1);
        }
    }

    public boolean valid(String option){
        if(option.length() > 1 && option.charAt(0) == '0') return false;
        if(Integer.parseInt(option) > 255) return false;
        return true;
    }

    public static void main(String[] args){
        IPAddressRestore restorer = new IPAddressRestore();
        List<String> sols = restorer.restoreIpAddresses("0000");
        ConsoleWriter.printCollection(sols);
    }
}
