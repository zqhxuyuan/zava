package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 下午1:59
 * The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows like this: (you may want to display this pattern in a fixed font for better legibility)
 *
 *      P   A   H   N
 *      A P L S I I G
 *      Y   I   R
 * And then read line by line: "PAHNAPLSIIGYIR"
 * Write the code that will take a string and make this conversion given a number of rows:
 *
 * string convert(string text, int nRows);
 * convert("PAYPALISHIRING", 3) should return "PAHNAPLSIIGYIR".
 */
public class C1_75_ZigZag {
    public static String convert(String s, int nRows) {
        if (nRows == 1 || nRows > s.length()) return s;
        List<Character>[] matrix = new ArrayList[nRows];
        for (int i = 0; i < nRows; i++) matrix[i] = new ArrayList<Character>();

        int row = 0;
        boolean down = true;
        for (char ch : s.toCharArray()) {
            if (down) {
                if (row < nRows - 1) matrix[row++].add(ch);
                else if (row == nRows - 1) {
                    matrix[row].add(ch);
                    down = false;
                }
            } else {
                if (row > 0) matrix[--row].add(ch);
                if (row == 0) {
                    down = true;
                    row++;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nRows; i++) {
            for (Character ch : matrix[i])
                builder.append(ch);
        }
        return builder.toString();
    }
}
