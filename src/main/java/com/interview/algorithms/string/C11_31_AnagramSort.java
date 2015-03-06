package com.interview.algorithms.string;

import com.interview.algorithms.general.C1_59_PrimeNumber;
import com.interview.basics.sort.QuickSorterThreeWay;
import com.interview.basics.sort.Sorter;

/**
 * Created_By: stefanie
 * Date: 14-10-19
 * Time: 上午9:37
 */
public class C11_31_AnagramSort {
    static class Element implements Comparable<Element>{
        Long key;
        String value;

        Element(Long key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Element o) {
            return this.key.compareTo(o.key);
        }
    }
    static Sorter<Element> SORTER = new QuickSorterThreeWay<>();
    static int[] PRIM;
    static{
        PRIM = C1_59_PrimeNumber.generate(26);
    }

    public static String[] sort(String[] array){
        Element[] elements = new Element[array.length];
        for(int i = 0; i < array.length; i++) elements[i] = new Element(getKey(array[i].toLowerCase()), array[i]);
        SORTER.sort(elements);
        for(int i = 0; i < array.length; i++) array[i] = elements[i].value;
        return array;
    }

    private static Long getKey(String str){
        long key = 1;
        for(int i = 0; i < str.length(); i++) key *= PRIM[str.charAt(i) - 'a'];
        return key;
    }
}
