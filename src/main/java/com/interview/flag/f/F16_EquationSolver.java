package com.interview.flag.f;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 15-1-29
 * Time: 下午4:48
 */
public class F16_EquationSolver {
    private Integer[] equation;

    public F16_EquationSolver(String[] equation){
        String[] normal = normalize(equation);
        this.equation = simplify(normal, normal.length);
    }

    public double solve(double X){
        if(equation[2] == 0) return 0;
        double result = equation[0] + equation[1] * X;
        return -1 * result / equation[2];
    }

    private String[] normalize(String[] equation){
        String[] normal = new String[equation.length + 2];
        int index = 0;
        while(index < equation.length && !equation[index].equals("=")) normal[index] = equation[index++];
        if(index < equation.length){
            normal[index++] = "-";
            normal[index++] = "(";
            while((index - 1) < equation.length) normal[index] = equation[(index++ - 1)];
            normal[index++] = ")";
        }
        return normal;
    }

    private Integer[] simplify(String[] equation, int length){
        Stack<Integer[]> stack = new Stack();
        Stack<Integer> operators = new Stack();

        Integer[] current = new Integer[]{0,0,0};
        int flag = 1;

        for(int i = 0; i < length; i++){
            if(equation[i].equals("+")) flag = 1;
            else if(equation[i].equals("-")) flag = -1;
            else if(equation[i].equals("(")){
                stack.push(current);
                operators.push(flag);
                flag = 1;
                current = new Integer[]{0,0,0};
            } else if(equation[i].equals(")")){
                current = eval(stack.pop(), current, operators.pop());
            } else {
                if(equation[i].endsWith("X")){
                    int number = getNumber(equation[i]);
                    current[1] += flag * number;
                } else if(equation[i].endsWith("Y")){
                    int number = getNumber(equation[i]);
                    current[2] += flag * number;
                } else {
                    int number = Integer.parseInt(equation[i]);
                    current[0] += flag * number;
                }
                flag = 1;
            }
        }
        return current;
    }

    private int getNumber(String str){
        if(str.length() == 1) return 1;
        else return Integer.parseInt(str.substring(0, str.length() - 1));
    }

    private Integer[] eval(Integer[] equ1, Integer[] equ2, int flag){
        for(int i = 0; i < equ1.length; i++){
            equ1[i] = equ1[i] + flag * equ2[i];
        }
        return equ1;
    }

    public static void main(String[] args){
        String[] equation = "3 + 2X + 5Y - ( 3 + 5X ) = 8 - 7Y + 2X".split("\\s");
        F16_EquationSolver solver = new F16_EquationSolver(equation);
        System.out.println(solver.solve(10)); //4.833
        System.out.println(solver.solve(-3)); //-0.583

        equation = "4X + Y - 16 = 5X - 5Y + 4 - ( 3X + 5 )".split("\\s");
        solver = new F16_EquationSolver(equation);
        System.out.println(solver.solve(10)); //-0.833
        System.out.println(solver.solve(-3)); //3.5
    }
}
