package com.interview.books.ccinterview;

import com.interview.utils.ConsoleWriter;
import com.interview.utils.models.Point;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午7:54
 */
public class CC20_PaintFill {
    public void paint(int[][] matrix, int x, int y, int color){
        if(matrix == null || matrix.length == 0) return;
        if(!withinMatrix(matrix, x, y) || matrix[x][y] == color) return;

        int originalColor = matrix[x][y];
        Queue<Point> queue = new LinkedList();
        queue.add(new Point(x, y));
        while(!queue.isEmpty()){
            Point point = queue.poll();
            matrix[point.x][point.y] = color;
            paintNeighbor(matrix, point.x + 1, point.y, originalColor, color, queue);
            paintNeighbor(matrix, point.x - 1, point.y, originalColor, color, queue);
            paintNeighbor(matrix, point.x, point.y + 1, originalColor, color, queue);
            paintNeighbor(matrix, point.x, point.y - 1, originalColor, color, queue);
        }
    }
    
    private void paintNeighbor(int[][] matrix, int x, int y, int original, int color, Queue<Point> queue){
        if(withinMatrix(matrix, x, y) && matrix[x][y] == original) {
            matrix[x][y] = color;
            queue.add(new Point(x, y));
        }
    }
    
    private boolean withinMatrix(int[][] matrix, int x, int y){
        if(x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length) return true;
        else return false;
    }
    
    public static void main(String[] args){
        int[][] matrix = new int[][]{
                {1,2,2,2,4,5,6},
                {4,3,2,4,4,4,7},
                {1,2,3,4,5,6,7}
        };
        CC20_PaintFill painter = new CC20_PaintFill();
        painter.paint(matrix, 1, 3, 7);
        ConsoleWriter.printIntArray(matrix);
    }
}
