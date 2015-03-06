package com.interview.leetcode.math;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-11-20
 * Time: 上午10:02
 */
public class Expressions {
    static Set<String> operators = new HashSet<String>();
    static {
        operators.add("*");
        operators.add("-");
        operators.add("+");
        operators.add("/");
    }

    public static int calculate(int num1, int num2, String operator){
        switch(operator){
            case "+": return num1 + num2;
            case "*": return num1 * num2;
            case "-": return num1 - num2;
            case "/": return num1 / num2;
            default : return 0;
        }
    }
    public static boolean validInteger(String s){
        try{
            Integer.parseInt(s);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Verify whether an arithmatic expression is valid, suppose the expression only contains "+", "-", "*", "/", "(", ")" and numbers are integers.
     * Invalid Case:
     *   0. ) without (, or ( without ), example: 2+)5*(2+4) or 2+(5*(2+4)
     *   1. 2 operators together:     5**2
     *   2. () and number together:   (5*2)2 or 2(5*2)
    */
    public static boolean valid(String str){
        boolean prevNumber = false;
        boolean needOperator = false;
        for(int i = 0; i < str.length(); i++){
            char ch = str.charAt(i);
            if(ch == ' ') continue;
            else if(Character.isDigit(ch)) {
                if (needOperator) return false;    // invalid case: (5*2)2
                prevNumber = true;
            } else if(ch == ')') return false;      //invalid case: 2+)5*(2+4)
            else if(ch == '(') {
                if(prevNumber) return false;       //2(2+2)
                int close = str.lastIndexOf(')');
                if(close == -1)  return false;     //invalid case: 2+(5*(2+4)
                if(!valid(str.substring(i + 1, close))) return false;   //valid substring
                prevNumber = true;
                needOperator = true;
                i = close;
            } else {  //check preNumber
                if(prevNumber == false) return false;     //invalid case: 5**2
                prevNumber = false;
                needOperator = false;
            }
        }
        return true;
    }

    static class ReversePolishNotation{
        public boolean verify(String[] tokens){
            if(tokens.length == 0) return false;
            Stack<Integer> stack = new Stack<Integer>();
            for(String token: tokens){
                if(!operators.contains(token)){
                    if(!validInteger(token)) return false;
                    stack.push(0);
                } else {
                    if(stack.size() < 2) return false;
                    stack.pop();
                }
            }
            return (stack.size() == 1)? true : false;
        }

        public int eval(String[] tokens) {
            if(tokens.length == 0) return 0;
            Stack<Integer> stack = new Stack<Integer>();

            for(String token : tokens){
                if(!operators.contains(token)) stack.push(Integer.parseInt(token));
                else {
                    int num2 = stack.pop();
                    int num1 = stack.pop();
                    stack.push(Expressions.calculate(num1, num2, token));
                }
            }
            return stack.pop();
        }

        public String tranformRPN(String[] tokens){
            StringBuilder builder = new StringBuilder();
            Stack<String> operatorsStack  = new Stack<String>();
            for(String token : tokens){
                if(token.equals("(") || operators.contains(token)) operatorsStack.push(token);
                else if(token.equals(")")) popOperators(operatorsStack, builder);
                else {
                    if(builder.length() > 0) builder.append(" ");
                    builder.append(token);
                }
            }
            popOperators(operatorsStack, builder);
            return builder.toString();
        }

        public String tranformFromRPN(String[] tokens){
            StringBuilder builder = new StringBuilder();
            Stack<String> numbers = new Stack<>();
            for(String  token : tokens){
                if(!operators.contains(token)) numbers.push(token);
                else {
                    String number = numbers.pop();
                    if(builder.length() == 0) {
                        String number2 = numbers.pop();
                        builder.append("( " + number2 + " " + token + " " + number + " )");
                    } else {
                        builder.append(" )");
                        builder.insert(0, "( " + number + " " + token + " ");
                    }
                }
            }
            return builder.toString();
        }

        public void popOperators(Stack<String> operators, StringBuilder builder){
            while(!operators.isEmpty()) {
                String operator = operators.pop();
                if(operator.equals("(")) break;
                if(builder.length() > 0) builder.append(" ");
                builder.append(operator);
            }
        }
    }

    /**
     * Given a string containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.
     */
    static class ValidParentheses{
        private static final Map<Character, Character> map =
                new HashMap<Character, Character>() {{
                    put('(', ')');
                    put('{', '}');
                    put('[', ']');
                }};

        public boolean isValid(String s) {
            Stack<Character> stack = new Stack<>();
            for (char c : s.toCharArray()) {
                if (map.containsKey(c)) {
                    stack.push(c);
                } else if (stack.isEmpty() || map.get(stack.pop()) != c) return false;
            }
            return stack.isEmpty();
        }
    }

}
