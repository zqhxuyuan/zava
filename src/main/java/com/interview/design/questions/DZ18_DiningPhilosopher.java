package com.interview.design.questions;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/28/14
 * Time: 11:00 AM
 */
public class DZ18_DiningPhilosopher {
    static class Chopstick{
        int id;
        Lock lock;

        Chopstick(int id) {
            this.id = id;
            lock = new ReentrantLock();
        }

        public boolean pickUp(){
            return lock.tryLock();
        }
        public void putDown(){
            lock.unlock();
        }
    }

    static class Philosopher implements Runnable{
        static int bites = 10;
        int id;
        Chopstick left;
        Chopstick right;

        Philosopher(int id, Chopstick left, Chopstick right) {
            this.id = id;
            this.left = left;
            this.right = right;
        }

        public boolean pickUp(){
            if(!left.pickUp()){
                return false;
            }
            if(!right.pickUp()){
                left.putDown();
                return false;
            }
            //System.out.printf("Philosopher-%d: Both Chopstick %d and %d are picked up. Ready to eat.\n", id, left.id, right.id);
            return true;
        }

        public void putDown(){
            left.putDown();
            right.putDown();
            //System.out.printf("Philosopher-%d: Both Chopstick %d and %d are putted up. Finish eat.\n", id, left.id, right.id);
        }

        public void chew(){
            try {
                System.out.printf("Philosopher-%d: eatting\n", id);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void eat(){
            while(!pickUp()) {
                try {
                    System.out.printf("Philosopher-%d: is waiting for Chopstick\n", id);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            chew();
            putDown();
        }

        @Override
        public void run() {
            for(int i = 0; i < bites; i++) {
                eat();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args){
        int number = 10;
        Philosopher[] philosopher = new Philosopher[number];
        Chopstick[] chopsticks = new Chopstick[number];
        for(int i = 0; i < number; i++) chopsticks[i] = new Chopstick(i);
        for(int i = 0; i < number; i++){
            int left = i;
            int right = i + 1 < number? i + 1 : 0;
            philosopher[i] = new Philosopher(i, chopsticks[left], chopsticks[right]);
        }

        for(int i = 0; i < number; i++)
            new Thread(philosopher[i]).start();

    }
}
