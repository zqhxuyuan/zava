package com.interview.flag.g;

import com.interview.basics.model.geometry.Line;
import com.interview.books.topcoder.geometry.TC_G9_PointPolygonRelation;
import com.interview.utils.GeoUtil;

/**
 * Created_By: stefanie
 * Date: 14-12-31
 * Time: 下午7:41
 */
class Cycle{
    float[] center;
    int radius;

    Cycle(float[] center, int radius) {
        this.center = center;
        this.radius = radius;
    }
}
class Square{
    float[][] points;

    Square(float[][] points) {
        this.points = points;
    }

    public float[] center(){
        Line l1 = new Line(points[0], points[2]);
        Line l2 = new Line(points[1], points[3]);
        return l1.intersection(l2);
    }
}
public class G5_CycleSquareOverlapChecker {
    public boolean hasOverlap(Cycle cycle, Square square){

        String relation = new TC_G9_PointPolygonRelation().relation(square.points, cycle.center);
        if(relation.equals(TC_G9_PointPolygonRelation.BOUNDARY) || relation.equals(TC_G9_PointPolygonRelation.INTERIOR)) return true;

        float[] squareCenter = square.center();
        Line ccLine = new Line(squareCenter, cycle.center);

        int len = square.points.length;
        for(int i = 0; i < square.points.length; i++){
            float[] intersection = ccLine.intersection(new Line(square.points[i%len], square.points[(i+1)%len]));
            if(intersection != null){
                float distance = GeoUtil.distance(intersection, cycle.center);
                if(distance < cycle.radius) return true;
                else return false;
            }
        }
        return false;
    }

    public static void main(String[] args){
        G5_CycleSquareOverlapChecker checker = new G5_CycleSquareOverlapChecker();
        Square square = new Square(new float[][]{
                {0,0},{0,10},{10,10},{10,0}
        });
        Cycle cycle = new Cycle(new float[]{6,6}, 2);
        System.out.println(checker.hasOverlap(cycle, square));//true

        cycle = new Cycle(new float[]{11,11}, 2);
        System.out.println(checker.hasOverlap(cycle, square));//true

        cycle = new Cycle(new float[]{15,15}, 2);
        System.out.println(checker.hasOverlap(cycle, square));//false
    }
}
