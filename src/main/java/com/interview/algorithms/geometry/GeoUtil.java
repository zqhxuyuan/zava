package com.interview.algorithms.geometry;

/**
 * Created_By: zouzhile
 * Date: 1/4/15
 * Time: 6:32 PM
 */
public class GeoUtil {

    /**
     * the dotProduct product of vector AB and AC
     * @param A
     * @param B
     * @param C
     * @return
     */
    public static int dotProduct(int[] A, int[] B, int[] C) {
        int[] AB = new int[2];
        int[] AC = new int[2];
        AB[0] = B[0] - A[0];
        AB[1] = B[1] - A[1];
        AC[0] = C[0] - A[0];
        AC[1] = C[1] - A[1];
        return AB[0] * AC[0] + AB[1] * AC[1];
    }

    /**
     * the crossProduct product of vector AB and AC
     * crossProduct(AB, AC) is AB x AC = |AB||AC|Sin(θ)
     *
     * @param A
     * @param B
     * @param C
     * @return
     */
    public static int crossProduct(int[] A, int[] B, int[] C) {
        int[] AB = new int[2];
        int[] AC = new int[2];
        AB[0] = B[0] - A[0];
        AB[1] = B[1] - A[1];
        AC[0] = C[0] - A[0];
        AC[1] = C[1] - A[1];
        return AB[0] * AC[1] - AB[1] * AC[0];
    }

    /**
     * the size of vector AB
     * @param A
     * @param B
     * @return
     */
    public static double size(int[] A, int[] B) {
        int[] AB = new int[2];
        AB[0] = B[0] - A[0];
        AB[1] = B[1] - A[1];
        return Math.sqrt(AB[0] * AB[0] + AB[1] * AB[1]);
    }

    // a line is defined as Ax + By = C
    // A = y2-y1
    // B = x1-x2
    // C = A*x1+B*y1
    // the return value is an array with A, B and C correspondingly.
    public static int[] getLine(int[] P1, int[] P2) {
        int[] line = new int[3];
        line[0] = P2[1] - P1[1];
        line[1] = P1[0] - P2[0];
        line[2] = line[0] * P1[0] + line[1] * P1[1];
        return line;
    }

    // Given a line Ax + By = C, the perpendicular line is -Bx + Ay = D
    public static int[] getPerpendicularLine(int[] P1, int[] P2) {
        int[] midPoint = new int[] {(P1[0] + P2[0])/2, (P1[1] + P2[1])/2};
        int[] line = getLine(P1, P2);
        int A = line[0], B = line[1], C = line[2];
        int D = -B * midPoint[0] + A * midPoint[1];
        return new int[] {-B, A, D};
    }

    public static double[] getSegmentIntersection(Segment s1, Segment s2) {
        C18_3_LineIntersection intersection = new C18_3_LineIntersection();
        double[] point = intersection.getIntersection(s1.p1, s1.p2, s2.p1, s2.p2);
        if((point[0] >= s1.p1[0] && point[0] <= s1.p2[0] || point[0] >= s1.p2[0] && point[0] <= s1.p1[0]) &&
           (point[0] >= s2.p1[0] && point[0] <= s2.p2[0]) || point[0] >= s2.p2[0] && point[0] <= s2.p1[0])
            return point;

        return null;
    }

    public static boolean isPointOnLine(int[] line, double x, double y) {
        double expectedY = (line[2] - line[0] * x) / line[1];
        return String.format("%.5f", expectedY).equals(String.format("%.5f", y));
    }

    public static boolean isPointOnSegment(Segment segment, double x, double y) {
        int[] line = getLine(segment.p1, segment.p2);
        return ((x >= segment.p1[0] && x <= segment.p2[0]) || (x >= segment.p2[0] && x <= segment.p1[0])) && isPointOnLine(line, x, y);
    }

}
