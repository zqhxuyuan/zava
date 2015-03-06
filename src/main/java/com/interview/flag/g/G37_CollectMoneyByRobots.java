package com.interview.flag.g;

/**
 * Created by stefanie on 1/27/15.
 */
public class G37_CollectMoneyByRobots {
    
    public int maxMoney(int[][] matrix){
        int N = matrix.length;
        int M = matrix[0].length;
        int T = N + M;
        
        int[][][] money = new int[T-1][N][N];
        
        money[0][0][0] = matrix[0][0];
        
        for(int s = 1; s < T - 1; s++){
            for(int i = 0; i < s && i < N; i++){
                if(s-i >= M) continue;
                for(int j = 0; j <= s && j < N; j++){
                    if(s-j >= M) continue;
                    int pre = Math.max(Math.max(money[s-1][i][j], i == 0 || j == 0? 0 : money[s-1][i-1][j-1]),
                            Math.max(i == 0 ? 0 : money[s-1][i-1][j], j == 0? 0 : money[s-1][i][j-1]));
                    if(i != j) money[s][i][j] = pre + matrix[i][s-i] + matrix[j][s-j];
                    else money[s][i][j] = pre + matrix[i][s-i];
                }
            }
        }
        
        return money[T-2][N-1][N-1];
    }
    
    public static void main(String[] args){
        G37_CollectMoneyByRobots collector = new G37_CollectMoneyByRobots();
        int[][] matrix = new int[][]{
                {1,5,4,1},
                {2,0,7,1},
                {1,4,2,1} 
        };
        System.out.println(collector.maxMoney(matrix)); //28
    }
}
