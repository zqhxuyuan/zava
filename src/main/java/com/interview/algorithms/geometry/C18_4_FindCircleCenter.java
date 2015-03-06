package com.interview.algorithms.geometry;

/**
 * Created_By: zouzhile
 * Date: 1/11/15
 * Time: 10:21 AM
 */
public class C18_4_FindCircleCenter {

    public double[] findCircleCenter(int[] p1, int[] p2, int[] p3) {
        int[] l1 = GeoUtil.getPerpendicularLine(p1, p2);
        int[] l2 = GeoUtil.getPerpendicularLine(p2, p3);

        // x = (B2C1 - B1C2) / (A1B2 - A2B1)
        // y = (C2 - A2 * x) / B2

        double x = (l2[1]*l1[2] - l1[1]*l2[2]) / (l1[0]*l2[1] - l2[0]*l1[1]);
        double y = (l2[2] - l2[0] * x) / l2[1];

        return new double[] {x, y};
    }
}
