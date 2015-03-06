package com.interview.algorithms.array;

import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-11-9
 * Time: 下午10:12
 */
public class C4_78_LongestConsecutiveSequence {
    public static int longestConsecutive(int[] num) {
        if(num.length == 0) return 0;
        Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        for(int i = 0; i < num.length; i++) map.put(num[i], false);
        int longest = 1;
        for(Map.Entry<Integer, Boolean> entry : map.entrySet()){
            if(entry.getValue()) continue;
            boolean left = true, right = true;
            int count = 1, offset = 1;
            int number = entry.getKey();
            while(true){
                if(right) {
                    if (map.containsKey(number + offset)) {
                        map.put(number + offset, true);
                        count++;
                    } else right = false;
                }
                if(left){
                    if(map.containsKey(number - offset)) {
                        map.put(number - offset, true);
                        count++;
                    } else left = false;
                }
                if(!(left || right)) break;
                offset++;
            }
            if(count > longest) longest = count;
        }
        return longest;
    }

    static class Union{
        Integer parent;
        int size = 1;

        Union(Integer parent) {
            this.parent = parent;
        }
    }
    public static int longestConsecutiveUF(int[] num){
        if(num.length == 0) return 0;
        int max = 1;
        Map<Integer, Union> map = new HashMap<Integer, Union>();
        for(int i = 0; i < num.length; i++) map.put(num[i], new Union(num[i]));
        for(int i = 0; i < num.length; i++){
            max = Math.max(max, union(map, num[i], num[i] + 1));
            max = Math.max(max, union(map, num[i], num[i] - 1));
        }
        return max;
    }

    private static int union(Map<Integer, Union> map, int n1, int n2){
        if(!map.containsKey(n2)) return 1;
        Union u1 = find(map, n1);
        Union u2 = find(map, n2);
        if(u1.parent == u2.parent) return u1.size;
        if(u1.size < u2.size) {
            u1.parent = u2.parent;
            u2.size += u1.size;
            return u2.size;
        } else {
            u2.parent = u1.parent;
            u1.size += u2.size;
            return u1.size;
        }
    }

    private static Union find(Map<Integer, Union> map, int id){
        Union u = map.get(id);
        while(u.parent != id) {
            u = map.get(u.parent);
            id = u.parent;
        }
        return u;
    }

}
