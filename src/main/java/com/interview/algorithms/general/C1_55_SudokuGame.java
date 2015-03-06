package com.interview.algorithms.general;

import com.interview.basics.search.ASearcher;
import com.interview.utils.ArrayUtil;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/12/14
 * Time: 2:41 PM
 *
 * Sudoku Game: Given a 3*3 matrix, and 1-8 numbers in random order, 1 place as space.
 * Write code to find the min exchange of numbers to make the matrix in order
 *      5 4 1           1 2 3
 *      3   2   --->    8   4
 *      7 8 6           7 6 5
 * Refer: http://blueve.me/archives/690
 */
class GameState implements ASearcher.State<String> {
    static int size = 3;
    String s;

    GameState(String s) {
        this.s = s;
    }

    @Override
    public String key() {
        return s;
    }

    public GameState move(int direction){
        String next = "";
        int index = this.s.indexOf('0');
        switch (direction){
            case C1_55_SudokuGame.UP:
                if(index < 3) return null;
                next = ArrayUtil.swap(this.s, index, index - 3);
                break;
            case C1_55_SudokuGame.DOWN:
                if(index > 5) return null;
                next = ArrayUtil.swap(this.s, index, index + 3);
                break;
            case C1_55_SudokuGame.LEFT:
                if(index % 3 == 0) return null;
                next = ArrayUtil.swap(this.s, index, index - 1);
                break;
            case C1_55_SudokuGame.RIGHT:
                if((index + 1) % 3 == 0) return null;
                next = ArrayUtil.swap(this.s, index, index + 1);
                break;
        }
        return new GameState(next);
    }

    public static int getOp(String current, String next){
        int i = current.indexOf('0');
        int j = next.indexOf('0');
        if(j == i - 3) return C1_55_SudokuGame.UP;
        else if(j == i + 3) return C1_55_SudokuGame.DOWN;
        else if(j == i + 1) return C1_55_SudokuGame.RIGHT;
        else if(j == i - 1) return C1_55_SudokuGame.LEFT;
        return -1;
    }

    public static String getString(Integer[][] matrix){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                builder.append(matrix[i][j]);
            }
        }
        return builder.toString();
    }

    public static void print(String s){
        for(int i = 0; i < size; ++i){
            for(int j = 0; j < size; ++j){
                System.out.print(s.charAt(i * 3 + j) + " ");
            }
            System.out.println();
        }
    }
}
public class C1_55_SudokuGame extends ASearcher<String, GameState, Integer> {
    public final static int UP = 0;
    public final static int DOWN = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;
    public final static int[] DIRECTIONS = new int[] {UP, DOWN, LEFT, RIGHT};

    public C1_55_SudokuGame(Integer size) {
        super(size);
    }

    public void debug(boolean flag){
        this.isDebug = flag;
    }

    @Override
    protected double heuristicEstimateDistance(GameState c, GameState t) {
        double diff = 0.0;
        for(int i = 0; i < 9; i++) {
            if(c.s.charAt(i) != t.s.charAt(i)) diff++;
        }
        return diff;
    }

    @Override
    protected boolean isSame(GameState c, GameState t) {
        return c.s.equals(t.s);
    }

    @Override
    protected GameState[] nextState(GameState gameState) {
        //System.out.println(gameState.key());
        GameState[] next = new GameState[4];
        int i = 0;
        for(int direction : DIRECTIONS){
            next[i++] = gameState.move(direction);
        }
        return next;
    }

    @Override
    protected double gScore(Candidate c, GameState t) {
        return gScore.get(c.state.key()) + 1;
    }

    public int[] solve(Integer[][] startMatrix, Integer[][] endMatrix){
        GameState start = new GameState(GameState.getString(startMatrix));
        GameState end = new GameState(GameState.getString(endMatrix));

        Path<String> path = this.pathTo(start, end);

        int[] ops = new int[path.path.size()];
        if(path.path.size() > 0){
            int i = 0;
            String pre = start.key();
            while(!path.path.isEmpty()){
                String current = path.path.pop();
                int op = GameState.getOp(pre, current);
                pre = current;
                ops[i++] = op;
            }
        }
        return ops;
    }
}
