package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 下午4:56
 */
public class TQ10_LastNumberMStepsDeletion {

    public int delete(int N, int M){
        if(N == 1) return 0;
        else return (delete(N - 1, M) + M) % N;
    }

    public static void main(String[] args){
        TQ10_LastNumberMStepsDeletion deletion = new TQ10_LastNumberMStepsDeletion();

        System.out.println(deletion.delete(6, 3)); //0
        System.out.println(deletion.delete(6, 2)); //4
    }
}
