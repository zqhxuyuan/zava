package com.interview.algorithms.geometry;

import java.util.HashSet;

/**
 * Created_By: zouzhile
 * Date: 1/16/15
 * Time: 3:26 PM
 */
public class C18_5_PointLocationDetection {

    public String detect(int[] x, int[] y, int testX, int testY) {
        int size = x.length;

        Segment testSegment = new Segment(new int[]{testX, testY + 1000}, new int[]{testX, testY - 1000});
        HashSet<String> cuts = new HashSet<>();
        for(int i = 1; i <= size; i ++) {
            int current = i;
            int previous = current - 1;
            if(current == size) {
                current = 0;
                previous = size - 1;
            }

            Segment border = new Segment(new int[] {x[current], y[current]}, new int[]{x[previous], y[previous]});
            if(GeoUtil.isPointOnSegment(border, testX, testY)) {
                return "boundary";
            } else {
                double[] point = GeoUtil.getSegmentIntersection(testSegment, border);
                if (point != null) {
                    String key = this.serialize(point);
                    if(! cuts.contains(key)) cuts.add(key);
                }
            }
        }

        int cutsCount = cuts.size();
        return cutsCount % 2 == 0 ? "exterior" : "interior";
    }

    private String serialize(double[] point) {
        return String.format("%.5f", point[0]) +"\t" + String.format("%.5f", point[1]);
    }
}
