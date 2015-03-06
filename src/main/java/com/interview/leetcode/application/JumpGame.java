package com.interview.leetcode.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-26
 * Time: 下午5:00
 */
public class JumpGame {
    /**
     * Given an array of non-negative integers, you are initially positioned at the first index of the array.
     * Each element in the array represents your maximum jump length at that position.
     * Determine if you are able to reach the last index.
     */
    public boolean canJump(int[] A) {
        boolean[] can = new boolean[A.length];
        can[0] = true;
        for (int i = 1; i < A.length; i++) {
            for (int j = 0; j < i; j++) {
                if (can[j] && j + A[j] >= i) {
                    can[i] = true;
                    break;
                }
            }
        }
        return can[A.length - 1];
    }

    /**
     * Given an array of non-negative integers, you are initially positioned at the first index of the array.
     * Each element in the array represents your maximum jump length at that position.
     * Your goal is to reach the last index in the minimum number of jumps.
     * Find the min step of jump needed.
     */
    public int minSteps(int[] A) {
        int[] steps = new int[A.length];
        steps[0] = 0;
        for(int i = 1; i < A.length; i++){
            steps[i] = Integer.MAX_VALUE;
            if(A[0] >= i) {
                steps[i] = 1;
                continue;
            }
            for(int j = 1; j < i; j++){
                if(steps[j] != Integer.MAX_VALUE && j + A[j] >= i) {
                    steps[i] = Math.min(steps[i], steps[j] + 1);
                    if(steps[i] == 2) break;
                }
            }
        }
        return steps[A.length - 1];
    }

    /**
     * Given an array of non-negative integers, each element is the max jump length at that position,
     * You can jump both to the left and right.
     * Find out which elements can jump to A[0]
     */
    static class JumpToZero{
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

        public boolean dfs(int[] A, int node){
            if(visited[node] == true) return reachable[node];
            visited[node] = true;
            if((node - A[node] == 0)
                    || node - A[node] > 0 && dfs(A, node - A[node])
                    || node + A[node] < A.length && dfs(A, node + A[node])){
                reachable[node] = true;
            }
            return reachable[node];
        }
    }

    /**
     * Given an unsorted array of integers, find the length of the longest consecutive elements sequence.
     * For example, Given [100, 4, 200, 1, 3, 2],   The longest consecutive elements sequence is [1, 2, 3, 4].
     * Return its length: 4.
     */
    static class LongestConsecutiveSequence{
        public int longestConsecutive(int[] num) {
            if(num.length <= 1) return num.length;
            HashMap<Integer, Integer> map = new HashMap<>();
            for(int i : num) map.put(i, 0);
            int max = 1;
            for(int i : num){
                if(map.get(i) == 1) continue;
                int cur = i;
                int curMax = 1;
                while(map.containsKey(cur + 1)){
                    curMax++;
                    cur++;
                    map.put(cur, 1);
                }
                cur = i;
                while(map.containsKey(cur - 1)){
                    curMax++;
                    cur--;
                    map.put(cur, 1);
                }
                max = Math.max(curMax, max);
            }
            return max;
        }
    }
}
