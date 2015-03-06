package com.interview.design.questions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created_By: stefanie
 * Date: 14-12-17
 * Time: 下午3:11
 */
public class DZ20_ThreeThreadMethod {
    public static class Foo {
        Semaphore second;
        Semaphore third;

        public Foo() throws InterruptedException {
            second = new Semaphore(1);
            third = new Semaphore(1);
            second.acquire();
            third.acquire();
        }

        public void first() throws InterruptedException {
            Thread.sleep(3000);
            System.out.println("first is called");
            second.release();
        }

        public void second() throws InterruptedException {
            second.acquire();
            Thread.sleep(2000);
            System.out.println("second is called");
            third.release();
        }

        public void third() throws InterruptedException {
            third.acquire();
            Thread.sleep(1000);
            System.out.println("thrid is called");
        }
    }

    public static class Task implements Runnable {
        private Foo foo;
        private int index;

        public Task(Foo foo, int index) {
            this.foo = foo;
            this.index = index;
        }

        @Override
        public void run() {
            System.out.println("Task " + index + " is started.");
            try {
                switch (index) {
                    case 0:
                        System.out.println("call first");
                        foo.first();
                        break;
                    case 1:
                        System.out.println("call second");
                        foo.second();
                        break;
                    case 2:
                        System.out.println("call third");
                        foo.third();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Foo foo = new Foo();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i = 0; i < 3; i++){
            executorService.submit(new Task(foo, i));
        }
    }
}
