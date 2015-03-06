package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-9-7
 * Time: 下午3:39
 */
public class C11_24_XReplacement {
    private static boolean isMatch(String str, int start, String pattern) {
        for(int i =0; i< pattern.length(); i ++)
            if(str.charAt(start + i) != pattern.charAt(i))
                return false;
        return true;
    }
    public static String replace(String str, String pattern) {
        String result = "";
        for(int i = 0; i < str.length(); ) {
            if(isMatch(str, i, pattern)) {
                if(result.length() == 0 || result.charAt(result.length() - 1) != 'X')
                    result += 'X';
                i += pattern.length();
            }
            else {
                result += str.charAt(i);
                i ++;
            }
        }
        return result;
    }

}
