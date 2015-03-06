package com.interview.basics.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/28/14
 * Time: 11:18 AM
 */
public class RadixSorter<T extends Comparable<T>> extends Sorter<T>{
    private Map<Character, List<Integer>> bucket = new HashMap<Character, List<Integer>>();
    private Map<T, String> map = new HashMap<T, String>();
    @Override
    public T[] sort(T[] input) {
        int loop = 0;
        for(int i = 0; i < input.length; i++){
            String str = input[i].toString();
            map.put(input[i], str);
            if(str.length() > loop) loop = str.length();
        }

        T[] temp = input;
        int index = 0;
        while(index < loop){
            bucket.clear();
            for(int i = 0; i < temp.length; i++){
                String str = map.get(temp[i]);
                Character ch = index < str.length()? str.charAt(str.length() - index - 1):'0';
                List<Integer> list = bucket.get(ch);
                if(list == null){
                    list = new ArrayList<>();
                    bucket.put(ch, list);
                }
                list.add(i);
            }
            int k = 0;
            T[] aux = (T[]) new Comparable[input.length];
            for(Character ch = '0'; ch <= '9'; ch++){
                List<Integer> list = bucket.get(ch);
                if(list != null){
                    for(Integer j : list) aux[k++] = temp[j];
                }
            }
            temp = aux;
            index++;
        }
        for(int i = 0; i < input.length; i++) input[i] = temp[i];
        return input;
    }
}
