package com.interview.algorithms.dp;

/**
 * Created_By: stefanie
 * Date: 14-9-20
 * Time: 上午9:36
 * Editing Distance: given two string A and B, could do the following actions to A to make it equals B:
 *  Add a char, Delete a char, Modify a char.
 * Write code to find the minimal actions needed to make A equals B, as the Editing Distance of A and B.
 *
 * When scan to the i-th char in A and j-th char in B, it depends on the previous states.
 *    if i-th char in A == j-th char in B:  dis[i][j] = dis[i-1][j-1]
 *    delete i-th char in A:    dis[i][j] = dis[i-1][j] + 1;
 *    delete j-th char in B:    dis[i][j] = dis[i][j-1] + 1;
 *    add j-th char in B to A:  dis[i][j] = dis[i-1][j] + 1;
 *    add i-th char in A to B:  dis[i][j] = dis[i][j-1] + 1;
 *    modify i-th char in A to j-th char in B:  dis[i][j] = dis[i-1][j-1] + 1
 *
 * so loop on i and j, and calculate to find the min action path.
 */
public class C12_24_EditingDistance {

    public static int distance(String a, String b){
        int[][] dis = new int[a.length() + 1][b.length() + 1];

        dis[0][0] = 0;
        for(int i = 1; i <= a.length(); i++)
            dis[i][0] = i;
        for(int i = 1; i <= b.length(); i++)
            dis[0][i] = i;

        for(int i = 1; i <= a.length(); i++){
            for(int j = 1; j <= b.length(); j++){
                dis[i][j] = a.charAt(i-1) == b.charAt(j-1)? dis[i-1][j-1] : dis[i-1][j-1] + 1;
                int ten = Math.min(dis[i-1][j], dis[i][j-1]) + 1;
                if(dis[i][j] > ten) dis[i][j] = ten;
            }
        }
        return dis[a.length()][b.length()];
    }


}
