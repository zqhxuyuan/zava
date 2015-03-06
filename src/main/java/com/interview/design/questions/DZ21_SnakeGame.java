package com.interview.design.questions;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Created_By: stefanie
 * Date: 15-1-3
 * Time: 下午4:50
 */
public class DZ21_SnakeGame {
    class Cell{
        int row;
        int col;
        public Cell(int row, int col){
            this.row = row;
            this.col = col;
        }
    }
    class Snake{
        private Cell currentHead;
        private Queue<Cell> queue = new LinkedList();
        int initSnakeLength;

        public void init(){
            currentHead = new Cell(0,0);
            queue.add(currentHead);
        }
        public Cell getCurrentHead(){
            return currentHead;
        }
        public void addCell(Cell cell){
            queue.offer(cell);
        }
        public Cell removeTail(){
            return queue.poll();
        }
        public int length(){
            return queue.size();
        }
    }
    class Board{
        public static final int EMPTY = 0;
        public static final int SNAKE = 1;
        public static final int FRUIT = 2;

        int rows;
        int cols;
        int[][] store;
        int fruitCount;

        public Board(int rows, int cols){
            this.rows = rows;
            this.cols = cols;
            this.store = new int[rows][cols];
        }

        public void init(File configFile){
            //init board from config file
        }

        public boolean validCell(int row, int col){
            if(row >= 0 && row < rows && col >= 0 && col < cols && store[row][col] == SNAKE) return false;
            else return true;
        }

        public int getCellData(Cell cell){
            return store[cell.row][cell.col];
        }

        public void updateCell(Cell cell, int value){
            store[cell.row][cell.col] = value;
        }
    }
    class Display{
        public void paint(Board board){
            //show the board;
        }
        public void showGameOver(){
            //show the game over
        }
    }
    class Game {
        public static final int UP = 0;
        public static final int DOWN = 1;
        public static final int LEFT = 2;
        public static final int RIGHT = 3;

        int currentLevel = 0;
        Board board;
        Snake snake;
        Display display;
        boolean gameover = false;
        int direction = RIGHT;

        public Game(int boardRows, int boardCols){
            board = new Board(boardRows, boardCols);
            snake = new Snake();
            display = new Display();
        }

        private void initGame(int level){
            //board.init(config file);
            //snake.init();
        }

        public void play() throws InterruptedException {
            while(!gameover){
                initGame(currentLevel);
                while(board.fruitCount > 0){
                    Cell head = snake.getCurrentHead();
                    Cell next = nextCell(head, this.direction);
                    if(next == null){
                        gameover = true;
                        break;
                    } else {
                        int data = board.getCellData(next);
                        snake.addCell(next);
                        board.updateCell(next, Board.SNAKE);
                        if(data == Board.FRUIT) board.fruitCount--;
                        if(data != Board.FRUIT && snake.length() > snake.initSnakeLength){
                            Cell last = snake.removeTail();
                            board.updateCell(last, Board.EMPTY);
                        }
                    }
                    display.paint(board);
                    TimeUnit.SECONDS.sleep(1);
                }
                if(board.fruitCount == 0) currentLevel++;
            }
            display.showGameOver();
        }

        private Cell nextCell(Cell current, int direction){
            int row = current.row;
            int col = current.col;
            switch(direction){
                case UP: row--; break;
                case DOWN: row++; break;
                case LEFT: col--; break;
                case RIGHT: col++; break;
            }
            if(board.validCell(row, col)) return new Cell(row, col);
            else return null;
        }

        public void turn(int direction){
            this.direction = direction;
        }
    }
}
