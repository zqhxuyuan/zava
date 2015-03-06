package com.interview.algorithms.design;

/**
 * Created_By: stefanie
 * Date: 14-8-30
 * Time: 下午8:26
 */
public class C9_4_DivideFish {

    public static int minAmout(int person){
        int step = (int) Math.pow(5, person - 1);
        int back = 0;
        for(int i = person - 2; i >= 0; i--){
            back += Math.pow(5, i);
        }
        step -= back;
        int n = 1;
        while(!canDivide(n, person)) {
            //n += step;
            n++;
        }

        return n;
    }

    private static boolean canDivide(int n, int person){
        while(person > 0){
            if((n-1) % 5 == 0){
                n = (n-1)/5 * 4;
                person--;
            } else {
                return false;
            }
        }
        return true;
    }
}
