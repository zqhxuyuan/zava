package com.interview.algorithms.array;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-9-4
 * Time: 下午5:25
 *
 * Given an array A[N], N is unknown. getNum() will return one of the number in the array, and return NULL when the array is empty.
 * Write a get() method to random get a number in the array with 1/N probability.
 *
 * every time scan the array, until it return null. and tracking the return value in nValue.
 * every time get one number, if(rand%i)==0 update nValue.
 * return ith number probability is i is selected 1/i and any j(i<j<N) is not be selected j-1/j.
 * so p = 1/i * i/i+1 * i+1/i+2 .... * n-1/n = 1/n
 *
 */
class Numbers{
    private Integer[] numbers;
    private int index = 0;

    public Numbers(Integer[] numbers) {
        this.numbers = numbers;
    }

    public Integer getNumber(){
        if(index < numbers.length) return numbers[index++];
        else return null;
    }
    public void reset(){
        index = 0;
    }
}
public class C4_52_RandomGet {
    public static Random r = new Random();

    public static Integer get(Numbers n){
        int i = 1;
        Integer nVal = 0;
        Integer nRet = 0;
        while((nRet = n.getNumber()) != null){
            if(r.nextInt() % (i++) == 0)    nVal = nRet;
        }
        return nVal;
    }

}
