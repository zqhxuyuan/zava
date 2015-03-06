package com.interview.flag.l;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created_By: stefanie
 * Date: 15-1-29
 * Time: 下午10:18
 */
public class L7_H2OSimulator {

    static class H2O {
        int count = 0;
        Semaphore H = new Semaphore(2);
        Semaphore O = new Semaphore(1);
        List<Integer> Hs = new ArrayList();
        List<Integer> Os = new ArrayList();
        public void H(int id) throws InterruptedException {
            H.acquire();
            Hs.add(id);
            H2O();
        }

        public void O(int id) throws InterruptedException {
            O.acquire();
            Os.add(id);
            H2O();
        }

        public void H2O() throws InterruptedException {
            if(Hs.size() == 2 && Os.size() == 1){
                System.out.printf("Generate %d-th H2O from H(%d, %d) and O(%d)\n", this.count++, Hs.get(0), Hs.get(1), Os.get(0));
                TimeUnit.SECONDS.sleep(1);
                Hs.clear();
                Os.clear();
                H.release(2);
                O.release();
            }
        }
    }

    static class HTask implements Runnable{
        H2O generator;
        int id;
        public HTask(int id, H2O generator){
            this.id = id;
            this.generator = generator;
        }
        @Override
        public void run() {
            while(true){
                try {
                    generator.H(id);
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    static class OTask implements Runnable{
        int id;
        H2O generator;
        public OTask(int id, H2O generator){
            this.id = id;
            this.generator = generator;
        }
        @Override
        public void run() {
            while(true){
                try {
                    generator.O(id);
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        H2O generator = new H2O();
        ExecutorService executor = Executors.newCachedThreadPool();
        for(int i = 0; i < 10; i++){
            executor.execute(new HTask(i, generator));
            executor.execute(new OTask(i, generator));
        }
        TimeUnit.MINUTES.sleep(1);
        System.exit(0);
    }
}
