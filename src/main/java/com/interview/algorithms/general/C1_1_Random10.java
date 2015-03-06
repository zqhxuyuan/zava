package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/29/14
 * Time: 11:35 AM
 */
public class C1_1_Random10 {
    public int rand7(){
        return (int) ((7 * Math.random()) % 7  + 1);
    }

    public int rand10(){
        int prod = 49;
        while(prod > 40){
            prod = (rand7() - 1) * 7 + rand7();
        }
        return prod % 10 + 1;
    }

    public static void main(String[] args) {
        int[] marker1 = new int[8];
        int[] marker2 = new int[11];
        C1_1_Random10 random = new C1_1_Random10();
        for(int i = 0; i < 1000000; i++){
            int rand = random.rand7();
            //System.out.println(rand);
            marker1[rand]++;
            rand = random.rand10();
            marker2[rand]++;
        }
        for(int i = 1; i < 8; i++){
            System.out.println(marker1[i]);
        }
        System.out.println("-----------------------");
        for(int i = 1; i < 11; i++){
            System.out.println(marker2[i]);
        }

    }
}
