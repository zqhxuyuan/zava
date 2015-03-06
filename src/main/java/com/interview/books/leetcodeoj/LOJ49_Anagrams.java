package com.interview.books.leetcodeoj;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午2:55
 */
public class LOJ49_Anagrams {
    public List<String> anagrams(String[] strs) {
        HashMap<String, List<String>> map = new HashMap();
        for(int i = 0; i < strs.length; i++){
            char[] chars = strs[i].toCharArray();
            Arrays.sort(chars);
            String sorted = String.valueOf(chars);
            if(map.containsKey(sorted)) map.get(sorted).add(strs[i]);
            else {
                List<String> list = new ArrayList();
                list.add(strs[i]);
                map.put(sorted, list);
            }
        }
        List<String> list = new ArrayList();
        for(List<String> item : map.values()){
            if(item.size() > 1) list.addAll(item);
        }
        return list;
    }
}
