package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-30
 * Time: 下午9:10
 */
public class LOJ159_LongestSubstringwithTwoDistinctChar {
    //use two pointer begin and next, begin is the begin of the substring, (next + 1) is the option of next begin
    //so char between (next + 1) and i-th should be the same, so the two distinct char is s[i-1] and s[next]
    //loop on every char
    //  if s[i] == s[i-1] just continue
    //  else if(next == -1) next = i-1;
    //  else if(s.charAt(i) == s.charAt(next)) next = i-1;
    //  else if(s.charAt(i) != s.charAt(next)) more than two char, max = Math.max(max, i-begin), begin = next+1, next = i-1;
    //so the condition is:
    //  if(s[i] == s[i-1]) continue;
    //  if(next != -1 && s.charAt(i) != s.charAt(next)) max = Math.max(max, i-begin), begin = next+1;
    //  next = i - 1;
    //at the end, need check max = Math.max(max, s.length() - begin);
    public int lengthOfLongestSubstringTwoDistinct(String s) {
        int max = 0;
        int begin = 0;
        int next = -1;
        for(int i = 1; i < s.length(); i++){
            if(s.charAt(i) == s.charAt(i - 1)) continue;
            if(next != -1 && s.charAt(i) != s.charAt(next)){
                max = Math.max(max, i - begin);
                begin = next + 1;
            }
            next = i - 1;
        }
        max = Math.max(max, s.length() - begin);
        return max;
    }
}
