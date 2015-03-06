package com.interview.algorithms.design;

import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/28/14
 * Time: 10:35 AM
 */
public class C10_8_FooClass {
    static class Foo{
        Semaphore sem1, sem2;
        public Foo() {
            try {
                sem1 = new Semaphore(1);
                sem2 = new Semaphore(1);
                sem1.acquire();
                sem2.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void first(){
            try {
                System.out.println("first is called");
                Thread.sleep(1000);
                System.out.println("first is finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                sem1.release();
            }
        }

        public void second() {
            try {
                sem1.acquire();
                System.out.println("second is called");
                Thread.sleep(1000);
                System.out.println("second is finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                sem2.release();
            }

        }

        public void third() {
            try {
                sem2.acquire();
                System.out.println("third is called");
                Thread.sleep(1000);
                System.out.println("third is finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    static class FirstWorker implements Runnable{
        public Foo foo;
        public FirstWorker(Foo foo){
           this.foo = foo;
        }
        @Override
        public void run() {
            foo.first();
        }
    }

    static class SecondWorker implements Runnable{
        public Foo foo;
        public SecondWorker(Foo foo){
            this.foo = foo;
        }
        @Override
        public void run() {
            foo.second();
        }
    }

    static class ThirdWorker implements Runnable{
        public Foo foo;
        public ThirdWorker(Foo foo){
            this.foo = foo;
        }
        @Override
        public void run() {
            foo.third();
        }
    }

    public static void main(String[] args){
        Foo foo = new Foo();
        Runnable worker1 = new FirstWorker(foo);
        Runnable worker2 = new SecondWorker(foo);
        Runnable worker3 = new ThirdWorker(foo);

        new Thread(worker1).start();
        new Thread(worker2).start();
        new Thread(worker3).start();
    }
}
