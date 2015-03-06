package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午4:38
 */
public class LOJ50_PowX_N {
    //basic method to do n times multiply x, a better way to do in 2's exponent, every time double the result.
    //consider n to be positive or negative, the base case will be n == 0/1/-1
    //consider n to be even or odd, do p1 = pow(x, n/2) and p2 = pow(x, n - 2*(n/2));
    //if n is even, p2 == 1, n is odd, p2 = -1/1 based on n's flag.
    //return p1 * p1 * p2
    public double pow(double x, int n) {
        if(n == 0) return 1;
        else if(n == 1) return x;
        else if(n == -1) return 1.0/x;

        double p1 = pow(x, n/2);
        double p2 = pow(x, n - 2*(n/2));
        return p1 * p1 * p2;
    }
}
