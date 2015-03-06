package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 15-1-26
 * Time: 下午4:27
 */
public class G31_MinDistanceForPolice {
    public static final int CLOSED = 1;
    public static final int OPENED = 0;
    public static final int POLICE = 2;

    public int[][] minDistance(int[][] grid){
        int[][] distance = new int[grid.length][grid[0].length];

        for(int i = 0; i < distance.length; i++) Arrays.fill(distance[i], Integer.MAX_VALUE);

        Queue<Integer> queue = new LinkedList();

        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
                if(grid[i][j] == POLICE){
                    distance[i][j] = 0;
                    queue.offer(i * grid[0].length + j);
                }
            }
        }
        BFSVisit(grid, distance, queue);
        return distance;
    }

    private void BFSVisit(int[][] grid, int[][] distance, Queue<Integer> queue){
        int cols = grid[0].length;
        int step = 0;
        while(!queue.isEmpty()){
            int queueSize = queue.size();
            for(int k = 0; k < queueSize; k++){
                int position = queue.poll();
                int i = position / cols;
                int j = position % cols;
                distance[i][j] = step;
                enqueue(queue, i + 1, j, grid, distance);
                enqueue(queue, i - 1, j, grid, distance);
                enqueue(queue, i, j + 1, grid, distance);
                enqueue(queue, i, j - 1, grid, distance);
            }
            step++;
        }
    }

    private void enqueue(Queue<Integer> queue, int i, int j, int[][] grid, int[][] distance) {
        if(i < 0 || i >= grid.length || j < 0 || j >= grid[0].length
                || grid[i][j] == CLOSED || distance[i][j] != Integer.MAX_VALUE) return;
        queue.offer(i * grid[0].length + j);
    }

    public static void main(String[] args){
        G31_MinDistanceForPolice calculator = new G31_MinDistanceForPolice();
        int[][] houses = new int[][]{
                {1,0,0,2,0,0,1,2},
                {0,1,0,1,0,0,0,0},
                {1,0,0,1,0,0,1,0}
        };
        int[][] distance = calculator.minDistance(houses);
        ConsoleWriter.printIntArray(distance);
//        {~,2,1,0,1,2,~,0},
//        {~,~,2,~,2,3,2,1},
//        {~,4,3,~,3,4,~,2}
    }
}
