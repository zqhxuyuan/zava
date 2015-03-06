package com.interview.flag.l;

/**
 * Created_By: stefanie
 * Date: 15-1-8
 * Time: 下午5:57
 */
public class L5_IntegerComplement {
    public int getIntComplement(int number){
        int base = 1;
        int complement = 0;
        while(number > 0){
            if((number & 1) == 0) complement += base;
            number >>= 1;
            base <<= 1;
        }
        return complement;
    }

    public static void main(String[] args){
        L5_IntegerComplement complementor = new L5_IntegerComplement();
        System.out.println(complementor.getIntComplement(10)); //5
    }
}
