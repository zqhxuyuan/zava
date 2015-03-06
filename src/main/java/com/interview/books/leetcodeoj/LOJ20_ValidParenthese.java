package com.interview.books.leetcodeoj;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午3:42
 */
public class LOJ20_ValidParenthese {
    //return stack.isEmpty() when parse over the string.
    static HashMap<Character, Character> pairs = new HashMap();
    static {
        pairs.put('(', ')');
        pairs.put('[', ']');
        pairs.put('{', '}');
    }
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack();
        for(int i = 0; i < s.length(); i++){
            char ch = s.charAt(i);
            if(pairs.containsKey(ch)) stack.push(ch);
            else {
                if(stack.isEmpty() || pairs.get(stack.pop()) != ch) return false;
            }
        }
        return stack.isEmpty();
    }
}
