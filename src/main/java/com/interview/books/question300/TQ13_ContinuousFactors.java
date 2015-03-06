package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 下午8:06
 */
public class TQ13_ContinuousFactors {

    //Time: O(N)
    public int count(int N) {
        int count = 0;
        int sum = 1;
        int start = 1;
        for (int i = start + 1; i <= (N+1)/2; i++) {
            sum += i;
            while (sum >= N) {
                if (sum == N) count++;
                sum -= start;
                start++;
            }
        }
        return count;
    }

    public static void main(String[] args){
        TQ13_ContinuousFactors counter = new TQ13_ContinuousFactors();
        System.out.println(counter.count(15)); //3
    }
}
