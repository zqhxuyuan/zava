package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 下午3:58
 */
public class CC37_MaxBlackBorderSubsquare {

    class Subsquare {
        int row;
        int col;
        int size;

        Subsquare(int row, int col, int size) {
            this.row = row;
            this.col = col;
            this.size = size;
        }
    }
    class CountCell {
        int rightOnes = 0;
        int belowOnes = 0;

        CountCell(int rightOnes, int belowOnes) {
            this.rightOnes = rightOnes;
            this.belowOnes = belowOnes;
        }
    }

    public Subsquare findMax(int[][] matrix){
        CountCell[][] preprocessed = processSquare(matrix);   //Optimize
        for(int len = matrix.length; len >= 1; len--){
            int end = matrix.length - len + 1;
            for(int row = 0; row < end; row++){
                for(int col = 0; col < end; col++){
                    if(checkBorder(preprocessed, row, col, len)) return new Subsquare(row, col, len);
                }
            }
        }
        return null;
    }
    // preprocess by find how many continious 1 in right or below,
    // when check bolder, just need check diff if 1's count in each edge == size.
    private CountCell[][] processSquare(int[][] matrix) {
        CountCell[][] preprocessed = new CountCell[matrix.length][matrix.length];

        for(int r = matrix.length - 1; r >= 0; r--){
            for(int c = matrix.length - 1; c >= 0; c--){
                int right = 0;
                int below = 0;
                if(matrix[r][c] == 1){
                    right++;
                    below++;
                    if(c + 1 < matrix.length)   right += preprocessed[r][c+1].rightOnes;
                    if(r + 1 < matrix.length)   below += preprocessed[r+1][c].belowOnes;
                }
                preprocessed[r][c] = new CountCell(right, below);
            }
        }
        return preprocessed;
    }

    private boolean checkBorder(CountCell[][] preprocessed, int row, int col, int len){
        if(preprocessed[row][col].rightOnes < len) return false;  //topLeft
        if(preprocessed[row][col].belowOnes < len) return false;
        if(preprocessed[row][col + len - 1].belowOnes < len) return false; //topRight
        if(preprocessed[row + len - 1][col].rightOnes < len) return false; //bottomLeft
        return true;
    }

    public static void main(String[] args){
        CC37_MaxBlackBorderSubsquare finder = new CC37_MaxBlackBorderSubsquare();

        int[][] matrix = new int[][]{
                {1,0,0,1,1,1},
                {1,0,1,1,0,1},
                {1,1,1,1,1,1},
                {0,1,0,0,1,1},
                {1,1,0,1,1,1},
                {0,1,1,1,1,1}
        };
        Subsquare square = finder.findMax(matrix);
        System.out.println(square.row + ", " + square.col + ", size: " + square.size);
        //2, 1, size: 4

        matrix = new int[][]{
                {1,0,0,1,1,1},
                {1,0,1,1,0,1},
                {1,1,1,1,1,1},
                {0,1,0,0,1,1},
                {1,1,0,1,0,1},
                {0,1,1,1,1,1}
        };

        square = finder.findMax(matrix);
        System.out.println(square.row + ", " + square.col + ", size: " + square.size);
        //0, 3, size: 3
    }
}
