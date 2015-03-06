package com.interview.leetcode.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 下午1:35
 * You are given a string, S, and a list of words, L, that are all of the same length.  (L are same length simplify the problem)
 * Find all starting indices of substring(s) in S that is a concatenation of each word in L exactly once and
 * without any intervening characters.
 *
 * For example, given:  S: "barfoothefoobarman" L: ["foo", "bar"]
 * You should return the indices: [0,9].   (order does not matter).
 *
 * Solutions:  similar like {@link MinWindowSubstring}
 *   1. create a hashmap of all words in L
 *   2. scan S from 0 ~ length - L.totalLength()
 *        every time get a word, check if exist in L, if yes, count it, if no break
 *        check the count, if count is larger than in L, break
 *        if found all the words, mark current i if a position
 *
 * Tricks:
 *  1. use a HashMap for string matching problem.
 */
public class ConcatenationSubString {

    public static List<Integer> findSubstring(String S, String[] L) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        HashMap<String, Integer> expected = new HashMap<String, Integer>();
        HashMap<String, Integer> found = new HashMap<String, Integer>();

        int m = L.length; int n = L[0].length();

        for(int i = 0; i < m; i++){
            if(!expected.containsKey(L[i])) expected.put(L[i], 1);
            else expected.put(L[i], expected.get(L[i]) + 1);
        }

        for(int i = 0; i <= S.length() - n * m; i++){
            found.clear();
            int j = 0;
            for(j = 0; j < m; j++){
                int k = i + j * n;
                String str = S.substring(k, k + n);
                if(expected.get(str) == null) break;  //contains a word doesn't expected
                if(!found.containsKey(str)) found.put(str, 1);
                else found.put(str, found.get(str) + 1);

                if(found.get(str) > expected.get(str)) break;  //word count found is larger than expected
            }
            if(j == m) result.add(i); //found all the word
        }

        return result;
    }
}
