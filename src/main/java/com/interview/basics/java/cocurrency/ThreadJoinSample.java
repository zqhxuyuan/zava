package com.interview.basics.java.cocurrency;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 11/4/14
 * Time: 11:16 AM
 */
public class ThreadJoinSample {
    static class Sleeper extends Thread {
        private int duration;
        public Sleeper(String name, int sleepTime){
            super(name);
            duration = sleepTime;
            start();
        }
        public void run(){
            try {
                sleep(duration);
            } catch (InterruptedException e) {
                System.out.println(getName() + " was interrupted. " + "isInterrupted(): " + isInterrupted());
                return;
            }
            System.out.println(getName() + " has awakened");
        }
    }

    static class Joiner extends Thread{
        private Sleeper sleeper;
        public Joiner(String name, Sleeper sleeper){
            super(name);
            this.sleeper = sleeper;
            start();
        }

        public void run(){
            try {
                System.out.println(getName() + " calling " + sleeper.getName() + " to join.");
                sleeper.join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
            System.out.println(getName() + " join completed.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Sleeper sleepy = new Sleeper("Sleepy", 2000);
        Sleeper grumpy = new Sleeper("Grumpy", 2000);
        Joiner  dopey = new Joiner("Dopey", sleepy);
        Joiner  doc = new Joiner("Doc", grumpy);
        TimeUnit.MILLISECONDS.sleep(1000);
        grumpy.interrupt();
    }
}
