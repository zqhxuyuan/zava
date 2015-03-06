package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午2:46
 */
public class LOJ14_LongestCommonPrefix {
    //use strs[0] as pivot and remember to check offset >= strs[i].length() for other str.
    public String longestCommonPrefix(String[] strs) {
        if(strs.length == 0) return "";
        int offset = 0;
        while(offset < strs[0].length()){
            boolean allSame = true;
            char expected = strs[0].charAt(offset);
            for(int i = 1; i < strs.length; i++){
                if(offset >= strs[i].length() || strs[i].charAt(offset) != expected){
                    allSame = false;
                    break;
                }
            }
            if(!allSame) break;
            offset++;
        }
        return strs[0].substring(0, offset);
    }
}
