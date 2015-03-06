package com.interview.books.topcoder.geometry;

import com.interview.basics.model.geometry.Line;
import com.interview.utils.FloatAssertion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 15-1-5
 * Time: 下午10:33
 */
public class TC_G12_SymmetryLine {

    public int count(float[][] points){
        List<Line> symmetry = new ArrayList();

        Set<String> pointSet = new HashSet();
        for(float[] point : points) pointSet.add(getKey(point));

        for(int i = 0; i < points.length; i++){
            for(int j = i + 1; j < points.length; j++){
                Line line = new Line(points[i], points[j]).perpendicular();
                boolean allHaveReflection = true;
                for(int k = 0; k < points.length; k++){
                    if(k == i || k == j) continue;
                    float[] reflection = line.reflection(points[k]);
                    if(!pointSet.contains(getKey(reflection))){
                        allHaveReflection = false;
                        break;
                    }
                }
                if(allHaveReflection) symmetry.add(line);
            }
        }
        return symmetry.size()/(points.length/2);
    }

    public String getKey(float[] point){
        return FloatAssertion.toString(point[0]) + "-" + FloatAssertion.toString(point[1]);
    }

    public static void main(String[] args){
        //0 0 2 1 0 5 -2 4
        TC_G12_SymmetryLine line = new TC_G12_SymmetryLine();
        float[][] points = new float[][]{
            {0,0},{2,1},{0,5},{-2,4}
        };
        System.out.println(line.count(points)); //2

        points = new float[][]{
            {0, 0},{100, 0},{50, 87}
        };
        System.out.println(line.count(points)); //1
    }
}
