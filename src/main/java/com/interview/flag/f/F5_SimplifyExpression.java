package com.interview.flag.f;

import com.interview.basics.model.collection.hash.HashSet;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-10
 * Time: 上午11:03
 */
public class F5_SimplifyExpression {
    static String ONLYNUMBERS = "[0-9]*";
    static HashSet<String> operators = new HashSet<>();
    static {
        operators.add("+");
        operators.add("-");
        operators.add("*");
        operators.add("/");
    }


    //x 1 + 3 * 2 2x 5 + * + -> 7x + 13
    public String simplify(String[] expression){
        Stack<String> stack = new Stack<>();
        for(int i = 0; i < expression.length; i++){
            String variable = expression[i];
            if(operators.contains(variable)){
                String second = stack.pop();
                String first = stack.pop();
                String result = calculate(first, second, variable);
                stack.push(result);
            } else {
                stack.push(variable);
            }
        }
        return stack.pop();
    }

    public String calculate(String first, String second, String operator){
        boolean firstIsNumber = first.matches(ONLYNUMBERS);
        boolean secondIsNumber = second.matches(ONLYNUMBERS);
        if(firstIsNumber && secondIsNumber){
            return calculateNumber(first, second, operator);
        } else if(firstIsNumber && !secondIsNumber){
            StringBuilder builder = new StringBuilder();
            String[] parts = second.split(" ");
            boolean added = false;
            for(int i = 0; i < parts.length; i++){
                String part = parts[i];
                if(operators.contains(part)) builder.append(part + " ");
                else if(part.matches(ONLYNUMBERS)){
                    added = true;
                    builder.append(calculateNumber(first, part, operator) + " ");
                } else if(operator.equals("*") || operator.equals("/")){
                    int j = part.length() - 1;
                    for(; j > 0; j--){
                        if(part.substring(0,j).matches(ONLYNUMBERS)){
                            builder.append(calculateNumber(first, part.substring(0,j), operator) + part.substring(j) + " ");
                            break;
                        }
                    }
                    if(j == 0) {
                        builder.append(calculateNumber("1", second, operator) + part + " ");
                    }
                } else {
                    builder.append(part + " ");
                }
            }
            if((operator.equals("+") || operator.equals("-")) && added == false){
                builder.insert(0, first + " " + operator + " ");
            }
            return builder.toString();
        } else if(!firstIsNumber && secondIsNumber){
            String[] parts = first.split(" ");
            StringBuilder builder = new StringBuilder();
            boolean added = false;
            for(int i = 0; i < parts.length; i++){
                String part = parts[i];
                if(operators.contains(part)) builder.append(part + " ");
                else if(part.matches(ONLYNUMBERS)){
                    added = true;
                    builder.append(calculateNumber(part, second, operator) + " ");
                } else if(operator.equals("*") || operator.equals("/")){
                    int j = part.length() - 1;
                    for(; j > 0; j--){
                        if(part.substring(0,j).matches(ONLYNUMBERS)){
                            builder.append(calculateNumber(part.substring(0,j), second, operator) + part.substring(j, part.length()) + " ");
                            break;
                        }
                    }
                    if(j == 0){
                        builder.append(calculateNumber("1", second, operator) + part + " ");
                    }
                } else {
                    builder.append(part + " ");
                }
            }
            if((operator.equals("+") || operator.equals("-")) && added == false){
                builder.append(operator + " " + second + " ");
            }
            return builder.toString();
        } else {
            if(operator.equals("+") || operator.equals("-")){
                StringBuilder builder = new StringBuilder();
                String[] firstParts = first.split(" ");
                String[] secondParts = second.split(" ");
                for(int i = 0; i < firstParts.length; i++){
                    String firstPart = firstParts[i];
                    if(operator.contains(firstPart)) {
                        builder.append(firstPart + " ");
                        continue;
                    }
                    for(int j = 0; j < secondParts.length; j++){
                        String secondPart = secondParts[j];
                        if(firstPart.matches(ONLYNUMBERS) && secondPart.matches(ONLYNUMBERS)){
                            builder.append(calculateNumber(firstPart, secondPart, operator));
                            break;
                        } else if(!firstPart.matches(ONLYNUMBERS) && !secondPart.matches(ONLYNUMBERS)){
                            int p = firstPart.length() - 1;
                            for(; p > 0 && !firstPart.substring(0,p).matches(ONLYNUMBERS); p--);
                            int q = secondPart.length() - 1;
                            for(; q > 0 && !firstPart.substring(0,q).matches(ONLYNUMBERS); q--);
                            builder.append(calculateNumber(p == 0? "1" : firstPart.substring(0, p), q == 0? "1" : secondPart.substring(0, q), operator) + firstPart.substring(p));
                            break;
                        }
                    }
                }
                return builder.toString();
            }

        }
        return "";
    }

    public String calculateNumber(String first, String second, String operator){
        int num1 = Integer.parseInt(first);
        int num2 = Integer.parseInt(second);
        switch (operator){
            case "+": return String.valueOf(num1 + num2);
            case "-": return String.valueOf(num1 - num2);
            case "*": return String.valueOf(num1 * num2);
            case "/": return String.valueOf(num1 - num2);
            default:  return "";
        }
    }



    //（ x ＋ 1 ）＊ 3 ＋ 2 *（ 2x + 5 ） -> x 1 + 3 * 2 2x 5 + * +
    public String[] convertRPN(String[] expression){
        Stack<String> stack = new Stack<String>();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < expression.length; i++){
            String variable = expression[i];
            if(variable.equals("(")) {
                stack.push(variable);
            } else if(operators.contains(variable)){
                while(!stack.isEmpty() && highPriority(variable, stack.peek())){ //calculate the high
                    builder.append(stack.pop() + " ");
                }
                stack.push(variable);
            } else if(variable.equals(")")){
                while(!stack.isEmpty() && !stack.peek().equals("(")){
                    builder.append(stack.pop() + " ");
                }
                stack.pop();
            } else builder.append(variable + " ");
        }
        while(!stack.isEmpty()){
            builder.append(stack.pop() + " ");
        }
        return builder.toString().split(" ");
    }

    public boolean highPriority(String op1, String op2){
        if((op1.equals("+") || op1.equals("-")) && (op2.equals("*") || op2.equals("/"))) return true;
        else return false;
    }

    public static void main(String[] args){
        F5_SimplifyExpression simplifer = new F5_SimplifyExpression();
        String expre = "( x + 1 ) * 3 + 2 * ( 2x + 5 )";
        String[] expression = expre.split(" ");
        String[] RPN = simplifer.convertRPN(expression);
        //x 1 + 3 * 2 2x 5 + * +
        for(int i = 0; i < RPN.length; i++){
            System.out.print(RPN[i] + " ");
        }
        System.out.println();
        String result = simplifer.simplify(RPN);
        System.out.println(result);
    }
}
