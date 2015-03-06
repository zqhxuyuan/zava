package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午5:51
 */
public class LOJ78_Subset {
    //de dup by sort(S) and while(offset < S.length - 1 && S[offset + 1] == S[offset]) offset++;
    List<List<Integer>> sols;
    public List<List<Integer>> subsets(int[] S) {
        sols = new ArrayList<>();
        Arrays.sort(S);
        sols.add(new ArrayList());
        List<Integer> set = new ArrayList();
        subsets(S, 0, set);
        return sols;
    }

    public void subsets(int[] S, int offset, List<Integer> set){
        if(offset >= S.length) return;
        set.add(S[offset]);
        sols.add(new ArrayList(set));
        subsets(S, offset + 1, set);
        set.remove(set.size() - 1);
        while(offset < S.length - 1 && S[offset + 1] == S[offset]) offset++;
        subsets(S, offset + 1, set);
    }
}
