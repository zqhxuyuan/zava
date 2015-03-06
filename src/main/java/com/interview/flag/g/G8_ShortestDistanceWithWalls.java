package com.interview.flag.g;

import com.interview.basics.model.geometry.Line;
import com.interview.basics.model.graph.searcher.IndexedPriorityQueue;
import com.interview.utils.GeoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-6
 * Time: 下午3:20
 */
public class G8_ShortestDistanceWithWalls {

    //Dijsktra
    public float distance(float[] S, float[] T, List<Line> walls){

        IndexedPriorityQueue<String, Float> heap = new IndexedPriorityQueue();
        HashMap<String, Float> paths = new HashMap();
        heap.add(getKey(S), 0.0f);
        paths.put(getKey(S), 0.0f);

        while(!heap.isEmpty()){
            String point = heap.poll();
            float[] coordinates = getCoordinates(point);
            if(coordinates[0] == T[0] && coordinates[1] == T[1]) return paths.get(point);

            Line intersectedWall = nearestIntersectedWalls(coordinates, T, walls);
            float[][] next = null;
            if(intersectedWall != null) next = intersectedWall.endpoints();
            else next = new float[][]{T};

            for(int i = 0; i < next.length; i++){
                String candidate = getKey(next[i]);
                float distance = paths.get(point) + GeoUtil.distance(coordinates, next[i]); //this distance should also consider convex hull to relax.
                if(heap.contains(candidate)){
                    if(paths.get(candidate) > distance) {
                        heap.update(candidate, distance);
                        paths.put(candidate, distance);
                    }
                } else {
                    heap.add(candidate, distance);
                    paths.put(candidate, distance);
                }
            }
        }
        return Float.MAX_VALUE;
    }

    public Line nearestIntersectedWalls(float[] S, float[] T, List<Line> walls){
        Line st = new Line(S, T);
        Line nearestWall = null;
        float[] intersection = null;
        for(int i = 0; i < walls.size(); i++){
            float[] point = st.intersection(walls.get(i));
            if(point != null && (point[0] != S[0] && point[1] != S[1])){
                if(intersection == null || GeoUtil.distance(S, point) < GeoUtil.distance(S, intersection)){
                    intersection = point;
                    nearestWall = walls.get(i);
                }
            }
        }
        return nearestWall;
    }


    private float[] getCoordinates(String key){
        float[] points = new float[2];
        String[] splits = key.split("-");
        for(int i = 0; i < splits.length; i++) points[i] = Float.parseFloat(splits[i]);
        return points;
    }

    private String getKey(float[] points){
        return points[0] + "-" + points[1];
    }

    public static void main(String[] args){
        G8_ShortestDistanceWithWalls finder = new G8_ShortestDistanceWithWalls();
        float[] S = new float[]{0,0};
        float[] T = new float[]{10,10};
        List<Line> walls = new ArrayList();
        walls.add(new Line(new float[]{3,1}, new float[]{3,4}));
        System.out.println(finder.distance(S, T, walls)); //14.219544
        walls.add(new Line(new float[]{2,8}, new float[]{6,5}));
        System.out.println(finder.distance(S, T, walls));  //14.564032
    }
}
