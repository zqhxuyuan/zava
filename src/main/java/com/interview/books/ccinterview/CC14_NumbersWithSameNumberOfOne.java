package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午4:25
 */
public class CC14_NumbersWithSameNumberOfOne {
    public static int next(int next){
        int number = next;
        int endZeroCount = 0;
        int midOneCount = 0;
        while((number & 1) == 0 && (number != 0)){   //count end zero
            endZeroCount++;
            number >>= 1;
        }
        while((number & 1) == 1){  //count middle one
            midOneCount++;
            number >>= 1;
        }
        //if n == 0 or can't find a larger number
        if(endZeroCount + midOneCount == 0 || endZeroCount + midOneCount == 31) return -1;

        int total = endZeroCount + midOneCount;
        next |= 1 << total;             //00000100000    rightmost zero to one
        next &= ~((1 << total) - 1);    //11111100000    clear left of rightmost to zero
        next |= (1 << (midOneCount - 1)) - 1;//00000000011    put rest 1 to the end
        return next;

//      return n + (1 << c0) + (1 << (c1 - 1)) - 1;

    }

    public static int prev(int prev){
        int number = prev;
        int midZeroCount = 0;
        int endOneCount = 0;
        while((number & 1) == 1){
            endOneCount++;
            number >>= 1;
        }
        if(number == 0) return -1;
        while((number & 1) == 0 && (number != 0)){
            midZeroCount++;
            number >>= 1;
        }

        int totol = midZeroCount + endOneCount;
        prev &= ((~0) << (totol + 1));        //clear from bit p onwards
        int mask = (1 << (endOneCount + 1)) - 1;//sequence of (c1 + 1) ones
        prev |= mask << (midZeroCount - 1);
        return prev;

//      return n - (1 << c1) - (1 << (c0 - 1)) + 1;
    }

    public static void main(String[] args){
        int n = 0x367C;      //0011011001111100
        System.out.println(Integer.toBinaryString(n));
        n = next(n);
        System.out.println(Integer.toBinaryString(n));
        System.out.println(Integer.toBinaryString(prev(n)));
    }
}
