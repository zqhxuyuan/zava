package com.interview.books.question300;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;

/**
 * Created by stefanie on 1/21/15.
 */
public class TQ52_OtherProducts {
    public int[] products(int[] num){
        int total = 1;
        int zeroCount = 0;
        int zeroIdx = -1;
        for(int i = 0; i < num.length; i++) {
            if(num[i] == 0) {
                zeroIdx = i;
                zeroCount++;
            } else {
                total *= num[i];
            }
        }

        int[] products = new int[num.length];
        if(zeroCount >= 2){
            Arrays.fill(products, 0);
        } else if(zeroCount == 1){
            Arrays.fill(products, 0);
            products[zeroIdx] = total;
        } else {
            for(int i = 0; i < num.length; i++) products[i] = total / num[i];
        }
        return products;
    }

    public static void main(String[] args){
        TQ52_OtherProducts calculator = new TQ52_OtherProducts();
        int[] nums = new int[]{1,2,3,4,5,6,7,8};
        ConsoleWriter.printIntArray(calculator.products(nums));  //40320, 20160, 13440, 10080, 8064, 6720, 5760, 5040,
        nums = new int[]{1,2,3,4,5,0,7,8};
        ConsoleWriter.printIntArray(calculator.products(nums));  //0, 0, 0, 0, 0, 6720, 0, 0,
        nums = new int[]{1,2,0,4,5,0,7,8};
        ConsoleWriter.printIntArray(calculator.products(nums));  //0, 0, 0, 0, 0, 0, 0, 0,
    }
}
