package com.interview.books.ninechapter;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 上午10:07
 */
public class NC3_FairGenerator {

    //generate 0 and 1 with possibility p and 1 - p;
    public static int randP(){
        int number = new Random().nextInt(4); //generate 0,1,2,3
        if(number >= 1) return 1;
        else return 0;
    }

    //generate 0 and 1 in the same possibility
    public static int randFair(){
        int i = 0;
        int j = 0;
        while(i == j){
            i = randP();
            j = randP();
        }
        if(i == 0 && j == 1) return 1;
        else return 0;
    }

    //Write code to generate 1-N in same possibility 1/N, generate the binary version of 1-N
    public static int randN(int n){
        double pow = Math.ceil(Math.log(n)/Math.log(2)); //how many bit in N's binary version

        int i = n;
        while(i >= n) i = randP(pow);  //generate each bit
        return i + 1;
    }

    private static int randP(double size){  //generate a random number have given bit
        int k = 0;
        for(int i = 0; i < size; i++){
            k = k << 1;  //k = k * 2;
            if(randFair() == 1) k++;
        }
        return k;
    }

    public static void main(String[] args){
        NC3_FairGenerator generator = new NC3_FairGenerator();
        testRandFair(generator);
        testRandN(generator, 3);
        testRandN(generator, 5);
    }


    public static void testRandFair(NC3_FairGenerator generator){
        int[] possP = new int[2];
        int[] possF = new int[2];
        for(int i = 0; i < 10000; i++){
            int rand = generator.randP();
            possP[rand]++;
            rand = generator.randFair();
            possF[rand]++;
        }
        System.out.printf("Generate random 0 and 1 with possibility p: \n");
        for(int i = 0; i < possP.length; i++){
            float p = possP[i]/10000.0f;
            System.out.println(i + ": " + p);
        }

        System.out.printf("Generate random 0 and 1 with same possibility: \n");
        for(int i = 0; i < possF.length; i++){
            float p = possF[i]/10000.0f;
            System.out.println(i + ": " + p);
        }
    }

    public static void testRandN(NC3_FairGenerator generator, int N){
        int[] possN = new int[N + 1];
        for(int i = 0; i < 10000; i++){
            int rand = generator.randN(N);
            possN[rand]++;
        }
        System.out.printf("Generate random 1-%d Number: \n", N);
        for(int i = 1; i < possN.length; i++) {
            float p = possN[i] / 10000.0f;
            System.out.println(i + ": " + p);
        }
    }


}
