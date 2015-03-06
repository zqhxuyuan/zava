package com.interview.algorithms.stackqueue;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-11-8
 * Time: 下午4:12
 */
public class C7_10_LongestValidParetheses {
    public static String longestValidParentheses(String s) {
        String max = "";
        Stack<String> stack = new Stack<String>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(') stack.push("(");
            else {
                String prev = "";
                String current = "";
                while (!stack.empty()) {
                    prev = stack.pop();
                    current = prev + current;
                    if (prev == "(") {
                        stack.push(current + ")");
                        break;
                    }
                }
                if (current.length() > max.length()) max = current;
            }
        }
        String current = "";
        while (!stack.empty()) {
            String item = stack.pop();
            if (item != "(") current = item + current;
            else {
                if (current.length() > max.length()) max = current;
                current = "";
            }
        }
        if (current.length() > max.length()) max = current;
        return max;
    }
}
