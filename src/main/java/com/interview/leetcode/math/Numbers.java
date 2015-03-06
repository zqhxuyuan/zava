package com.interview.leetcode.math;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午9:03
 *
 * Handle the numbers
 * 1. swap a and b without using extra variable   {@link #swap(int, int)}
 * 2. divide 2 numbers without using multiple, division and mod operation   {@link #divide(int, int)}
 * 3. calculate the sqrt of a number  {@link #sqrt(int)}
 * 4. reverse digits of an integer.   {@link #reverse(int)}
 * 5. calculate the n pow of x, which is x^n  {@link #pow(double, int)}
 * 6. Given a non-negative number represented as an array of digits, plus one to the number. {@link #plusOne(int[])}
 *    The digits are stored such that the most significant digit is at the head of the list.
 * 7. Given a number, check if it's a palindrome. {@link #isPalindrome(int)}
 *
 */
public class Numbers {

    public static void swap(int a, int b){
        a = a + b;
        b = a - b;   // a + b - b = a
        a = a - b;   // a + b - a = b
    }

    public static int[] generatePrim(int N) {
        int[] primes = new int[N];
        int k = 0;
        int i = 2;
        while(k < N){
            int j = 0;
            for(; j < k; j++){
                if(i % primes[j] == 0) break;
            }
            if(j == k) primes[k++] = i;
            i++;
        }
        return primes;
    }

    public static int divide(int dividend, int divisor) {
        boolean negative = (dividend > 0 && divisor < 0) ||
                (dividend < 0 && divisor > 0);

        long a = Math.abs((long)dividend);
        long b = Math.abs((long)divisor);
        int ans = 0;

        while (a >= b) {
            int shift = 0;
            while ((b << shift) <= a) {
                shift++;
            }
            ans += 1 << (shift-1);
            a = a - (b << (shift-1));
        }
        return negative ? -ans : ans;
    }

    public static int sum(int a, int b){
        int sum, carry;
        do {
            sum = a ^ b;
            carry = (a & b) << 1;
            a = sum;
            b = carry;
        } while (b != 0);
        return a;
    }

    /**
     * do binary search between 0 ~ x, to find target * target = x
     */
    public static int sqrt(int x) {
        long lo = 0;
        long hi = x;

        while (hi >= lo) {
            long mid = (hi + lo)/2;
            if (x < mid * mid) {
                hi = mid-1;      // not hi = mid
            } else {
                lo = mid+1;
            }
        }
        return (int) hi;
    }

    /**
     * ret = ret * 10 + x % 10 and x /= 10 until x == 0
     * handle overflow/underflow by check the abs
     */
    public int reverse(int x) {
        int ret = 0;
        while (x != 0) {
            // handle overflow/underflow
            if (Math.abs(ret) > 214748364) {
                return 0;
            }
            ret = ret * 10 + x % 10;
            x /= 10;
        }
        return ret;
    }

    /**
     * use recursively call, and should consider n be positive and negative
     * 1. the edge case is:
     *      n == 0 return 1
     *      n == 1 return x;
     *      n == -1 return 1.0/x
     * 2. do the recursively call
     *      when n is even: pow(x, n / 2) * pow(x, n / 2);
     *      when n is odd:  pow(x, n / 2) * pow(x, n / 2) * (x or 1.0/x based on n > 0 or n < 0)
     */
    public double pow(double x, int n) {
        if(n == 0) return 1;
        else if(n == 1) return x;
        else if(n == -1) return 1.0 / x;
        if(n % 2 == 0){
            double pow = pow(x, n / 2);
            return pow * pow;
        } else {
            if(n > 0){
                double pow = pow(x, (n - 1)/2);
                return pow * pow * x;
            } else {
                double pow = pow(x, (n + 1)/2);
                return pow * pow * (1.0/x);
            }
        }
    }

    /**
     * using carry to hold when sum larger than 10
     */
    public int[] plusOne(int[] digits) {
        int carry = 1;
        for(int i = digits.length - 1; i >= 0; i--){
            int sum = digits[i] + carry;
            digits[i] = sum % 10;
            carry = sum / 10;
        }
        if(carry == 0) return digits;
        int[] newDigits = new int[digits.length + 1];
        newDigits[0] = 1;
        for(int i = 1; i < newDigits.length; i++) newDigits[i] = digits[i - 1];
        return newDigits;
    }

    /**
     * recursively call given a reverse of scanned number
     */
    public boolean isPalindrome(int x) {
        if(x < 0) return false;
        return isPalindrome(x, 0);
    }

    /**
     * prev is the reverse of scanned x.
     */
    private boolean isPalindrome(int x, int prev){
        int mod = x % 10;
        prev = prev * 10 + mod;
        if(prev == x) return true; //odd offset
        x = x / 10;
        if(prev == x) return true; //even offset
        if(prev == 0 && x != 0) return false;
        //when the lower offset contains 0, and higher is not empty, shouldn't have palindrome,
        //since the highest offset can't be 0
        if(x > 0)   return isPalindrome(x, prev);
        else return false;
    }

    /**
     * define a clear scan sequence for valid, use sign = -1/1 as the flag for positive and negative
     */
    static int max = Integer.MAX_VALUE / 10;
    public static int atoi(String str) {
        int i = 0; int sign = 1; int n = str.length(); int num = 0;
        while (i < n && Character.isWhitespace(str.charAt(i))) i++;
        if(i < n && str.charAt(i) == '+') i++;
        else if(i < n && str.charAt(i) == '-'){
            sign = -1;
            i++;
        }
        while(i < n && Character.isDigit(str.charAt(i))){
            int digit = Character.getNumericValue(str.charAt(i));
            if(num > max || num == max && digit >= 8)
                return sign == 1? Integer.MAX_VALUE : Integer.MIN_VALUE;
            num = num * 10 + digit;
            i++;
        }
        return sign * num;
    }

    /**
     * define a clear scan sequence for valid
     */
    public boolean isNumber(String str) {
        int i = 0; int n = str.length(); boolean isNumber = false;
        while(i < n && Character.isWhitespace(str.charAt(i))) i++;
        if(i < n && (str.charAt(i) == '+' || str.charAt(i) == '-')) i++;
        while(i < n && Character.isDigit(str.charAt(i))){
            isNumber = true;
            i++;
        }
        if(i < n && str.charAt(i) == '.'){
            i++;
            while(i < n && Character.isDigit(str.charAt(i))) {
                isNumber = true;
                i++;
            }
        }
        if(isNumber && i < n && str.charAt(i) == 'e'){
            i++;
            isNumber = false;
            if(i < n && (str.charAt(i) == '+' || str.charAt(i) == '-')) i++;
            while(i < n && Character.isDigit(str.charAt(i))) {
                isNumber = true;
                i++;
            }
        }
        while(i < n && Character.isWhitespace(str.charAt(i))) i++;
        return isNumber && i == n;
    }
}
