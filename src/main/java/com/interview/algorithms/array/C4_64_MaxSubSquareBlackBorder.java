package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-10-9
 * Time: 上午10:21
 */
public class C4_64_MaxSubSquareBlackBorder {

    class Result {
        int x1, y1; // left up corner point
        int x2, y2; // bottom right corner point
        int size() { return (x2 - x1) * (y2 - y1); }
    }

    public Result find(boolean[][] array) {
        Result result = new Result();
        for(int i = 0; i < array.length; i ++)
            for(int j = 0; j < array[0].length; j ++)
                if(array[i][j])
                    this.find(array, i, j, result);
        return result;
    }

    public void find(boolean[][] array, int x1, int y1, Result result) {
        int y2 = y1;
        while(y2 < array[0].length && array[x1][y2]) y2++;
        if(y2 == array[0].length || !array[x1][y2]) y2 --;

        int x2 = x1;
        while(x2 < array.length && array[x2][y1]) x2++;
        if(x2 == array.length || !array[x2][y1]) x2 --;

        for(int i = x1; i <= x2; i ++) {
            if(! array[i][y2]) {
                y2 -= 1;
                i = x1;
            }
        }

        for(int j = y1; j <= y2; j ++) {
            if(! array[x2][j]) {
                x2 -= 1;
                j = y1;
            }
        }

        if((y2 - y1) * (x2 - x1) > result.size()) {
            result.x1 = x1;
            result.y1 = y1;
            result.x2 = x2;
            result.y2 = y2;
        }
    }

    public static void main(String[] args) {
        // int[][] array = new int[][] {{0, 1, 1, 1, 0}, {0, 1, 0, 1, 0}, {0, 1, 1, 1, 0}};
        boolean[][] array = new boolean[][] {
                {false, true, true,  true, true},
                {false, true, false, true, true},
                {false, true, true,  true, true}};
        C4_64_MaxSubSquareBlackBorder finder = new C4_64_MaxSubSquareBlackBorder();
        Result border = finder.find(array);
        System.out.println(String.format("x1=%s, y1=%s, x2=%s,y2=%s", border.x1, border.y1, border.x2, border.y2));
    }

}
