package com.interview.basics.model.geometry;

import com.interview.utils.FloatAssertion;
import com.interview.utils.GeoUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-12
 * Time: 下午3:48
 */
public class ConvexHull_SweepLine {
    static Comparator<float[]> COMPARATOR = new Comparator<float[]>() {
        @Override
        public int compare(float[] o1, float[] o2) {
            if(FloatAssertion.equals(o1[0], o2[0])){
                return FloatAssertion.compareTo(o1[1], o2[1]);
            } else return FloatAssertion.compareTo(o1[0], o2[0]);
        }
    };
    public List<float[]> find(float[][] points){
        Arrays.sort(points, COMPARATOR);
        List<float[]> upper = new ArrayList();
        List<float[]> lower = new ArrayList();
        float[] leftMost = points[0];
        for(int i = 1; i < points.length; i++){
            if(points[i][1] >= leftMost[1]) upper.add(points[i]);
            else lower.add(points[i]);
        }

        List<float[]> upperConvexHull = buildConvexHull(leftMost, upper, true);
        List<float[]> lowerConvexHull = buildConvexHull(leftMost, lower, false);

        for(int i = lower.size() - 1; i >= 0; i--){
            upperConvexHull.add(lowerConvexHull.get(i));
        }
        return upperConvexHull;
    }

    private List<float[]> buildConvexHull(float[] leftMost, List<float[]> points, boolean isUpper) {
        List<float[]> convexHull = new ArrayList();
        convexHull.add(leftMost);
        for(int i = 0; i < points.size(); i++){
            if(i == 0) convexHull.add(points.get(i));
            else {
                while(convexHull.size() >= 2){
                    float cross = GeoUtil.crossProduct(convexHull.get(convexHull.size() - 2),
                            convexHull.get(convexHull.size() - 1), points.get(i));
                    if((isUpper && FloatAssertion.compareTo(cross, 0.0) >= 0)
                            || (!isUpper && FloatAssertion.compareTo(cross, 0.0) <= 0)){
                        convexHull.remove(convexHull.size() - 1);
                    } else {
                        break;
                    }
                }
                convexHull.add(points.get(i));
            }
        }
        return convexHull;
    }
}
