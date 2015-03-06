package com.interview.flag.g;

import com.interview.utils.models.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 15-2-8
 * Time: 下午5:16
 */
public class G53_PointCanGoToEverySea {
    int[][] counter;
    int[][] matrix;
    public List<Point> findPoints(int[][] matrix){
        this.matrix = matrix;
        counter = new int[matrix.length][matrix[0].length];
        int seaCount = 0;
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if(matrix[i][j] != 0 || counter[i][j] == -1) continue;
                seaCount++;
                searchPoints(i, j);
            }
        }
        List<Point> points = new ArrayList();
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if(counter[i][j] == seaCount) points.add(new Point(i, j));
            }
        }
        return points;
    }

    private void searchPoints(int row, int col){
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        Queue<Integer> queue = new LinkedList();
        visited[row][col] = true;
        counter[row][col] = -1;
        queue.offer(row * cols + col);

        while(!queue.isEmpty()){
            Integer position = queue.poll();
            int r = position / cols;
            int c = position % cols;
            enqueue(r + 1, c, visited, matrix[r][c], queue);
            enqueue(r - 1, c, visited, matrix[r][c], queue);
            enqueue(r, c + 1, visited, matrix[r][c], queue);
            enqueue(r, c - 1, visited, matrix[r][c], queue);
        }
    }

    private void enqueue(int row, int col, boolean[][] visited, int prev, Queue<Integer> queue){
        if(row < 0 || row >= matrix.length || col < 0 || col >= matrix[0].length || visited[row][col]) return;
        if(prev == 0 && matrix[row][col] == 0){  //connected sea
            counter[row][col] = -1;
            visited[row][col] = true;
            queue.add(row * matrix[0].length + col);
        } else if(matrix[row][col] >= prev){
            counter[row][col]++;
            visited[row][col] = true;
            queue.add(row * matrix[0].length + col);
        }
    }

    public static void main(String[] args){
        G53_PointCanGoToEverySea finder = new G53_PointCanGoToEverySea();
        int[][] matrix = new int[][]{
                {0, 0, 0, 1, 2, 3, 0},
                {0, 1, 2, 2, 4, 3, 2},
                {2, 1, 1, 3, 3, 2, 0},
                {0, 3, 3, 3, 2, 3, 3}
        };
        List<Point> points = finder.findPoints(matrix);
        for(Point point : points) System.out.println(point.toString());     //(1, 4)
    }
}
