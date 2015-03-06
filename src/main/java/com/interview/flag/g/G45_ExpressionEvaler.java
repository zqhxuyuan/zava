package com.interview.flag.g;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 15-1-27
 * Time: 下午10:22
 */
public class G45_ExpressionEvaler {
    class Expression{
        String op;
        List<Integer> numbers = new ArrayList();
        public Expression(String op){
            this.op = op;
        }
        public int eval(){
            if(op.equals("*")){
                int product = 1;
                for(int i = 0; i < numbers.size(); i++) product *= numbers.get(i);
                return product;
            } else {
                int sum = 0;
                for(int i = 0; i < numbers.size(); i++) sum += numbers.get(i);
                return sum;
            }
        }
    }
    public int eval(String expression){
        String[] tokens = expression.split("\\s");
        Stack<Expression> stack = new Stack();
        for(int i = 0; i < tokens.length; i++){
            if(tokens[i].equals("(")) continue;
            else if(tokens[i].equals("*") || tokens[i].equals("+")) stack.push(new Expression(tokens[i]));
            else if(tokens[i].equals(")")){
                Expression expr = stack.pop();
                if(!stack.isEmpty()) stack.peek().numbers.add(expr.eval());
                else return expr.eval();
            } else stack.peek().numbers.add(Integer.parseInt(tokens[i]));
        }
        return stack.peek().eval();
    }

    public static void main(String[] args){
        G45_ExpressionEvaler evaler = new G45_ExpressionEvaler();
        System.out.println(evaler.eval("( * 1 ( + 1 2 3 ) )")); //6
        System.out.println(evaler.eval("( * ( + 1 1 ) 17 )")); //34
        System.out.println(evaler.eval("( * 17 ( + 1 1 ) )")); //34
    }
}
