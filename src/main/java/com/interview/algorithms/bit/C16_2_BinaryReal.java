package com.interview.algorithms.bit;

/**
 * Created_By: stefanie
 * Date: 14-10-10
 * Time: 下午11:08
 */
public class C16_2_BinaryReal {
    public static int BUFFER = 32;
    public static String binaryString(double i){
        if(i >= 1 && i < 0) return "ERROR";
        StringBuilder builder = new StringBuilder();
        builder.append("0.");
        while(i > 0 && builder.length() <= BUFFER + 2){
            i *= 2;
            if(i >= 1) {
                builder.append("1");
                i -= 1;
            }
            else builder.append("0");
        }
        if(i != 0) return "OVERFLOW";
        else return builder.toString();
    }
}
