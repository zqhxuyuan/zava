package com.interview.books.leetcodeoj;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午2:40
 */
public class LOJ150_EvaluateReversePolishNotation {
    //use stack to hold the numbers, when find a operator pop two and eval the value and push back to stack.
    //return stack.pop().
    static Set<String> operators = new HashSet();
    static {
        operators.add("+");
        operators.add("-");
        operators.add("*");
        operators.add("/");
    }
    public int evalRPN(String[] tokens) {
        if(tokens.length == 0) return 0;
        Stack<Integer> stack = new Stack();
        for(int i = 0; i < tokens.length; i++){
            String token = tokens[i];
            if(!operators.contains(token)){
                stack.push(Integer.parseInt(token));
            } else {
                int second = stack.pop();
                int first = stack.pop();
                stack.push(eval(first, second, token));
            }
        }
        return stack.pop();
    }

    public int eval(int first, int second, String token){
        switch(token){
            case "+": return first + second;
            case "-": return first - second;
            case "*": return first * second;
            case "/": return first / second;
        }
        return 0;
    }
}
