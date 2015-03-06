package com.interview.books.leetcodeoj;

import com.interview.utils.models.Point;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午2:29
 */
public class LOJ149_MaxPointsOnLine {
    //use HashMap to calculate how many point pairs with the same slope
    //be careful of slope is double, same point, p.x == q.x(slope is max positive).
    //call slopeMap.clear(); in the loop of select cur point
    public int maxPoints(Point[] points) {
        int max = 0;
        HashMap<Double, Integer> slopeMap = new HashMap();
        for(int i = 0; i < points.length; i++){
            slopeMap.clear();
            Point cur = points[i];
            int same = 0;
            for(int j = 0; j < points.length; j++){
                Point p = points[j];
                if(j == i || (p.x == cur.x && p.y == cur.y)) same++;
                else {
                    Double slope = 0.0;
                    if(p.x == cur.x) slope = Integer.MAX_VALUE + 0.0;
                    else slope = (p.y - cur.y)/(p.x - cur.x + 0.0);
                    if(slopeMap.containsKey(slope)) slopeMap.put(slope, slopeMap.get(slope) + 1);
                    else slopeMap.put(slope, 1);
                }
            }
            max = Math.max(max, same);
            for(Integer count : slopeMap.values()){
                max = Math.max(max, count + same);
            }
        }
        return max;
    }
}
