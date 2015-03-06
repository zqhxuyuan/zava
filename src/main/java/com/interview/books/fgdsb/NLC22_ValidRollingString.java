package com.interview.books.fgdsb;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 下午3:22
 */
public class NLC22_ValidRollingString {
    public boolean validate(String s, int A, int K){
        int count = (int) Math.pow(A, K);
        if(s.length() < count + K - 1) return false;

        boolean[] mark = new boolean[count];

        int base = 0;
        int highest = (int) Math.pow(A, K - 1);
        for(int idx = 0; idx < s.length(); idx++) {
            if(idx >= K) {
                base -= Character.getNumericValue(s.charAt(idx - K)) * highest;
            }
            int current = Character.getNumericValue(s.charAt(idx));
            if(current >= A) return false;
            base = base * A + current;
            if(!mark[base]) {
                mark[base] = true;
                count--;
            }
        }
        return count == 0;
    }

    public static void main(String[] args){
        NLC22_ValidRollingString checker = new NLC22_ValidRollingString();
        System.out.println(checker.validate("00110", 2, 2));//true
        System.out.println(checker.validate("0011", 2, 2)); //false
        System.out.println(checker.validate("001103",2,2));//false);
        System.out.println(checker.validate("10110",2,2));//false);
        System.out.println(checker.validate("00110",2,2));//true);
        System.out.println(checker.validate("00111",2,2));//false);
        System.out.println(checker.validate("4537860129",10,1));//true);
        System.out.println(checker.validate("0123456789",10,2));//false);
    }
}
