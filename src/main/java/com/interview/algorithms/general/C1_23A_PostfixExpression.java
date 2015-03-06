package com.interview.algorithms.general;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/25/14
 * Time: 3:04 PM
 */
public class C1_23A_PostfixExpression {

    public static String transform(String exp){
        Stack<Character> ops = new Stack<>();
        StringBuilder builder = new StringBuilder();
        for(char ch : exp.toCharArray()){
            if(ch == ' ') continue;
            if(isNumber(ch)) builder.append(ch);
            else {
                builder.append(' ');
                if(ch == ')') pop(ops, builder);
                else ops.push(ch);
            }
        }
        pop(ops, builder);
        return builder.toString();
    }

    private static void pop(Stack<Character> ops, StringBuilder builder){
        while(!ops.isEmpty()){
            Character ch = ops.pop();
            if(ch == '(') return;
            builder.append(ch);
        }
    }

    private static boolean isOperation(char charValue) {
        return charValue == '+' || charValue == '-'
                || charValue == '*' || charValue == '/';
    }

    private static boolean isNumber(char charValue){
        return charValue == '.'
                || (charValue >= '0' && charValue <= '9');
    }
}
