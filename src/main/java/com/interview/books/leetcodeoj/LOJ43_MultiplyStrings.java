package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-21
 * Time: 下午6:03
 */
public class LOJ43_MultiplyStrings {
    //loop for the lowest digit in num1 and num2, and tracking carry
    //1. int[] num3 = new int[num1.length() + num2.length()];
    //2. init carry = 0 in every loop of i
    //3. after loop of j put carry to num3[i]; // num3[i + j + 1] where j is -1;
    //4. num3 is stored from lowest to highest digit, so need visit reversely when put in StringBuffer.
    //5. offset from 0 and offset < num1.length() + num2.length() - 1, omit highest 0
    public String multiply(String num1, String num2) {
        if(num1 == null || num2 == null) return null;
        int[] num3 = new int[num1.length() + num2.length()];
        int carry = 0;
        for(int i = num1.length() - 1; i >= 0; i--){
            carry = 0;
            for(int j = num2.length() - 1; j >= 0; j--){
                int sum = num3[i + j + 1] + carry +
                        Character.getNumericValue(num1.charAt(i)) * Character.getNumericValue(num2.charAt(j));
                num3[i + j + 1] = sum % 10;
                carry = sum / 10;
            }
            num3[i] = carry;
        }
        StringBuffer buffer = new StringBuffer();
        int offset = 0;
        while(offset < num1.length() + num2.length() - 1 && num3[offset] == 0) offset++;
        while(offset < num1.length() + num2.length()) buffer.append(num3[offset++]);
        return buffer.toString();
    }
}
