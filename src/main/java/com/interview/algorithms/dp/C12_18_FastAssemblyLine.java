package com.interview.algorithms.dp;

/**
 * Created_By: stefanie
 * Date: 14-6-26
 * Time: 下午8:13
 *
 * The is N assembly line, which have M station, each station operator the same function with different time defined in lines.
 * Product can transfer among stations between two assembly line with certain transition time defined in transitions.
 *
 * Please find the fastest way to assemble a product and give the selection on station on each lines.
 *
 * for each station i on each line j
 *      time[j][i] = min( time[j][i-1]+line[j][i], time[k][i-1]+transition[k][j][i]+line[j][i] other k != j)
 * at the end, find the min of time[j][N-1], and backtrace the solution
 *
 */
public class C12_18_FastAssemblyLine {

    public static int[] find(int[][] lines, int[][][] transition){
        int lineNum = lines.length;
        int stationNum = lines[0].length;

        int[][] time = new int[lineNum][stationNum];
        int[][] solution = new int[lineNum][stationNum];

        for(int i = 0; i < lineNum; i++){
            solution[i][0] = i;
            time[i][0] = lines[i][0];
        }

        for(int i = 1; i < stationNum; i++){
            for(int j = 0; j < lineNum; j++){
                time[j][i] = Integer.MAX_VALUE;
                //for each station on each line, find the min value from previous i-1 station from k and save k as the solution
                for(int k = 0; k < lineNum; k++){
                    int t = j == k? time[j][i-1] + lines[j][i] : time[k][i-1] + transition[k][j][i] + lines[j][i];
                    if(t < time[j][i]){
                        time[j][i] = t;
                        solution[j][i] = k;
                    }
                }
            }
        }

        int min = time[0][stationNum - 1];
        int index = 0;
        for(int i = 1; i < lineNum; i++){
            if(time[i][stationNum - 1] < min) {
                min = stationNum;
                index = i;
            }
        }

        int[] r = new int[stationNum];
        r[stationNum - 1] = index;
        for(int i = stationNum - 1; i > 0; i--){
           r[i - 1] = solution[r[i]][i];
        }

        return r;
    }
}
