package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-27
 * Time: 下午2:58
 */

/**
 * Given an array of non-negative integers, each element is the max jump length at that position,
 * You can jump both to the left and right.
 * Find out which elements can jump to A[0]
 */
public class G6_JumpGame {

    boolean[] reachable;
    boolean[] visited;
    public List<Integer> jumpToZero(int[] A){
        reachable = new boolean[A.length];
        visited = new boolean[A.length];

        List<Integer> reachableNodes = new ArrayList<>();
        for(int i = 1; i < A.length; i++){
            if(!visited[i]) dfs(A, i);
            if(reachable[i]) reachableNodes.add(i);
        }

        return reachableNodes;
    }

    public boolean dfs(int[] A, int idx){
        if(idx < 0 || idx >= A.length) return false;   //for over the range
        if(visited[idx] == true) return reachable[idx];//for already visited

        visited[idx] = true;
        if((idx - A[idx] == 0) || dfs(A, idx - A[idx]) || dfs(A, idx + A[idx])) reachable[idx] = true;
        return reachable[idx];
    }

    public static void main(String[] args){
        G6_JumpGame game = new G6_JumpGame();
        int[] steps = new int[]{1,3,0,2,4,7};
        List<Integer> positions = game.jumpToZero(steps);
        ConsoleWriter.printCollection(positions); //output: {1,3,4}
    }
}
