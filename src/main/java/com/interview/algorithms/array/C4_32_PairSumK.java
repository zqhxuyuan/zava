package com.interview.algorithms.array;

import com.interview.basics.sort.QuickSorter;
import com.interview.utils.models.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/22/14
 * Time: 4:42 PM
 */
public class C4_32_PairSumK {
    static QuickSorter<Integer> sorter = new QuickSorter<>();

    public static Pair<Integer> findPairBySort(Integer[] a, Integer K){

        sorter.sort(a);

        int i = 0;
        int j = a.length - 1;
        while(i < j){
            int sum = a[i] + a[j];
            if(sum == K) return new Pair<>(a[i], a[j]);
            else if(sum > K) j--;
            else i++;
        }
        return null;
    }

    public static List<Pair> findPairs(Integer[] a, Integer K){
        sorter.sort(a);
        List<Pair> pairs = new ArrayList<>();
        Integer[] b = new Integer[a.length];
        for(int i = 0; i < a.length; i++) b[i] = K - a[i];

        int i = 0;
        int j = a.length - 1;

        while(j >= 0 && i < a.length){
            if(a[i] == b[j])  pairs.add(new Pair(a[i++], K-b[j--]));
            else if(a[i] < b[i]) i++;
            else j--;
        }

        return pairs;
    }

    public static List<Pair> findPairsON(Integer[] a, Integer K){
        List<Pair> pairs = new ArrayList<>();
        Set<Integer> hash = new HashSet<Integer>();
        for(int i = 0; i < a.length; i++) hash.add(K - a[i]);

        for(int i = 0; i < a.length; i++){
            if(hash.contains(a[i])) pairs.add(new Pair(a[i], K - a[i]));
        }
        return pairs;
    }
}
