package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-1
 * Time: 下午9:57
 */
public class G3_SortArrayByOtherArray {

    public void sort(Integer[] a, Integer[] b){
        final HashMap<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < b.length; i++) map.put(b[i], i);
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if(map.containsKey(o1) && map.containsKey(o2))  return map.get(o1).compareTo(map.get(o2));
                else return o1.compareTo(o2);
            }
        };
        Arrays.sort(a, comparator);
    }


    public static void main(String[] args){
        Integer[] b = new Integer[]{2,1,4,3,8,7};
        Integer[] a = new Integer[]{5,6,1,2,3,4,10,7,8};
        G3_SortArrayByOtherArray sorter = new G3_SortArrayByOtherArray();
        sorter.sort(a, b);
        //2,1,4,3,5,6,8,7,10
        ConsoleWriter.printIntArray(a);
    }
}
