package com.interview.books.topcoder.geometry;

import com.interview.basics.model.geometry.Polygon;

/**
 * Created_By: stefanie
 * Date: 15-1-5
 * Time: 下午6:31
 */
public class TC_G11_Surveyor {

    public float area(String direction, int[] steps){
        float[][] points = new float[steps.length + 1][2];
        points[0] = new float[]{0,0};
        for(int i = 0; i < steps.length; i++){
            char ch = direction.charAt(i);
            switch (ch){
                case 'N': points[i+1] = new float[]{points[1][0], points[i][1] + steps[i]}; break;
                case 'S': points[i+1] = new float[]{points[i][0], points[i][1] - steps[i]}; break;
                case 'E': points[i+1] = new float[]{points[i][0] + steps[i], points[i][1]}; break;
                case 'W': points[i+1] = new float[]{points[i][0] - steps[i], points[i][1]}; break;
            }
        }
        Polygon polygon = new Polygon(points);
        return polygon.area();
    }

    public static void main(String[] args){
        TC_G11_Surveyor surveyor = new TC_G11_Surveyor();
        String direction = "NESWNWSW";
        int[] steps = new int[]{20,200,30,100,20,30,10,70};
        System.out.println(surveyor.area(direction, steps));//4700

    }
}
