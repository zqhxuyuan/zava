package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-1-26
 * Time: 下午2:46
 */
public class G30_GameOfLife {

    public boolean[][] nextState(boolean[][] grid){
        boolean[][] next = new boolean[grid.length][grid[0].length];
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
                int count = countNeighbors(grid, i, j);
                if(count <= 1 || count >= 4) next[i][j] = false; //will die
                else if(count == 3) next[i][j] = true; //will recovery
                else if(count == 2) next[i][j] = grid[i][j]; //will be stable
            }
        }
        return next;
    }

    private int countNeighbors(boolean[][] grid, int row, int col){
        int count = 0;
        for(int i = -1; i <= 1; i++){
            int nextRow = row + i;
            if(nextRow < 0 || nextRow >= grid.length) continue;
            for(int j = -1; j <= 1; j++){
                if(i == 0 && j == 0) continue;
                int nextCol = col + j;
                if(nextCol < 0 || nextCol >= grid[0].length) continue;
                if(grid[nextRow][nextCol]) count++;
            }
        }
        return count;
    }

    public static void main(String[] args){
        G30_GameOfLife game = new G30_GameOfLife();
        boolean[][] grid = new boolean[][]{
                {true, false, true, true},
                {true, true, false, true},
                {false, false, true, false}
        };

        grid = game.nextState(grid);
        for(int i = 0; i < grid.length; i++) ConsoleWriter.printBooleanArray(grid[i]);
//        {true, false, true, true},
//        {true, false, false, true},
//        {false, true, true, false}

        grid = game.nextState(grid);
        for(int i = 0; i < grid.length; i++) ConsoleWriter.printBooleanArray(grid[i]);
//        false true true true
//        true false false true
//        false true true false
        grid = game.nextState(grid);
        for(int i = 0; i < grid.length; i++) ConsoleWriter.printBooleanArray(grid[i]);
//        false true true true
//        true false false true
//        false true true false
        //in a stable status.
    }
}
