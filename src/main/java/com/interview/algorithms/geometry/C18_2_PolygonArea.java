package com.interview.algorithms.geometry;

/**
 *
 * See basic geo concepts: http://help.topcoder.com/data-science/competing-in-algorithm-challenges/algorithm-tutorials/geometry-concepts-basic-concepts/
 *
 * Created_By: zouzhile
 * Date: 1/4/15
 * Time: 7:14 PM
 */
public class C18_2_PolygonArea {

    public double getArea(int[][] points) {
        double area = 0.0;
        for(int i = 1; i + 1 < points.length; i ++) {
            area += GeoUtil.crossProduct(points[0], points[i], points[i+1]);
        }

        return Math.abs(area / 2);
    }
}
