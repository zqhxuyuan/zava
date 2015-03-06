package com.interview.algorithms.design;

/**
 * Created_By: stefanie
 * Date: 14-11-3
 * Time: 下午8:44
 */
public class C10_10_TicTacToeGame {
    int size;
    int user;
    short[][] chess;
    short[] markers;

    public C10_10_TicTacToeGame(int size, int user){
        this.size = size;
        this.user = user;
        chess = new short[size][size];
        markers = new short[user];
        for(short i = 1; i < user + 1; i++) markers[i-1] = i;
    }

    public boolean put(int user, int x, int y){
        if(!inChess(x, y)) return false;
        if(chess[x][y] != 0) return false;
        chess[x][y] = markers[user];
        return true;
    }

    public boolean isSuccess(int user, int x, int y){
        return false;
    }

    private boolean inChess(int x, int y){
        return x >= 0 && x < size && y >= 0 && y < size;
    }
}
