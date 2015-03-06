package com.interview.flag.g;

import com.interview.leetcode.utils.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 15-1-15
 * Time: 下午6:03
 */
public class G27_WhereRainGoes {
    int[][] matrix;
    int[][] marker;
    boolean[][] visited;
    Queue<Point> queue;

    public List<Point> findPoints(int[][] matrix){
        List<Point> points = new ArrayList();
        this.matrix = matrix;
        this.marker = new int[matrix.length][matrix[0].length];
        this.queue = new LinkedList();

        topLeft();
        downRight();

        for(int i = 0; i < marker.length; i++){
            for(int j = 0; j < marker[0].length; j++){
                if(marker[i][j] == 2) points.add(new Point(i, j));
            }
        }
        return points;
    }

    private void topLeft(){
        this.visited = new boolean[matrix.length][matrix[0].length];
        for(int i = 0; i < matrix[0].length; i++) {
            queue.add(new Point(0, i));
            visited[0][i] = true;
        }
        for(int i = 1; i < matrix.length; i++) {
            queue.add(new Point(i, 0));
            visited[i][0] = true;
        }
        BFS();
    }

    private void downRight(){
        this.visited = new boolean[matrix.length][matrix[0].length];
        for(int i = 0; i < matrix[0].length; i++) {
            queue.add(new Point(matrix.length - 1, i));
            visited[matrix.length - 1][i] = true;
        }
        for(int i = 0; i < matrix.length - 1; i++) {
            queue.add(new Point(i, matrix[0].length - 1));
            visited[i][matrix[0].length - 1] = true;
        }
        BFS();
    }

    private void BFS(){
        while(!queue.isEmpty()){
            Point point = queue.poll();
            int value = matrix[point.x][point.y];
            marker[point.x][point.y]++;
            enqueue(point.x + 1, point.y, value);
            enqueue(point.x - 1, point.y, value);
            enqueue(point.x, point.y + 1, value);
            enqueue(point.x, point.y - 1, value);
        }
    }

    private void enqueue(int x, int y, int value){
        if(x < 0 || x >= matrix.length || y < 0 || y >= matrix[0].length || visited[x][y]) return;
        if(matrix[x][y] > value) {
            queue.add(new Point(x, y));
            visited[x][y] = true;
        }
    }

    public static void main(String[] args){
        G27_WhereRainGoes finder = new G27_WhereRainGoes();
        int[][] matrix = new int[][]{
                {3,2,1},
                {4,5,1},
                {5,6,1},
                {6,6,6}
        };
        List<Point> points = finder.findPoints(matrix);
        for(Point point : points) System.out.println(point.x + "," + point.y);
    }
}
