package com.interview.books.ninechapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 上午11:03
 */
public class NC4_WordPairMinDistance {
    String[] article;
    HashMap<String, List<Integer>> indexes;

    public NC4_WordPairMinDistance(String[] article){
        this.article = article;
        indexes = new HashMap<>();
        buildIndex();
    }

    private void buildIndex(){
        for(int i = 0; i < article.length; i++){
            List<Integer> offsets = indexes.get(article[i]);
            if(offsets == null){
                offsets = new ArrayList<>();
                indexes.put(article[i], offsets);
            }
            offsets.add(i);
        }
    }

    public int minDistance(String w1, String w2){
        List<Integer> w1Idxs = indexes.get(w1);
        List<Integer> w2Idxs = indexes.get(w2);
        if(w1Idxs == null || w2Idxs == null) return Integer.MAX_VALUE;
        int w1Idx = 0;
        int w2Idx = 0;
        int min = Integer.MAX_VALUE;
        //Time: O(N)
        while(w1Idx < w1Idxs.size() && w2Idx < w2Idxs.size()){
            //move the index of W1 to the largest index which smaller than the index of W2.
            while(w1Idx < w1Idxs.size() - 1 && w1Idxs.get(w1Idx + 1) < w1Idxs.get(w1Idx))  w1Idx++;
            int distance = w2Idxs.get(w2Idx) - w1Idxs.get(w1Idx) - 1;
            if(distance >= 0) {
                min = Math.min(min, distance);
                w1Idx++;
            }
            //move the index of W2 to the smallest index which larger than index of W1.
            while(w2Idx < w2Idxs.size() && w1Idx < w1Idxs.size() && w2Idxs.get(w2Idx) < w1Idxs.get(w1Idx))  w2Idx++;
        }
        return min;
    }

    public static void main(String[] args){
        String[] article = "ABBCCAABC".split("|");
        NC4_WordPairMinDistance finder = new NC4_WordPairMinDistance(article);

        System.out.println(finder.minDistance("A", "C"));
        System.out.println(finder.minDistance("C", "A"));
        System.out.println(finder.minDistance("B", "A"));
        System.out.println(finder.minDistance("C", "B"));

    }
}
