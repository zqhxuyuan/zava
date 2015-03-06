package com.interview.books.svinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-5
 * Time: 下午4:32
 */
public class SV3_PairDividedByK {
    public static boolean checkPair(int[] array, int k){
        int[] mod = new int[k];
        for(int i = 0; i < array.length; i++) mod[array[i]%k]++;
        if(mod[0] % 2 != 0) return false;    //the count of element can be divided by K is not even, so someone can't find a pair
        if(k % 2 == 0 && mod[k/2] % 2 != 0) return false;   //k is even, and the count of k/2 is not even, so someone can't find a pair
        for(int i = 1; i < k/2; i++){
            if(mod[i] != mod[k-i]) return false;  //element mod is i should be pair with element mod is k-i
        }
        return true;
    }

    public static void main(String[] args){
        int[] array = new int[]{4,5,7,9,11,12};
        System.out.println(checkPair(array, 8));
        array = new int[]{3,4,5,7,8,9,11,12};
        System.out.println(checkPair(array, 8));
    }
}
