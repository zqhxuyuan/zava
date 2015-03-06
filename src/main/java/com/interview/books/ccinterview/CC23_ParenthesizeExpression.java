package com.interview.books.ccinterview;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午8:42
 */
public class CC23_ParenthesizeExpression {
    int[] total; //Cat(N): Catalan Sequence

    //Memo: key is "start-end", also can use int[exp.rows()][exp.rows()], but need set initial value to -1
    HashMap<String, Integer> trueCount;

    public int countWays(String exp, boolean result) {
        trueCount = new HashMap<>();
        total = catalan((exp.length() - 1) / 2);
        return countWays(exp, result, 0, exp.length() - 1);
    }

    private int countWays(String exp, boolean expected, int start, int end) {
        String key = start + "-" + end;

        int ways = 0;
        if (trueCount.containsKey(key)) ways = trueCount.get(key);
        else {
            if (start == end) {  //unit case
                if (exp.charAt(start) == '1') ways = 1;
                else ways = 0;
            }
            for (int i = start + 1; i <= end; i += 2) {  //enumerate places to insert parenthesis
                char op = exp.charAt(i);
                if (op == '&') {
                    ways += countWays(exp, true, start, i - 1) * countWays(exp, true, i + 1, end);
                } else if (op == '|') {
                    int leftOps = (i - 1 - start) / 2;
                    int rightOps = (end - i - 1) / 2;
                    int totalWays = total[leftOps] + total[rightOps];
                    int totalFalse = countWays(exp, false, start, i - 1) * countWays(exp, false, i + 1, end);
                    ways += totalWays - totalFalse;
                } else if (op == '^') {
                    ways += countWays(exp, true, start, i - 1) * countWays(exp, false, i + 1, end);
                    ways += countWays(exp, false, start, i - 1) * countWays(exp, true, i + 1, end);
                }
            }
            trueCount.put(key, ways);
        }

        if (expected) {   //expected == True
            return ways;
        } else {   //expected == False
            int len = (end - start) / 2;
            return total[len] - ways;
        }
    }

    private int[] catalan(int n) {
        int[] total = new int[n + 1];
        total[0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                total[i] += total[j] * total[i - 1 - j];
            }
        }
        return total;
    }

    public static void main(String[] args) {
        CC23_ParenthesizeExpression counter = new CC23_ParenthesizeExpression();
        String exp = "1^0|0|1";
        System.out.println(counter.countWays(exp, false));
    }
}
