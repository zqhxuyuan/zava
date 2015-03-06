package com.interview.flag.l;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 15-1-8
 * Time: 上午9:57
 */
public class L3_TriangleBuilder {

    public int[] triangle(int[] edges){
        Arrays.sort(edges);
        for(int i = 0; i+2 < edges.length; i++){
            if(edges[i] + edges[i+1] > edges[i+2]){
                return new int[]{i, i+1, i+2};
            }
        }
        return new int[]{-1,-1,-1};
    }
}
