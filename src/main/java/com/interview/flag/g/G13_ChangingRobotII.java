package com.interview.flag.g;

import com.interview.leetcode.utils.Point;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 15-1-1
 * Time: 下午2:20
 */
public class G13_ChangingRobotII {
    public static final int SERVER = 1;
    public static final int OBSTACKLES = 2;
    public static final int EMPTY = 0;

    public Point getPosition(int[][] grid){
        List<Point> machines = getMachinePositions(grid);

        int[][] distance = new int[grid.length][grid[0].length];
        for(Point machine : machines){
            calculateDistance(machine, grid, distance);
        }

        Point center = new Point(-1,-1);
        int minDis = Integer.MAX_VALUE;
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
                if(grid[i][j] == EMPTY && distance[i][j] != 0 && distance[i][j] < minDis){
                    minDis = distance[i][j];
                    center.x = i;
                    center.y = j;
                }
            }
        }
        return center;
    }

    private List<Point> getMachinePositions(int[][] grid){
        List<Point> machines = new ArrayList();
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
                if(grid[i][j] == SERVER) machines.add(new Point(i, j));
            }
        }
        return machines;
    }


    private int[][] calculateDistance(Point machine, int[][] grid, int[][] distance) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Queue<Point> queue = new LinkedList();
        queue.add(machine);
        visited[machine.x][machine.y] = true;
        int level = 0;
        while(!queue.isEmpty()){
            int levelSize = queue.size();
            for(int i = 0; i < levelSize; i++){
                Point point = queue.poll();
                distance[point.x][point.y] += level;
                enqueue(point.x + 1, point.y, grid, queue, visited);
                enqueue(point.x - 1, point.y, grid, queue, visited);
                enqueue(point.x, point.y + 1, grid, queue, visited);
                enqueue(point.x, point.y - 1, grid, queue, visited);
            }
            level++;
        }
        return distance;
    }

    private void enqueue(int x, int y, int[][] grid, Queue<Point> queue, boolean[][] visited){
        if(x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && !visited[x][y]){
            visited[x][y] = true;
            if(grid[x][y] == EMPTY) queue.add(new Point(x, y));
        }
    }

    public static void main(String[] args){
        int[][] grid = new int[][]{
                {EMPTY, SERVER, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, OBSTACKLES, OBSTACKLES, EMPTY, SERVER},
                {OBSTACKLES, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, SERVER, EMPTY, OBSTACKLES, EMPTY}
        };

        G13_ChangingRobotII finder = new G13_ChangingRobotII();
        Point center = finder.getPosition(grid);
        System.out.println(center.x + "-" + center.y);
    }
}
