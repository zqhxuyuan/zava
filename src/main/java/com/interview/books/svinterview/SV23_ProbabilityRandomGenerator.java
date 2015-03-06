package com.interview.books.svinterview;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午10:20
 */
public class SV23_ProbabilityRandomGenerator {
    int[] P;
    int[] Q;
    int sum;
    Random random = new Random();

    public SV23_ProbabilityRandomGenerator(int[] P){
        this.P = P;
        this.Q = new int[this.P.length];
        sum = 0;
        for(int i = 0; i < P.length; i++){
            sum += P[i];
            Q[i] = sum;
        }
    }

    private int find(int number){
        int low = 0;
        int high = Q.length - 1;
        while(low < high){
            int mid = low + (high - low)/2;
            if(number <= Q[mid]) high = mid;
            else low = mid + 1;
        }
        return low;
    }

    public int random(){
        int rand = random.nextInt(sum) + 1;
        int offset = find(rand);
        return offset;
    }

    public static void main(String[] args){
        int[] pro = new int[]{1,5,3,2,4};         //Q = [1,6,8,10,14]
        SV23_ProbabilityRandomGenerator generator = new SV23_ProbabilityRandomGenerator(pro);
        double[] marker = new double[5];
        for(int i = 0; i < 1000; i++){
            int rand = generator.random();
            marker[rand]++;
        }
        for(int i = 0; i < marker.length; i++) {
            System.out.println(marker[i] / 1000);
        }
    }
}
