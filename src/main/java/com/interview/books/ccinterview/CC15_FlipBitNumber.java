package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午5:04
 */
public class CC15_FlipBitNumber {
    public static int count(int A, int B){
        int AXB = A ^ B;
        int count = 0;
        while(AXB != 0){
            count++;
            AXB &= (AXB - 1);
        }
        return count;
    }

    public static void main(String[] args){
        System.out.println(CC15_FlipBitNumber.count(31, 14));
    }
}
