package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午5:39
 */
public class LOJ77_Combination {
    List<List<Integer>> sols;
    public List<List<Integer>> combine(int n, int k) {
        sols = new ArrayList<>();
        List<Integer> cur = new ArrayList();
        combine(n, 1, cur, k);
        return sols;
    }

    private void combine(int n, int offset, List<Integer> cur, int k){
        if(offset > n) return;
        cur.add(offset);
        if(cur.size() == k){
            sols.add(new ArrayList(cur));
        } else {
            combine(n, offset + 1, cur, k);
        }
        cur.remove(cur.size() - 1);
        combine(n, offset + 1, cur, k);
    }
}
