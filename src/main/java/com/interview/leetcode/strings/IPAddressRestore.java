package com.interview.leetcode.strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-16
 * Time: 下午1:39
 * Given a string containing only digits, restore it by returning all possible valid IP address combinations.
 * For example: Given "25525511135", return ["255.255.11.135", "255.255.111.35"]. (Order does not matter)
 *
 * It's a application question of combination, but have strict rules to put every element:
 *   1. make sure rest string can find a solution: rest string can partition to left element, every element have 1 digits and most 3 digits
 *   2. the element itself is valid, <255 and not begin with 0
 * Backtracing also can use to find the answer
 */
public class IPAddressRestore {

    public List<String> restoreIpAddresses(String s) {
        List<String> addresses = new ArrayList<String>();
        if(s.length() < 4 || s.length() > 12) return addresses;
        String[] current = new String[4];
        restoreIpAddresses(s, 0, current, 0, addresses);
        return addresses;
    }

    private void restoreIpAddresses(String s, int pos, String[] current, int index, List<String> addresses){
        int leftNum = current.length - index - 1;
        for(int i = 1; i < 4; i++){
            int using = pos + i;
            if(s.length() - using < leftNum) break;  //left string can't be partite to leftNum, every position have 1 digits
            if(s.length() - using > leftNum * 3) continue;  //left string more than leftNum, every position have 3 digits
            String num = s.substring(pos, using);
            if(Integer.parseInt(num) > 255 || (num.length() > 1 && num.startsWith("0"))) break; //invalid;
            current[index] = num;
            if(index == current.length - 1){  //complete the partition
                StringBuilder builder = new StringBuilder();
                for(int j = 0; j < current.length; j++) builder.append(current[j] + ".");
                builder.deleteCharAt(builder.length() - 1); //remove last "."
                addresses.add(builder.toString());
                return;
            } else {  //partition the following num
                restoreIpAddresses(s, using, current, index + 1, addresses);
            }
        }
    }


}
