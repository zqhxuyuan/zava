package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 上午11:14
 */
public class C1_74_BinaryStringAdd {
    public static String addBinary(String a, String b) {
        StringBuilder builder = new StringBuilder();
        int carry = 0;
        int la = a.length() - 1;
        int lb = b.length() - 1;
        int common = Math.min(a.length(), b.length());
        for(int i = 0; i < common; i++){
            carry = sum(a.charAt(la - i), b.charAt(lb - i), carry, builder);
        }
        String remain = a.length() == common? b : a;
        int left = remain.length() - common;
        for(int i = left - 1; i >= 0; i--){
            carry = sum(remain.charAt(i), '0', carry, builder);
        }
        if(carry == 1) builder.insert(0, 1);
        cleanZero(builder);
        return builder.toString();
    }

    private static int sum(char cha, char chb, int carry, StringBuilder builder){
        if(cha == '1' && chb == '1') {
            builder.insert(0, carry);
            return 1;
        } else if(cha == '0' && chb == '0'){
            builder.insert(0, carry);
            return 0;
        } else {
            if(carry == 1) builder.insert(0, 0);
            else builder.insert(0, 1);
            return carry;
        }
    }

    private static void cleanZero(StringBuilder builder){
        for(int i = 0; i < builder.length() - 1 && builder.charAt(i) == '0';){
            builder.deleteCharAt(0);
        }
    }
}
