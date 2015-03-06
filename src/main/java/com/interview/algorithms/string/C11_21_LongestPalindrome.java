package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-8-4
 * Time: 下午9:41
 *
 * Also can be solve using Suffix Tree: http://m.blog.csdn.net/blog/beyond_boy/8066447
 */
public class C11_21_LongestPalindrome {

    //Solution: http://blog.csdn.net/ggggiqnypgjg/article/details/6645824
    public static String findOptimized(String str){
        char[] chars = new char[str.length()*2 -1];
        chars[0] = str.charAt(0);
        for(int i = 1; i < str.length(); i++){
            chars[i*2 -1] = '#';
            chars[i*2] = str.charAt(i);
        }

        int mx = 0, id = 0;
        int[] p = new int[chars.length];
        for (int i = 1; i < chars.length; i++) {
            p[i] = mx > i ? Math.min(p[2*id-i], mx-i) : 1;
            while (i-p[i] >= 0 && i + p[i] < chars.length && chars[i + p[i]] == chars[i - p[i]]) p[i]++;
            if (i + p[i] > mx) {
                mx = i + p[i];
                id = i;
            }
        }
        int maxP = 0;
        for(int i = 1; i < p.length; i++) {
            if(p[i] > p[maxP]) maxP = i;
        }

        int length = p[maxP];
        int pos = (maxP - length) % 2 == 0? 2 : 1;
        char[] pChars = new char[length - pos / 2];
        for(int i = 0; i < pChars.length; i++){
            pChars[i] = chars[(maxP - length) + 2 * i + pos];
        }
        return String.copyValueOf(pChars);
    }

    public static String findBetter(String s){
        int left = 0, right = 0, range = 0;
        for(int i = 0; i < s.length(); i++) {
            for(int j = 0; j <= Math.min(i, s.length()-i-1) && s.charAt(i-j) == s.charAt(i+j);j++) {
                if(range < 2*j+1) {
                    range = 2 * j + 1;
                    left = i - j;
                    right = i + j;
                }
            }
            for(int j = 1; j <= Math.min(i+1, s.length()-i-1) && s.charAt(i+1-j) == s.charAt(i+j); j++) {
                if(range < 2*j) {
                    range = 2 * j;
                    left = i - j + 1;
                    right = i + j;
                }
            }
        }
        return (right >= s.length())?s.substring(left): s.substring(left, right+1);
    }

    public static String find(String str){
        int longest = 1;
        int begin = 0;
        int end = 1;
        for(int i = 0; i < str.length() - 1 && str.length() - i > longest; i++){
            int p = i;
            int q = str.length() - 1;
            int count = 0;
            while(p < q){
                if(str.charAt(p) != str.charAt(q)) {
                    if(count > 0){
                        q += count;
                        p = i;
                        int recount = 0;
                        while(count > 0 && str.charAt(q) == str.charAt(q-1)){
                            q--;
                            p++;
                            count--;
                            recount++;
                        }
                        count = recount;
                    }
                } else {
                    p++;
                    count++;
                }
                q--;
            }
            int len = q + count - p + count;
            if(len > longest) {
                longest = len;
                begin = p - count;
                end = q + count + 1;
            }
        }
        return str.substring(begin, end);
    }


}
