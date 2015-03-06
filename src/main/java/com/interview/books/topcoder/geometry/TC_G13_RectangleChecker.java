package com.interview.books.topcoder.geometry;

import com.interview.utils.FloatAssertion;
import com.interview.utils.GeoUtil;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 15-1-6
 * Time: 上午11:22
 */
public class TC_G13_RectangleChecker {

    public boolean isRectangle(float[][] points){
        Comparator<float[]> comparator = new Comparator<float[]>() {
            @Override
            public int compare(float[] o1, float[] o2) {
                if(FloatAssertion.equals(o1[0], o2[0])){
                    return FloatAssertion.compareTo(o1[1], o2[1]);
                } else return FloatAssertion.compareTo(o2[0], o1[0]);
            }
        };
        Arrays.sort(points, comparator);
        if(GeoUtil.isPerpendicular(points[0], points[1], points[3])
                && GeoUtil.isPerpendicular(points[0], points[2], points[3])) return true;
        else return false;
    }

    public static void main(String[] args){

        TC_G13_RectangleChecker checker = new TC_G13_RectangleChecker();
        float[][] points = new float[][]{
                {0,0},{10,10},{0,10},{10,0}
        };
        System.out.println(checker.isRectangle(points)); //true
        points = new float[][]{
                {0,0},{2,1},{0,5},{-2,4}
        };
        System.out.println(checker.isRectangle(points)); //true
        points = new float[][]{
                {0,0},{3,1},{0,5},{-2,4}
        };
        System.out.println(checker.isRectangle(points)); //false
    }
}
