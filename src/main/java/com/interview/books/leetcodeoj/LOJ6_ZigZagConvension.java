package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 上午11:05
 */
public class LOJ6_ZigZagConvension {
    //use n StringBuffer to store data in each row.
    //1. init StringBuffer array
    //2. careful about row increase and decrease, and when to set down
    public String convert(String s, int nRows) {
        if(s == null || s.length() == 0 || nRows <= 1 || s.length() <= nRows) return s;
        StringBuffer[] buffer = new StringBuffer[nRows];
        for(int i = 0; i < buffer.length; i++){
            buffer[i] = new StringBuffer();
        }
        int offset = 0;
        int row = 0;
        boolean down = true;
        while(offset < s.length()){
            buffer[row].append(s.charAt(offset++));
            if(down){
                if(row < nRows - 1){
                    row++;
                } else {
                    row--;
                    down = false;
                }
            } else {
                if(row > 0){
                    row--;
                } else {
                    row++;
                    down = true;
                }
            }
        }
        for(int i = 1; i < buffer.length; i++){
            buffer[0].append(buffer[i].toString());
        }
        return buffer[0].toString();
    }
}
