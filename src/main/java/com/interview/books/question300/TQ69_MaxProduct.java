package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 15-1-25
 * Time: 下午10:58
 */
public class TQ69_MaxProduct {

    public int product(int[] array){
        int total = 1;
        int zeroCount = 0;
        int largestNegative = Integer.MIN_VALUE;
        int smallestPositive = 0;

        for(int i = 0; i < array.length; i++){
            if(array[i] == 0) zeroCount++;
            else {
                total *= array[i];
                if(array[i] > 0 && array[i] < smallestPositive) smallestPositive = array[i];
                else if(array[i] < 0 && array[i] > largestNegative) largestNegative = array[i];
            }
        }

        if(zeroCount > 1) return 0;
        else if(zeroCount == 1){
            return total > 0? total : 0;
        } else {
            if(total > 0) return total / smallestPositive;
            else return total / largestNegative;
        }
    }

    public static void main(String[] args){
        TQ69_MaxProduct finder = new TQ69_MaxProduct();
        int[] array = new int[]{1, 7, 1, -4, 4, 2, 10, 8, -3, -4};
        System.out.println(finder.product(array)); //71680

        array = new int[]{1, 7, 1, 0, 4, 2, 10, 8, -3, -4};
        System.out.println(finder.product(array)); //53760

        array = new int[]{1, 7, 0, -4, 4, 2, 10, 8, -3, -4};
        System.out.println(finder.product(array));  //0

        array = new int[]{1, 7, 0, -4, 4, 0, 10, 8, -3, -4};
        System.out.println(finder.product(array));  //0
    }
}
