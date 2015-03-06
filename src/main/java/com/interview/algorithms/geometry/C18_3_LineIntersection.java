package com.interview.algorithms.geometry;

/**
 * Created_By: zouzhile
 * Date: 1/4/15
 * Time: 9:25 PM
 */
public class C18_3_LineIntersection {

    /**
     * Get the intersection of two lines. Line 1 is determined by P1 and P2, Line 2 is determined by P3 and P4.
     *
     * @param P1
     * @param P2
     * @param P3
     * @param P4
     * @return  the intersection point P. If the two lines are parallel, return null.
     */
    public double[] getIntersection(int[] P1, int[] P2, int[] P3, int[] P4) {
        int[] L1 = GeoUtil.getLine(P1, P2);
        int[] L2 = GeoUtil.getLine(P3, P4);

        int A1 = L1[0], B1 = L1[1], C1 = L1[2];
        int A2 = L2[0], B2 = L2[1], C2 = L2[2];

        double slopDiff = A1 * B2 - A2 * B1;
        if(slopDiff == 0) return null;

        double[] P = new double[2];
        P[0] = (B2*C1 - B1*C2)/slopDiff;
        P[1] = (A1*C2 - A2*C1)/slopDiff;
        return P;
    }


}
