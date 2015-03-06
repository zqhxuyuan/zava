package com.interview.books.question300;

import com.interview.algorithms.string.C11_22_FindCombinationWithRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-8-26
 * Time: 下午3:54
 */
public class TQ39_FindCombinationWithRules {

    public int N;
    public Map<Integer, Integer[]> placeMap;
    public Map<Integer, Integer[]> neighborMap;

    public TQ39_FindCombinationWithRules(int n, Map<Integer, Integer[]> placeMap, Map<Integer, Integer[]> neighborMap) {
        this.N = n;
        this.placeMap = placeMap;
        this.neighborMap = neighborMap;
    }

    public List<String> find(){
        List<String> combinations = new ArrayList<String>();
        find(combinations, null, "", 1);
        return combinations;
    }

    private void find(List<String> combinations, Integer last, String prefix, int idx){
        if(prefix.length() == N) {
            combinations.add(prefix);
            return;
        }
        Integer[] blocks = placeMap.get(idx);
        Integer[] neighbor = last == null? null : neighborMap.get(last);
        for(int i = 1; i <= N; i++){
            if(blocks != null && blocks.length > 0 && !canPlace(i, blocks))  continue;
            if(neighbor != null && neighbor.length > 0 && !canPlace(i, neighbor)) continue;
            if(prefix.contains(i+"")) continue;
            find(combinations, i, prefix + i, idx + 1);
        }
    }

    private boolean canPlace(Integer i, Integer[] blocks){
        for(Integer block : blocks)
            if(i == block) return false;
        return true;
    }

    public static void main(String[] args){
        Map<Integer, Integer[]> placeMap = new HashMap<Integer, Integer[]>();
        placeMap.put(3, new Integer[]{4});
        Map<Integer, Integer[]> neighborMap = new HashMap<Integer, Integer[]>();
        neighborMap.put(3, new Integer[]{5});
        neighborMap.put(5, new Integer[]{3});
        TQ39_FindCombinationWithRules finder = new TQ39_FindCombinationWithRules(5, placeMap, neighborMap);

        List<String> combinations = finder.find();
        System.out.println(combinations.size());  //56
        for(String str : combinations) {
            System.out.println(str);
        }
    }
}
