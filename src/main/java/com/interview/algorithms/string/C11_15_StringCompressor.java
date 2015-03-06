package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-7-9
 * Time: 下午10:02
 *
 * Something need notice:
 *  1. need check if the compression string is smaller then the original one.
 *  2. when calculate compression length need notice number is not always 1 bit.
 *  3. using StringBuilder or StringBuffer to avoid string concatenation.
 */
public class C11_15_StringCompressor {

    public static String compress(String str) {
        if (str == null || str.length() == 0) return str;

        char[] chars = str.toCharArray();
        if (compressionCount(chars) >= chars.length) return str;

        StringBuilder builder = new StringBuilder();
        char last = chars[0];
        int count = 1;
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == last) count++;
            else {
                builder.append(last);
                builder.append(count);
                last = chars[i];
                count = 1;
            }
        }
        builder.append(last);
        builder.append(count);
        return builder.toString();
    }

    public static int compressionCount(char[] str) {
        int size = 0;

        char last = str[0];
        int count = 1;
        for (int i = 1; i < str.length; i++) {
            if (str[i] == last) count++;
            else {
                size += 1 + String.valueOf(count).length();
                last = str[i];
                count = 1;
            }
        }
        return size;
    }
}
