package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 下午3:05
 */
public class TQ8_Lucky7Sequence {

    public static int encode(int number){
        int encoded = 0;
        int times = 1;
        while(number > 0){
            int mod = number % 9;
            if(mod >= 7) mod++;
            encoded += mod * times;
            number /= 9;
            times *= 10;
        }
        return encoded;
    }

    public static int decode(int number){
        int decoded = 0;
        int times = 1;
        while(number > 0){
            int mod = number % 10;
            if(mod >= 7) mod--;
            decoded += mod * times;
            number /= 10;
            times *= 9;
        }
        return decoded;
    }

    public static void main(String[] args){
        int encoded = TQ8_Lucky7Sequence.encode(65);
        System.out.println(encoded);
        System.out.println(TQ8_Lucky7Sequence.decode(encoded));

        encoded = TQ8_Lucky7Sequence.encode(71);
        System.out.println(encoded);
        System.out.println(TQ8_Lucky7Sequence.decode(encoded));

        encoded = TQ8_Lucky7Sequence.encode(78);
        System.out.println(encoded);
        System.out.println(TQ8_Lucky7Sequence.decode(encoded));

        encoded = TQ8_Lucky7Sequence.encode(86);
        System.out.println(encoded);
        System.out.println(TQ8_Lucky7Sequence.decode(encoded));
    }
}
