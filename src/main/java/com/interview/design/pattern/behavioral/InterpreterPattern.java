package com.interview.design.pattern.behavioral;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午6:05
 *
 * Interpreter pattern provides way to evaluate language grammar or expression.
 * This pattern involves implementing a expression interface which tells to interpret a particular context.
 * This pattern is used in SQL parsing, symbol processing engine etc.
 */
public class InterpreterPattern {
    /**
     * create an interface Expression and concrete classes implementing the Expression interface.
     * A class TerminalExpression is defined which acts as a main interpreter of context in question.
     * Other classes OrExpression, AndExpression are used to create combinational expressions.
     */

    static interface Expression {
        public boolean interpret(String context);
    }

    static class TerminalExpression implements Expression {

        private String data;

        public TerminalExpression(String data){
            this.data = data;
        }

        @Override
        public boolean interpret(String context) {
            if(context.contains(data)){
                return true;
            }
            return false;
        }
    }

    static class OrExpression implements Expression {

        private Expression expr1 = null;
        private Expression expr2 = null;

        public OrExpression(Expression expr1, Expression expr2) {
            this.expr1 = expr1;
            this.expr2 = expr2;
        }

        @Override
        public boolean interpret(String context) {
            return expr1.interpret(context) || expr2.interpret(context);
        }
    }

    static class AndExpression implements Expression {

        private Expression expr1 = null;
        private Expression expr2 = null;

        public AndExpression(Expression expr1, Expression expr2) {
            this.expr1 = expr1;
            this.expr2 = expr2;
        }

        @Override
        public boolean interpret(String context) {
            return expr1.interpret(context) && expr2.interpret(context);
        }
    }

    //Rule: Robert and John are male
    public static Expression getMaleExpression(){
        Expression robert = new TerminalExpression("Robert");
        Expression john = new TerminalExpression("John");
        return new OrExpression(robert, john);
    }

    //Rule: Julie is a married women
    public static Expression getMarriedWomanExpression(){
        Expression julie = new TerminalExpression("Julie");
        Expression married = new TerminalExpression("Married");
        return new AndExpression(julie, married);
    }

    public static void main(String[] args) {
        Expression isMale = getMaleExpression();
        Expression isMarriedWoman = getMarriedWomanExpression();

        System.out.println("John is male? " + isMale.interpret("John"));
        System.out.println("Julie is a married women? "
                + isMarriedWoman.interpret("Married Julie"));
        System.out.println("David is male? " + isMale.interpret("David"));
        System.out.println("Lucy is a married women? "
                + isMarriedWoman.interpret("Married Lucy"));
    }
}
