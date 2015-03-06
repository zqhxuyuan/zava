package com.interview.algorithms.array;

import com.interview.utils.models.Point;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/15/14
 * Time: 4:42 PM
 * <p/>
 * http://blog.csdn.net/zhanglei8893/article/details/6234564
 */
public class C4_65_YangMatrixSearching {

    /**
     * scan the matrix from right-up corner,
     *   while(not out of the matrix){
     *      if the value = key, return true
     *      if the value < key, go down
     *      if the value > key, go left
     *   }
     * @param matrix
     * @param k
     * @return
     */
    public static boolean exist(int[][] matrix, int k){
        Point p = new Point(matrix[0].length - 1, 0);
        while(p.x >= 0 && p.y <= matrix.length - 1){
            int v = value(matrix, p);
            if( v == k ) return true;
            else if( v > k ) p.x--;
            else p.y++;
        }
        return false;
    }


    public static boolean exist2(int[][] matrix, int k) {
        int n = matrix[0].length - 1;
        int m = matrix.length - 1;
        if (k < matrix[0][0] || k > matrix[n][m]) return false;
        return exist(matrix, new Point(0, 0), new Point(n, m), k);
    }

    private static boolean exist(int[][] matrix, Point i, Point j, int k) {
        if (i.x == j.x && i.y == j.y) {
            if (k == value(matrix, i)) return true;
            else return false;
        } else {
            Point[] points = middle(i, j);
            if (k == value(matrix, points[0]) || k == value(matrix, points[1])) return true;
            else if (k > value(matrix, points[0]) && k < value(matrix, points[1])) {
                return exist(matrix, new Point(i.x, points[1].y), new Point(points[0].x, j.y), k)
                        | exist(matrix, new Point(points[1].x, i.y), new Point(j.x, points[0].y), k);
            } else {
                boolean exist = false;
                if (k > value(matrix, points[1])) exist = exist(matrix, points[1], j, k);
                else exist = exist(matrix, i, points[0], k);
                if (!exist) {
                    return exist(matrix, new Point(i.x, points[1].y), new Point(points[0].x, j.y), k)
                            | exist(matrix, new Point(points[1].x, i.y), new Point(j.x, points[0].y), k);
                } else return true;
            }
        }
    }

    private static int value(int[][] matrix, Point i) {
        return matrix[i.x][i.y];
    }

    private static Point[] middle(Point i, Point j) {
        int gapY = (j.y - i.y) >> 1;
        int gapX = (j.x - i.x) >> 1;
        Point[] points = new Point[2];
        points[0] = new Point(i.x + gapX, i.y + gapY);
        points[1] = new Point(i.x + gapX + 1, i.y + gapY + 1);
        return points;
    }
}
