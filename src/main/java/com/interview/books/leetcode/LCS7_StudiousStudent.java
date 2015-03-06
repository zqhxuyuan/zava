package com.interview.books.leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午2:57
 */
public class LCS7_StudiousStudent {

    public String concatenateString(List<String> words){
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (o1 + o2).compareTo(o2 + o1);
            }
        };

        Collections.sort(words, comparator);
        StringBuilder builder = new StringBuilder();
        for(String word : words) builder.append(word);
        return builder.toString();
    }

    public static void main(String[] args){
        List<String> words = new ArrayList<>();
        words.add("jibw");
        words.add("ji");
        words.add("jp");
        words.add("bw");
        words.add("jibw");

        LCS7_StudiousStudent student = new LCS7_StudiousStudent();
        System.out.println(student.concatenateString(words));
    }
}
