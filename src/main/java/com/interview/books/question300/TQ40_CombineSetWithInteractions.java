package com.interview.books.question300;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-17
 * Time: 下午10:03
 */
public class TQ40_CombineSetWithInteractions {


    public List<Set<String>> combine(List<Set<String>> input) {
        TQ2_UnionFind uf = new TQ2_UnionFind(input.size());
        for(int i = 0; i < input.size() - 1; i++){
            for(int j = i + 1; j < input.size(); j++){
                if(haveIntersection(input.get(i), input.get(j))){
                    uf.union(i, j);
                }
            }
        }
        HashMap<Integer, Set<String>> map = new HashMap<>();
        for(int i = 0; i < input.size(); i++){
            int root = uf.find(i);
            if(map.containsKey(root)){
                map.get(root).addAll(input.get(i));
            } else {
                map.put(root, input.get(i));
            }
        }
        List<Set<String>> result = new ArrayList<>();
        for(Set<String> set : map.values()){
            result.add(set);
        }
        return result;
    }

    private boolean haveIntersection(Set<String> s1, Set<String> s2){
        if(s1.size() < s2.size()) return haveIntersection(s2, s1);
        for(String str : s2){
            if(s1.contains(str)) return true;
        }
        return false;
    }


    public static void main(String[] args){
        List<Set<String>> input = new ArrayList<Set<String>>();
        Set<String> set = new HashSet<String>();
        set.add("aa");
        set.add("bb");
        set.add("cc");
        input.add(set);

        set = new HashSet<String>();
        set.add("dd");
        set.add("bb");
        input.add(set);

        set = new HashSet<String>();
        set.add("hh");
        input.add(set);

        set = new HashSet<String>();
        set.add("uu");
        set.add("jj");
        input.add(set);

        set = new HashSet<String>();
        set.add("dd");
        set.add("kk");
        input.add(set);

        TQ40_CombineSetWithInteractions combiner = new TQ40_CombineSetWithInteractions();
        List<Set<String>> result = combiner.combine(input);
        for(Set<String> item : result){
            System.out.println(item);
            boolean flag = false;
            if(item.size() == 1 && item.contains("hh")) flag = true;
            else if(item.size() == 2 && item.contains("uu") && item.contains("jj")) flag = true;
            else if(item.size() == 5 && item.contains("aa") && item.contains("bb")
                    && item.contains("cc") && item.contains("dd") && item.contains("kk")) flag = true;
            System.out.println(flag);
        }
    }

}
