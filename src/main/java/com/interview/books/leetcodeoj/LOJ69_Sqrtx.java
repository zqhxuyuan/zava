package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午1:31
 */
public class LOJ69_Sqrtx {
    //do binary search on range[0, x], using long to avoid overflow of mid * mid
    //if can't find a sqrt, check the low * low <= x? return low otherwise return low - 1;
    //be careful of the change of int and long
    public int sqrt(int x) {
        long low = 0;
        long high = x;
        while(low < high){
            long mid = low + (high - low)/2;
            long square = mid * mid;
            if(square == x) return (int) mid;
            else if(x < square) high = mid - 1;
            else low = mid + 1;
        }
        return low * low <= x? (int) low : (int)low - 1;
    }

    public static void main(String[] args){
        LOJ69_Sqrtx finder = new LOJ69_Sqrtx();
        System.out.println(finder.sqrt(2147483647));
    }
}
