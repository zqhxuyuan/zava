package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午3:49
 */
public class LOJ22_GenerateParenthese {
    List<String> sols;
    public List<String> generateParenthesis(int n) {
        sols = new ArrayList();
        if(n <= 0) return sols;
        generate(n, n, "");
        return sols;
    }

    public void generate(int left, int right, String prefix){
        if(left == 0 && right == 0){
            sols.add(prefix);
            return;
        }
        if(left > 0) generate(left - 1, right, prefix + "(");
        if(right > left) generate(left, right - 1, prefix + ")");
    }
}
