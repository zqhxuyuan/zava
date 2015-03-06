package com.interview.leetcode.matrix;

import com.interview.leetcode.utils.Point;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午4:47
 */
public class MaxPointsOnLine {

    public int maxPoints(Point[] points) {
        if (points == null || points.length == 0) return 0;
        HashMap<Double, Integer> map = new HashMap<Double, Integer>();
        int max = 0;
        for(Point cur : points){
            map.clear();
            int same = 0;
            for(Point p : points){
                if(p.x == cur.x && p.y == cur.y) {
                    same++;
                    continue;
                }
                double slop = 0;
                if(p.x == cur.x) slop = Integer.MAX_VALUE;
                else if(p.y == cur.y) slop = 0;
                else slop = (p.y - cur.y)/(p.x - cur.x + 0.0);

                if(map.containsKey(slop)){
                    map.put(slop, map.get(slop) + 1);
                } else {
                    map.put(slop, 1);
                }
            }
            if(same> max) max = same; //points in the same place
            for(Integer num : map.values()){
                if(num + same > max) max = num + same;
            }
        }
        return max;
    }
}
