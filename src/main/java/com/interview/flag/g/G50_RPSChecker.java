package com.interview.flag.g;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created_By: stefanie
 * Date: 15-2-3
 * Time: 上午11:56
 */
public class G50_RPSChecker {

    static class RPSChecker{
        private int limitation;
        private AtomicInteger count = new AtomicInteger(0);
        private AtomicLong startTimestamp = new AtomicLong(-1);

        public void setRPS(int count){
            this.limitation = count;
        }

        public boolean process(long timestamp){
            if (timestamp - startTimestamp.get() >= 1000) {
                count.set(1);
                startTimestamp.set(timestamp);
                return true;
            } else if(count.incrementAndGet() <= limitation) {
                return true;
            }
            return false;
        }

    }

    static class RequestSender implements Runnable{
        RPSChecker checker;
        int id;
        RequestSender(int id, RPSChecker checker){
            this.id = id;
            this.checker = checker;
        }
        @Override
        public void run() {
            while(true){
               if(checker.process(System.currentTimeMillis())){
                   System.out.printf("Sender %d sent a request\n", id);
               } else {
                   System.out.printf("Sender %d request rejected \n", id);
               }
                try {
                    TimeUnit.MILLISECONDS.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RPSChecker checker = new RPSChecker();
        checker.setRPS(7);
        ExecutorService executor = Executors.newCachedThreadPool();
        for(int i = 0; i < 5; i++){
            executor.execute(new RequestSender(i, checker));
        }
        TimeUnit.SECONDS.sleep(5);
        System.out.println("Adapt RPS to 15");
        checker.setRPS(15);
        TimeUnit.SECONDS.sleep(5);
        System.exit(0);
    }
}
