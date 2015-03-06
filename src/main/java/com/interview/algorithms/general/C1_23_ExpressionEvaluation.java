package com.interview.algorithms.general;

import com.interview.basics.model.collection.stack.LinkedStack;
import com.interview.basics.model.collection.stack.Stack;

/**
 * Given an arithmatic expression, evaluate the value of the expression,
 * suppose the expression only contains "+", "-", "*", "/", "(", ")" and numbers are integers.
 *
 * Created_By: zouzhile
 * Date: 1/15/14
 * Time: 2:55 PM
 */
public class C1_23_ExpressionEvaluation {

    public double evaluate(String expression) {
        Stack<String> ops = new LinkedStack<String>();
        Stack<Double> numbers = new LinkedStack<Double>();
        char[] charArray = expression.toCharArray();
        double value = 0;
        for(int i = 0; i < charArray.length ; i ++) {
            char currentChar = charArray[i];
            if(')' == currentChar) {
                double number2 = numbers.pop();
                double number1 = numbers.pop();
                char op = ops.pop().charAt(0);
                switch(op) {
                    case '+' : value = number1 + number2; break;
                    case '-' : value = number1 - number2; break;
                    case '*' : value = number1 * number2; break;
                    case '/' : value = number1 / number2; break;
                }
                numbers.push(value);
            } else if('(' == currentChar || ' ' == currentChar) {
                continue; // do nothing here
            } else if(this.isOperation(currentChar)) {
                ops.push(currentChar + "");
            } else if(this.isNumber(currentChar)) {
                String numberValue = "";
                for(; i < charArray.length; i++) {
                    currentChar = charArray[i];
                    // the dotProduct '.' of the double value is considered as a number, e.g. 9.5
                    if(! this.isNumber(currentChar)) {
                        i-- ; // move back to the last digit of the numeric value
                        numbers.push(Double.parseDouble(numberValue));
                        break;
                    }
                    numberValue += currentChar;
                }
            }
        }
        return value;
    }

    private boolean isOperation(char charValue) {
        return charValue == '+' || charValue == '-'
                || charValue == '*' || charValue == '/';
    }

    private boolean isNumber(char charValue) {
        return ! (charValue == '+' || charValue == '-'
                || charValue == '*' || charValue == '/'
                || charValue == '(' || charValue == ')');
    }

    public static void main(String[] args) {
        C1_23_ExpressionEvaluation evaluater = new C1_23_ExpressionEvaluation();
        System.out.println(evaluater.evaluate("(3.9+5.7)"));
    }
}
