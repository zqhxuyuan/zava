package com.interview.books.svinterview;

import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午8:41
 */
public class SV20_YangMatrixTopK {
    class Cell implements Comparable<Cell>{
        int row, col;
        int value;

        Cell(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + col;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || !(obj instanceof Cell)) return false;
            Cell cellObj = (Cell) obj;
            if(this.row == cellObj.row && this.col == cellObj.col) return true;
            else return false;
        }

        @Override
        public int compareTo(Cell o) {
            return this.value - o.value;
        }
    }
    public int topK(int[][] matrix, int K){
        if(matrix.length == 0) return -1;
        int m = matrix.length;
        int n = matrix[0].length;
        if(K > m * n) return -1;
        HashSet<Cell> visited = new HashSet();
        PriorityQueue<Cell> minHeap = new PriorityQueue<>();

        Cell start = new Cell(0,0,matrix[0][0]);
        minHeap.add(start);
        visited.add(start);
        while(!minHeap.isEmpty() && K > 1){
            Cell cell = minHeap.poll();
            K--;
            if(cell.row + 1 < m) {
                Cell down = new Cell(cell.row + 1, cell.col, matrix[cell.row + 1][cell.col]);
                if(!visited.contains(down)) {
                    minHeap.add(down);
                    visited.add(down);
                }
            }
            if(cell.col + 1 < n) {
                Cell right = new Cell(cell.row, cell.col + 1, matrix[cell.row][cell.col + 1]);
                if(!visited.contains(right)) {
                    minHeap.add(right);
                    visited.add(right);
                }
            }
        }
        return minHeap.poll().value;
    }

    public static void main(String[] args){
        SV20_YangMatrixTopK finder = new SV20_YangMatrixTopK();
        int[][] matrix = new int[][]{
                {1,3,6,9},
                {2,4,7,10},
                {5,6,8,12}
        };
        for(int i = 1; i < 10; i++){
            System.out.println(finder.topK(matrix, i));
        }
    }
}
