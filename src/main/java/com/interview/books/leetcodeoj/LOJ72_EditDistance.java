package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午2:38
 */
public class LOJ72_EditDistance {
    //state: distance[i][j]: the min edit distance of a.substring(0, i) and b.substring(0, j);
    //initialize: distance[i][0] = i and distance[0][j] = j
    //function: distance[i][j] = distance[i-1][j-1] if a.charAt(i - 1) == b.charAt(j - 1)
    //          distance[i][j] = min(distance[i-1][j-1], distance[i-1][j], distance[i][j-1]) + 1,
    //                                              if a.charAt(i - 1) != b.charAt(j - 1)
    //result: distance[a.length][b.length];
    public int minDistance(String a, String b) {
        int[][] distance = new int[a.length() + 1][b.length() + 1];
        for(int i = 0; i <= a.length(); i++) distance[i][0] = i;
        for(int j = 0; j <= b.length(); j++) distance[0][j] = j;
        for(int i = 1; i <= a.length(); i++){
            for(int j = 1; j <= b.length(); j++){
                if(a.charAt(i - 1) == b.charAt(j - 1)) distance[i][j] = distance[i-1][j-1];
                else {
                    distance[i][j] = Math.min(distance[i-1][j], distance[i][j-1]);
                    distance[i][j] = Math.min(distance[i][j], distance[i-1][j-1]);
                    distance[i][j] += 1;
                }
            }
        }
        return distance[a.length()][b.length()];
    }
}
