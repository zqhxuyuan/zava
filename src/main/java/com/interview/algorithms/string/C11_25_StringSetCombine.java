package com.interview.algorithms.string;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/29/14
 * Time: 4:25 PM
 *
 * Given several sets of strings, write code to combine the sets which interactions.
 * Such as {aa,bb,cc},{bb,dd},{hh},{uu,jj},{dd,kk}, the result should be {aa,bb,cc,dd,kk},{hh},{uu,jj}
 *
 * 1. using reverseIndex to build a reference graph using mark[]
 * 2. run UnionFind to combine mark[]
 * 3. combine the set and delete duplicate ones
 */
public class C11_25_StringSetCombine {
    public static List<Set<String>> combine(List<Set<String>> input) {
        int[] mark = new int[input.size()];
        for(int i = 0; i < mark.length; i++) mark[i] = i;
        //build index
        Map<String, List<Integer>> reverseIndex = new HashMap<String, List<Integer>>();
        for(int i = 0; i < input.size(); i++){
            for (String str : input.get(i)) {
                List<Integer> index = reverseIndex.get(str);
                if (index == null) {
                    index = new ArrayList<>();
                    reverseIndex.put(str, index);
                }
                if(!index.contains(mark[i]))
                    index.add(mark[i]);
            }
        }
        boolean hasChange = true;
        //change flag
        for(List<Integer> indexs : reverseIndex.values()){
            if(indexs.size() > 1){
                hasChange = true;
                for(int i = 1; i < indexs.size(); i++){
                    mark[indexs.get(i)] = indexs.get(0);
                }
            }
        }

        while(hasChange){
            hasChange = false;
            for(int i = 0; i < mark.length; i++){
                if(mark[mark[i]] != mark[i]) {
                    mark[i] = mark[mark[i]];
                    hasChange = true;
                }
            }
        }

        //combine the set
        boolean[] save = new boolean[mark.length];
        for(int i = 0; i < mark.length; i++)    save[mark[i]] = true;
        for(int i = 0; i < mark.length; i++)    input.get(mark[i]).addAll(input.get(i));
        for(int i = save.length - 1; i >= 0; i--) {
            if(!save[i])   input.remove(i);
        }
        return input;
    }
}
