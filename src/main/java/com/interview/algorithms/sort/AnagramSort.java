package com.interview.algorithms.sort;

/**
 * Two strings
 * Created_By: zouzhile
 * Date: 11/1/14
 * Time: 3:49 PM
 */
public class AnagramSort {

    public void sort(String[] array) {
        String[] anagramArray = new String[array.length];
        for(int i = 0; i < array.length; i ++) {
            anagramArray[i] = this.getBaseAnagram(array[i]);
        }

        this.sort(array, anagramArray, 0, array.length - 1);
    }

    private void sort(String[] array, String[] anagrams, int begin, int end) {
        if(begin <= end) {
            int pivot = begin - 1;
            String value = anagrams[end];
            for(int i = begin; i <= end; i ++) {
                if(anagrams[i].compareTo(value) <= 0) {
                    pivot ++;
                    this.switchElements(array, pivot, i);
                    this.switchElements(anagrams, pivot, i);
                }
            }
            this.sort(array, anagrams, begin, pivot-1);
            this.sort(array, anagrams, pivot + 1, end);
        }
    }

    private void switchElements(String[] array, int i, int j){
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private String getBaseAnagram(String s) {
        int[] counters = new int[256];
        for(char c : s.toCharArray())
            counters[c] += 1;
        String anagram = "";
        for(int i =0; i < counters.length; i ++)
            while(counters[i] > 0) {
                anagram += (char) i;
                counters[i] --;
            }

        return anagram;
    }

    public static void main(String[] args) {
        String[] array = new String[]{"yoshi", "eat", "shyoi", "army", "tea", "yshio", "aett", "mary", "yarm", "abc", "bac","ttae"};
        AnagramSort sorter = new AnagramSort();
        sorter.sort(array);
        String dump = "";
        for(String s : array)
            dump += s + " ";
        System.out.println(dump);
    }
}

