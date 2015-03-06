package com.interview.books.fgdsb;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created_By: stefanie
 * Date: 15-2-5
 * Time: 下午4:57
 */
public class NLC39_OddEvenPrinter {
    static class Context{
        Semaphore evenPrinted = new Semaphore(1);
        Semaphore oddPrinted = new Semaphore(1);
        public Context(){
            try {
                oddPrinted.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class OddPrinter implements Runnable{
        Context context;
        int cur = 1;
        public OddPrinter(Context context){
            this.context = context;
        }
        @Override
        public void run() {
            while(true){
                try {
                    context.evenPrinted.acquire();
                    System.out.println(cur);
                    cur += 2;
                    TimeUnit.SECONDS.sleep(1);
                    context.oddPrinted.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    static class EvenPrinter implements Runnable{
        Context context;
        int cur = 2;
        public EvenPrinter(Context context){
            this.context = context;
        }
        @Override
        public void run() {
            while(true){
                try {
                    context.oddPrinted.acquire();
                    System.out.println(cur);
                    cur += 2;
                    TimeUnit.SECONDS.sleep(1);
                    context.evenPrinted.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Context context = new Context();
        new Thread(new OddPrinter(context)).start();
        new Thread(new EvenPrinter(context)).start();

        TimeUnit.SECONDS.sleep(60);
    }
}
