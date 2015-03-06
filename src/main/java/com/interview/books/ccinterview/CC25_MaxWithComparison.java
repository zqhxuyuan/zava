package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午11:00
 */
public class CC25_MaxWithComparison {

    //if a and b have different sign, a - b may cause overflow.
    public static int getMax(int a, int b){

        int sa = sign(a);
        int sb = sign(b);
        int sc = sign(a - b);

        int diff = sa ^ sb;        //if sa != sb, diff == 1
        int same = (sa ^ sb) ^ 1;  //if sa == sb, same == 1

        //if a and b have different sign, k = sign(a), if a and b have same sign, k = sign(a-b).
        int k = diff * sa + same * sc;
        int q = k ^ 1;

        return a * k + b * q;
    }

    //return 1 for positive, and 0 for negative
    public static int sign(int a){
        return ((a >> 31) & 1) ^ 1;
    }
}
