package com.interview.books.topcoder.geometry;

import com.interview.basics.model.geometry.Circle;
import com.interview.utils.GeoUtil;

/**
 * Created_By: stefanie
 * Date: 15-1-5
 * Time: 下午5:48
 */
public class TC_G10_TVTowner {

    public Circle locateTower(float[][] towns){
        Circle tower = new Circle();
        if(towns.length == 1){
            tower.center = towns[0];
            return tower;
        }
        tower.radius = Float.MAX_VALUE;
        for(int i = 0; i < towns.length; i++){
            for(int j = i + 1; j < towns.length; j++){
                //X, Y as the diameter of a circle
                float[] midpoint = GeoUtil.midpoint(towns[i], towns[j]);
                updateCenter(midpoint, towns, tower);

                for(int k = j + 1; k < towns.length; k++){
                    //X, Y, Z to identify a circle
                    float[] center = new Circle(towns[i], towns[j], towns[k]).center;
                    updateCenter(center, towns, tower);
                }
            }
        }
        return tower;
    }

    private void updateCenter(float[] center, float[][] towns, Circle tower){
        float max = 0;
        for(int i = 0; i < towns.length; i++){
            max = Math.max(max, GeoUtil.distance(center, towns[i]));
        }
        if(max < tower.radius){
            tower.center = center;
            tower.radius = max;
        }
    }

    public static void main(String[] args){
        float[][] towns = new float[][]{
                {5,0},
                {3,4},
                {-4,3},
                {2,2}
        };
        TC_G10_TVTowner finder = new TC_G10_TVTowner();
        Circle tower = finder.locateTower(towns);
        System.out.println(tower.center[0] + ", " + tower.center[1]);    //0.5, 1.5
        System.out.println(tower.radius);  //4.7434163
    }
}
