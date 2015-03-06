package com.interview.design.pattern.behavioral;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午2:41
 *
 * 策略模式定义了一系列算法，并将每个算法封装起来，使他们可以相互替换，且算法的变化不会影响到使用算法的客户。
 * 需要设计一个接口，为一系列实现类提供统一的方法，多个实现类实现该接口，设计一个抽象类（可有可无，属于辅助类），提供辅助函数
 *
 * 策略模式的决定权在用户，系统本身提供不同算法的实现，新增或者删除算法，对各种算法做封装。
 * 因此，策略模式多用在算法决策系统中，外部用户只需要决定用哪个算法即可
 */
public class StrategyPattern {
    /**
     * Calculator is interface of a algorithm or function, it have 2 different implementation Add and Minus
     * Give a Context expose the same interface and can choose different implementation of Strategy
     */
    static interface Calculator{
        public int calculate(int num1, int num2);
    }

    static class Add implements Calculator{

        @Override
        public int calculate(int num1, int num2) {
            return num1 + num2;
        }
    }

    static class Minus implements Calculator{

        @Override
        public int calculate(int num1, int num2) {
            return num1 - num2;
        }
    }

    static class Context{
        Calculator strategy;

        public Context(Calculator calculator){
            this.strategy = calculator;
        }

        public int calculate(int num1, int num2){
            return this.strategy.calculate(num1, num2);
        }
    }

    public static void main(String[] args){
        Context context = new Context(new Add());
        System.out.println(context.calculate(1, 2));

        context = new Context(new Minus());
        System.out.println(context.calculate(1, 2));
    }
}
