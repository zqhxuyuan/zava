package com.interview.algorithms.design;

import com.interview.utils.ConsoleWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-8-22
 * Time: 上午9:31
 */
class Result {
    int max;
    int min;
    public Result(int max, int min){
        this.max = max;
        this.min = min;
    }
}
public class C9_3_AntGame {
    public int M;
    public int[] N;

    public C9_3_AntGame(int M, int[] N){
        this.M = M;
        this.N = N;
    }

    public Result findResult(){
        int min = Integer.MIN_VALUE;
        int half = M % 2 == 0? M / 2: (M+1)/2;
        for(int i = 0; i < N.length; i++){
            if(N[i] > half && M - N[i] > min) min = M - N[i];
            else if(N[i] < half && N[i] > min) min = N[i];
        }

        int max = N[N.length - 1];
        if(max < M - N[0]) max = M - N[0];

        return new Result(max, min);
    }

    public Map<Integer, boolean[]> find(){
        Map<Integer, boolean[]> solutions = new HashMap<>();
        boolean[] direction = new boolean[N.length];

        find(solutions, 0, direction);

        return solutions;
    }

    public void find(Map<Integer, boolean[]> solutions, int idx, boolean[] direction){
        if(idx > direction.length) return;
        if(idx == direction.length) {
            //ConsoleWriter.printBooleanArray(direction);
            int time = estimateTime(direction);
            //System.out.println(time);
            if(!solutions.containsKey(time)) solutions.put(time, direction);
            return;
        }
        direction[idx] = true;
        find(solutions, idx + 1, copyDirection(direction));
        direction[idx] = false;
        find(solutions, idx + 1, copyDirection(direction));
    }

    public Integer estimateTime(boolean[] direction){
        int[] n = copyLocation();
        int counter = 0;
        while(havntExit(n)){
            counter++;
            oneStep(n, direction);
        }

        return counter;
    }

    private void oneStep(int[] n, boolean[] direction){
        for(int i = 0; i < n.length; i++){
            if(n[i] <= M && direction[i]) n[i]++;
            else if(n[i] > 0 && !direction[i]) n[i]--;
        };

        for(int i = 0; i < n.length - 1; i++){
            for(int j = i+1; j < n.length; j++){
                if(!exit(n[i]) && !exit(n[j]) && n[i] == n[j]) {
                    direction[i] = !direction[i];
                    direction[j] = !direction[j];
                }
            }
        }

    }

    private boolean exit(int i){
        if(i > 0 && i <= M)   return false;
        else return true;
    }

    private boolean[] copyDirection(boolean[] direction){
        boolean[] dire = new boolean[direction.length];
        for(int i = 0; i < N.length; i++)   dire[i] = direction[i];
        return dire;
    }

    private int[] copyLocation(){
        int[] n = new int[N.length];
        for(int i = 0; i < N.length; i++)   n[i] = N[i];
        return n;
    }

    private boolean havntExit(int[] n){
        for(int i = 0; i < n.length; i++){
            if(!exit(n[i]))   return true;
        }
        return false;
    }
}
