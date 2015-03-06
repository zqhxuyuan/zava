package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-30
 * Time: 下午6:52
 */
public class LOJ168_ExcelsheetColumnTitle {
    //convert to base26, special case for 1-26
    //since 'A' -> 1, 'B' -> 2, so do n-- to leftshift one every time.
    public String convertToTitle(int n) {
        StringBuffer buffer = new StringBuffer();
        while(n > 0){
            n--;
            char ch = (char)('A' + (n % 26));
            buffer.insert(0, ch);
            n = n / 26;
        }
        return buffer.toString();
    }
}
