package com.interview.algorithms.geometry;

/**
 * See basic geo concepts: http://help.topcoder.com/data-science/competing-in-algorithm-challenges/algorithm-tutorials/geometry-concepts-basic-concepts/
 *
 * Created_By: zouzhile
 * Date: 1/4/15
 * Time: 7:09 PM
 */

public class C18_1_DotToLineDistance {

    public double getDistance(int[] A, int[] B, int[] C) {
        int area = Math.abs(GeoUtil.crossProduct(A, B, C));
        double sizeAB = Math.abs(GeoUtil.size(A, B));
        return area / sizeAB;
    }
}
