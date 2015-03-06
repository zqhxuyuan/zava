package com.interview.books.leetcode;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午7:53
 */
public class LCS14_StringReplaceWithX {

    public String replace(String base, String pattern){
        if(base == null || base.length() == 0 || pattern == null || pattern.length() == 0) return base;
        //for KMP
        int[] next = calNext(pattern);
        StringBuilder builder = new StringBuilder();
        int offset = 0;
        boolean isFirst = true;
        while(offset <= base.length() - pattern.length()){
            int found = matchFirstKMP(base, pattern, offset, next);
            if(found != -1){
                if(isFirst || found != offset){
                    if(isFirst) isFirst = false;
                    builder.append(base.substring(offset, found));
                    builder.append("X");
                }
                offset = found + pattern.length();
            } else {
                builder.append(offset);
            }
        }
        return builder.toString();
    }

    public int matchFirstKMP(String base, String pattern, int start, int[] next){
        int i = start, j = 0;
        while(i < base.length() && j < pattern.length()){
            if(pattern.charAt(j) == base.charAt(i)){
                i++;
                j++;
            } else if(j == 0)  i++;
            else j = next[j];
            if(j == pattern.length()) return i - j;
        }
        return -1;
    }

    private static int[] calNext(String pattern){
        int[] next = new int[pattern.length()];
        int front = 0, back = -1;   //init front from 0, back from -1
        next[0] = -1;
        while(front < next.length - 1){
            if(back == -1 || pattern.charAt(front) == pattern.charAt(back))
                next[++front] = ++back;
            else back = next[back];
        }
        return next;
    }

    public static void main(String[] args){
        LCS14_StringReplaceWithX replacer = new LCS14_StringReplaceWithX();
        String base = "abcdeffdfegabcabc";
        //XdeffdfegX
        System.out.println(replacer.replace(base, "abc"));
    }
}
