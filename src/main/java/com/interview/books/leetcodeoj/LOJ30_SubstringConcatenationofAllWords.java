package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-19
 * Time: 下午2:21
 */
public class LOJ30_SubstringConcatenationofAllWords {

    public List<Integer> findSubstring(String S, String[] L) {
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
