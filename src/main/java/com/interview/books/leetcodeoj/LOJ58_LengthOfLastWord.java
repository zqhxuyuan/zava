package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午7:36
 */
public class LOJ58_LengthOfLastWord {
    //string.length()
    //be careful of index begin and end.
    public int lengthOfLastWord(String s) {
        if(s == null || s.length() == 0) return 0;
        int end = s.length() - 1;
        while(end >= 0 && s.charAt(end) == ' ') end--;
        if(end == -1) return 0;
        int begin = end;
        while(begin >= 0 && s.charAt(begin) != ' ') begin--;
        return end - begin;
    }
}
