package com.interview.algorithms.general;

import com.interview.basics.sort.QuickSorter;
import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/28/14
 * Time: 3:45 PM
 */
public class C1_51_AllPermutation {
    static QuickSorter<Character> SORTER = new QuickSorter<>();

    public static void printRecursive(Character[] chars){
        printRecursive("", chars);
    }

    private static void printRecursive(String prefix, Character[] chars){
        if(prefix.length() == chars.length) {
            System.out.println(prefix);
            return;
        }
        for(char ch : chars){
            if(!prefix.contains(ch+"")) printRecursive(prefix + ch, chars);
        }
    }

    public static void printDicSort(Character[] chars){
        SORTER.sort(chars);
        ConsoleWriter.printCharacterArray(chars);
        while(true){
            int i;
            for (i = chars.length - 2; i >= 0; --i) {
                if (chars[i] < chars[i + 1]) break;
            }
            if (i < 0)  break;
            int k;
            for (k = chars.length - 1; k > i; --k) {
                if (chars[k] > chars[i]) break;
            }
            ArrayUtil.swap(chars, i, k);
            ArrayUtil.reverse(chars, i + 1, chars.length - 1);
            ConsoleWriter.printCharacterArray(chars);
        }
    }
}
