package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-11-9
 * Time: 下午8:45
 */
public class C11_36_DecodeWays {
    public static int numDecodings(String s) {
        if (s.length() == 0) return 0;
        int[] sols = new int[s.length()];     //to avoid duplicated calculation
        return numDecodings(s, 0, sols);
    }

    private static int numDecodings(String s, int offset, int[] sols) {
        if (offset == s.length()) return 1;  //reach the end of str, have decode all the chars,
        if (offset == s.length() - 1) return (s.charAt(offset) == '0') ? 0 : 1;  //have only one char
        //have more than one char, recursively calling
        if (sols[offset] != 0) return sols[offset];
        char cur = s.charAt(offset);
        char next = s.charAt(offset + 1);
        if (cur == '0') return 0;  //invalid case
        else if (next == '0') {    // 10... or 40...
            if (cur != '1' && cur != '2') return 0;    //invalid case, such 40
            else sols[offset] = numDecodings(s, offset + 2, sols);  //valid case, 10 or 20, can only move 2 steps
        } else if (cur > '2' || cur == '2' && next > '6') { // > 26, can't be together with next, can only move 1 steps
            sols[offset] = numDecodings(s, offset + 1, sols);
        } else { // < 26, move 1 or 2 steps
            sols[offset] = numDecodings(s, offset + 1, sols) + numDecodings(s, offset + 2, sols);
        }
        return sols[offset];
    }

    public static int numDecodingsDP(String s){
        if(s.length() == 0 || s.charAt(0) == '0') return 0;  //invalid
        int[] sols = new int[s.length() + 1];
        sols[0] = 1;  //empty
        sols[1] = 1;
        for(int i = 2; i <= s.length(); i++){
            int cur = s.charAt(i - 1) - '0';
            int pre = s.charAt(i - 2) - '0';
            if(cur == 0){
                if(pre == 0 || pre > 2) return 0;   //30, invalid
                else sols[i] = sols[i - 2];
            } else {
                cur = pre * 10 + cur;
                if(cur > 26 || cur < 10) sols[i] = sols[i - 1];  // <10 -> pre == 0
                else sols[i] = sols[i - 1] + sols[i - 2];
            }
        }
        return sols[s.length()];
    }

    public static int numDecodingsDPConstantSpace(String s){
        if(s.length() == 0 || s.charAt(0) == '0') return 0;  //invalid
        int[] sols = new int[3];
        sols[0] = 1;  //empty
        sols[1] = 1;  //one char
        for(int i = 2; i <= s.length(); i++){ //loop on 2nd char to end
            int cur = s.charAt(i - 1) - '0';
            int pre = s.charAt(i - 2) - '0';
            if(cur == 0){
                if(pre == 0 || pre > 2) return 0;   //30, invalid
                else sols[i%3] = sols[(i - 2)%3];   //10 or 20, sols[i] == sols[i-2];
            } else {
                cur = pre * 10 + cur;    //calculate the num
                if(cur > 26 || cur < 10) sols[i%3] = sols[(i - 1)%3];  // cur<10 -> pre == 0 or num > 26, can be together with prev, sols[i] == sols[i-1]
                else sols[i%3] = sols[(i - 1)%3] + sols[(i - 2)%3];  //sols[i] == sols[i-1] + sols[i-2]
            }
        }
        return sols[s.length()%3];
    }
}
