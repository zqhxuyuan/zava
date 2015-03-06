package com.interview.basics.model.geometry;

import com.interview.utils.FloatAssertion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-5
 * Time: 上午9:25
 */
public class ConvexHull {
    /**
     * If onEdge is true, use as many points as possible for
     * the convex hull, otherwise as few as possible.
     *
     * when crossProduct == 0, both N and X are in the same direction.
     * If onEdge is true, pick the closest one, otherwise pick the farthest one.
     */
    public List<float[]> find(float[][] points, boolean onEdge){
        List<float[]> convexHull = new ArrayList();
        boolean[] used = new boolean[points.length];
        int leftMost = leftMost(points);
        convexHull.add(points[leftMost]);
        used[leftMost] = true;
        int start = leftMost;
        float dist = onEdge? Integer.MAX_VALUE : 0;
        do{
            int next = -1;
            for(int i = 0; i < points.length; i++){
                if(used[i]) continue;
                float d = new Vector(points[start], points[i]).length();
                if(next == -1) {
                    next = i;
                    dist = d;
                }
                else {
                    float cross = new Vector(points[start], points[i]).cross(new Vector(points[start], points[next]));
                    int cmp = FloatAssertion.compareTo(cross, 0.0);
                    if(cmp < 0) {
                        next = i;
                        dist = d;
                    } else if(cmp == 0){
                        if(onEdge && d < dist){
                            next = i;
                            dist = d;
                        }else if(!onEdge && d > dist){
                            next = i;
                            dist = d;
                        }
                    }
                }
                convexHull.add(points[next]);
                start = next;
            }
        } while(start != leftMost);
        return convexHull;
    }

    public int leftMost(float[][] points){
        int leftMost = 0;
        for(int i = 1; i < points.length; i++){
            if(points[i][0] < points[leftMost][0]
                    || (points[i][0] == points[leftMost][0] && points[i][1] > points[leftMost][1]))
                leftMost = i;
        }
        return leftMost;
    }
}
