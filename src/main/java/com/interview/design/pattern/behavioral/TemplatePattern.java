package com.interview.design.pattern.behavioral;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午2:52
 *
 * In Template pattern, an abstract class exposes defined way(s)/template(s) to execute its methods.
 * Its subclasses can overrides the method implementations as per need basis but the invocation is to be
 * in the same way as defined by an abstract class.
 *
 * 模板方法模式，就是指：一个抽象类中，有一个主方法，再定义1...n个方法，可以是抽象的，也可以是实际的方法，
 * 定义一个类，继承该抽象类，重写抽象方法，通过调用抽象类，实现对子类的调用
 */
public class TemplatePattern {

    static abstract class Game {
        abstract void initialize();
        abstract void startPlay();
        abstract void endPlay();

        //template method
        public final void play(){

            //initialize the game
            initialize();

            //start game
            startPlay();

            //end game
            endPlay();
        }
    }

    static class Cricket extends Game {

        @Override
        void endPlay() {
            System.out.println("Cricket Game Finished!");
        }

        @Override
        void initialize() {
            System.out.println("Cricket Game Initialized! Start playing.");
        }

        @Override
        void startPlay() {
            System.out.println("Cricket Game Started. Enjoy the game!");
        }
    }

    static class Football extends Game {

        @Override
        void endPlay() {
            System.out.println("Football Game Finished!");
        }

        @Override
        void initialize() {
            System.out.println("Football Game Initialized! Start playing.");
        }

        @Override
        void startPlay() {
            System.out.println("Football Game Started. Enjoy the game!");
        }
    }

    public static void main(String[] args) {

        Game game = new Cricket();
        game.play();
        System.out.println();
        game = new Football();
        game.play();
    }
}
