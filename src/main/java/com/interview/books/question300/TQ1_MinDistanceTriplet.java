package com.interview.books.question300;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 下午10:26
 */
public class TQ1_MinDistanceTriplet {

    public int minDistance(int[] A, int[] B, int[] C, int[] result){
        if(A.length == 0 || B.length == 0 || C.length == 0) return Integer.MAX_VALUE;
        int[] idx = new int[3];
        boolean[] ended = new boolean[3];
        int[] cur = new int[3];
        cur[0] = A[0];
        cur[1] = B[0];
        cur[2] = C[0];
        int minDistance = Integer.MAX_VALUE;
        while(true){
            int distance = distance(cur);
            if(distance < minDistance){
                minDistance = distance;
                for(int i = 0; i < 3; i++) result[i] = cur[i];
            }
            int smallest = min(cur, ended);
            if(smallest == -1) break;
            idx[smallest]++;
            switch (smallest){
                case 0: if(idx[0] >= A.length) ended[0] = true;
                        else cur[0] = A[idx[0]];
                        break;
                case 1: if(idx[1] >= B.length) ended[1] = true;
                        else cur[1] = B[idx[1]];
                        break;
                case 2: if(idx[2] >= C.length) ended[2] = true;
                        else cur[2] = C[idx[2]];
                        break;
            }

        }
        return minDistance;
    }

    public int distance(int[] array){
        int distance = Integer.MIN_VALUE;
        for(int i = 0; i < array.length; i++){
            for(int j = i + 1; j < array.length; j++){
                distance = Math.max(distance, Math.abs(array[i] - array[j]));
            }
        }
        return distance;
    }

    public int min(int[] array, boolean[] ended){
        int min = -1;
        for(int i = 0; i < array.length; i++){
            if(!ended[i] && (min == -1 || array[i] < array[min])) min = i;
        }
        return min;
    }

    public static void main(String[] args){
        TQ1_MinDistanceTriplet finder = new TQ1_MinDistanceTriplet();
        int[] A = new int[]{1,5,7};
        int[] B = new int[]{3,5,12};
        int[] C = new int[]{4,9,10};
        int[] result = new int[3];
        int minDistance = finder.minDistance(A, B, C, result);
        System.out.println(minDistance);
        ConsoleWriter.printIntArray(result);
    }
}
