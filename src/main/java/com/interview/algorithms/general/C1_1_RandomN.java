package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/29/14
 * Time: 3:30 PM
 */
public class C1_1_RandomN {
    public int M;
    public int N;

    public C1_1_RandomN(int m, int n) {
        M = m;
        N = n;
    }

    public int randM(){
        return (int) ((M * Math.random()) % M  + 1);
    }

    public int randN(){
        if(M == N){
            return randM();
        } else if(M > N) {
            int rand = Integer.MAX_VALUE;
            while(rand > N) rand = randM();
            return rand;
        } else {
            int prod = Integer.MAX_VALUE;
            int threshold = ((M * M) / N) * N;
            while(prod > threshold){
                prod = (randM() - 1) * M + randM();
            }
            return prod % N + 1;
        }
    }

    public void test(){
        int[] marker1 = new int[M + 1];
        int[] marker2 = new int[N + 1];

        for(int i = 0; i < 1000000; i++){
            int rand = this.randM();
            marker1[rand]++;
            rand = this.randN();
            marker2[rand]++;
        }
        System.out.println("----------- Test Result ------------");
        System.out.println("Random" + M + ": ");
        for(int i = 1; i <= M; i++){
            System.out.println(marker1[i]);
        }
        System.out.println("Random" + N + ": ");
        for(int i = 1; i <= N; i++){
            System.out.println(marker2[i]);
        }
    }

    public static void main(String[] args) {
        C1_1_RandomN random = new C1_1_RandomN(7, 10);
        random.test();

        random = new C1_1_RandomN(10, 7);
        random.test();
    }
}
