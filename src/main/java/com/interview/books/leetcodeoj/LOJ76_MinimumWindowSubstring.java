package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午5:05
 */
public class LOJ76_MinimumWindowSubstring {
    //use two int[256] as expected and found to scan T and S
    //once found all the chars in T, shrink begin to get minimum window
    //while(begin < S.length()) when shrink begin
    //update window: if(window == "" || i - begin + 1 < window.length()) window = S.substring(begin, i + 1);
    public String minWindow(String S, String T) {
        if(S == null || S.length() == 0) return S;
        if(T == null || T.length() == 0 || T.length() > S.length()) return "";

        int[] expected = new int[256];
        for(int i = 0; i < T.length(); i++) expected[T.charAt(i)]++;
        int[] found = new int[256];
        String window = "";
        int count = 0; int begin = 0;
        for(int i = 0; i < S.length(); i++){
            char ch = S.charAt(i);
            found[ch]++;
            if(found[ch] <= expected[ch]) count++;
            if(count == T.length()){
                while(begin < S.length()){
                    char bch = S.charAt(begin);
                    if(found[bch] > expected[bch]){
                        found[bch]--;
                        begin++;
                    } else break;
                }
                if(window == "" || i - begin + 1 < window.length()) window = S.substring(begin, i + 1);
            }
        }
        return window;
    }
}
