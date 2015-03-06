package com.interview.flag.l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-8
 * Time: 下午8:25
 */
public class L1_WordMinDistanceInSentence {
    HashMap<String, List<Integer>> index = new HashMap();
    public L1_WordMinDistanceInSentence(String[] sens){
        for(int i = 0; i < sens.length; i++){
            if(index.containsKey(sens[i])){
                index.get(sens[i]).add(i);
            } else {
                List<Integer> offset = new ArrayList();
                offset.add(i);
                index.put(sens[i], offset);
            }
        }
    }
    public int distance(String w1, String w2){
        List<Integer> offset1 = index.get(w1);
        List<Integer> offset2 = index.get(w2);
        if(offset1 == null || offset2 == null) return Integer.MAX_VALUE;
        int minDistance = Integer.MAX_VALUE;
        int idx1 = 0;
        int idx2 = 0;
        while(idx1 < offset1.size() && idx2 < offset2.size()){
            minDistance = Math.min(minDistance, Math.abs(offset1.get(idx1) - offset2.get(idx2)));
            if(offset1.get(idx1) < offset2.get(idx2)) idx1++;
            else idx2++;
        }
        return minDistance;
    }

    public static void main(String[] args){
        String[] sens = new String[]{"the", "quick", "brown", "fox", "quick"};
        L1_WordMinDistanceInSentence dict = new L1_WordMinDistanceInSentence(sens);
        System.out.println(dict.distance("fox","the")); //3
        System.out.println(dict.distance("quick","fox")); //3
    }
}
