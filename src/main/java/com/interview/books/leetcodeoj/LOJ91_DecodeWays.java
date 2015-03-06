package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午10:14
 */
public class LOJ91_DecodeWays {
    //edge case: s.charAt(0) == '0' return 0;
    //state: ways[i]: is the decode ways of s.substring(0, i);
    //initialize: ways[0] = 1, ways[1] = 1;
    //function: cur = s.charAt(i - 1), pre = s.charAt(i - 2)
    //          if(cur == '0')
    //              if(pre == '0' || pre > '2') return 0;
    //              else ways[i] = ways[i-2];
    //          else num = (pre - '0') * 10 + (cur - '0');
    //              if(num < 10 || num > 26) ways[i] = ways[i - 1];
    //              else ways[i] = ways[i-1] + ways[i-2];
    //result: ways[s.length]
    public int numDecodings(String s) {
        if(s == null || s.length() == 0 || s.charAt(0) == '0') return 0;
        int[] ways = new int[3];
        ways[0] = 1;
        ways[1] = 1;
        for(int i = 2; i <= s.length(); i++){
            char cur = s.charAt(i - 1);
            char pre = s.charAt(i - 2);
            if(cur == '0'){
                if(pre == '0' || pre > '2') return 0;
                else ways[i % 3] = ways[(i-2)%3];
            } else {
                int num = (pre - '0') * 10 + (cur - '0');
                if(num < 10 || num > 26) ways[i%3] = ways[(i-1)%3];
                else ways[i%3] = ways[(i-1)%3] + ways[(i-2)%3];
            }
        }
        return ways[s.length()%3];
    }
}
