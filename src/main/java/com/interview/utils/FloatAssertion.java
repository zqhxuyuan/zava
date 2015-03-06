package com.interview.utils;

import java.text.DecimalFormat;

/**
 * Created_By: stefanie
 * Date: 15-1-16
 * Time: 下午3:54
 */
public class FloatAssertion {
    static DecimalFormat FORMATTER = new DecimalFormat(".00000");
    static String ZERO = "0.00000";
    static Double EPSILON = 0.00001;

    public static boolean isZero(double number){
        if(number == 0.0 || number == -0.0) return true;
        else return FORMATTER.format(number).equals(ZERO);
    }

    public static boolean equals(double num1, double num2){
        if(num1 == num2) return true;
        else return FORMATTER.format(num1).equals(FORMATTER.format(num2));
    }

    public static int compareTo(double num1, double num2){
        Double formatted1 = Double.valueOf(FORMATTER.format(num1));
        Double formatted2 = Double.valueOf(FORMATTER.format(num2));
        return formatted1.compareTo(formatted2);
    }

    public static boolean larger(double value, double base){
        return value > base - EPSILON;
    }

    public static boolean smaller(double value, double base){
        return value < base + EPSILON;
    }

    public static boolean inRange(double value, double low, double high){
        return value > low - EPSILON && value < high + EPSILON;
    }

    public static String toString(double value){
        return FORMATTER.format(value);
    }

}
