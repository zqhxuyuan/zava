package com.ctriposs.sdb;

import com.ctriposs.sdb.utils.TestUtil;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Simulate producer & consumer
 * 
 * Created by z_wu on 2014/5/26.
 */
public class SDBPerfTestB {
	
    private static String testDir = TestUtil.TEST_BASE_DIR + "sdb_perf_testb";
    private static SDB db;
    private static BlockingQueue<byte[]> keysInMemoryQueue = new LinkedBlockingQueue<byte[]>();
    private static final AtomicInteger producingItemCount = new AtomicInteger(0);
    private static final AtomicInteger consumingItemCount = new AtomicInteger(0);
    

    // configurable parameters
    //////////////////////////////////////////////////////////////////
    private static int loop = 5;
    private static int totalItemCount = 1000000;
    private static int producerNum = 4;
    private static int consumerNum = 4;
    private static int messageMaxLength = 16;
    //////////////////////////////////////////////////////////////////

    @Test
    public void testProducerAndConsumer() throws Exception{
    	System.out.println("SDB performance test begin ...");
        for(int i = 0; i < loop; i++) {
            db = new SDB(testDir);
            System.out.println("[doRunProduceThenConsume] round " + (i + 1) + " of " + loop);
            this.doRunProduceThenConsume();
            producingItemCount.set(0);
            consumingItemCount.set(0);
            db.close();
            db.destory();
        }
        System.out.println("[doRunProduceThenConsume] test ends");

        //Mixed type test
        System.out.println("Mixed type test begin");
        for(int i = 0; i < loop; i++) {
            db = new SDB(testDir);
            System.out.println("[doRunMixed] round " + (i + 1) + " of " + loop);
            this.doRunMixed();
            producingItemCount.set(0);
            consumingItemCount.set(0);
            db.close();
            db.destory();
        }
        System.out.println("[doRunMixed] test ends");
        System.out.println("Test on SDB finished successfully");
    }


    private static enum Status {
        ERROR,
        SUCCESS
    }

    private static class Result {
        Status status;
        long duration;
    }

    private static class Producer extends Thread {
        private final CountDownLatch latch;
        private final Queue<Result> resultQueue;
        public Producer(CountDownLatch latch, Queue<Result> resultQueue) {
            this.latch = latch;
            this.resultQueue = resultQueue;
        }

        public void run() {
            Result result = new Result();
            try {
                latch.countDown();
                latch.await();

                long start = System.nanoTime();
                while(true) {
                    int count = producingItemCount.incrementAndGet();
                    if(count > totalItemCount) break;
                    
                    byte[] key = TestUtil.randomString(messageMaxLength).getBytes();
                    
                    keysInMemoryQueue.offer(key);
                    db.put(key, key);
                }
                long end = System.nanoTime();
                result.status = Status.SUCCESS;
                result.duration = end - start;
            } catch (Exception e) {
                e.printStackTrace();
                result.status = Status.ERROR;
            }
            resultQueue.offer(result);
        }
    }

    private static class Consumer extends Thread {
        private final CountDownLatch latch;
        private final Queue<Result> resultQueue;

        public Consumer(CountDownLatch latch, Queue<Result> resultQueue) {
            this.latch = latch;
            this.resultQueue = resultQueue;
        }

        public void run() {
            Result result = new Result();
            result.status = Status.SUCCESS;
            try {
                latch.countDown();
                latch.await();

                long start = System.nanoTime();
                while(true) {
                    int count = consumingItemCount.getAndIncrement();
                    if (count >= totalItemCount) break;
                    
                    byte[] keyBytes = keysInMemoryQueue.take();
                    if(keyBytes != null) {
                       byte[] valueBytes = db.get(keyBytes);
                       // wait a moment for k/v to be put in the DB
                       while(valueBytes == null) {
                    	   valueBytes = db.get(keyBytes);
                       }
                       if (!new String(keyBytes).equals(new String(valueBytes))) {
                           result.status = Status.ERROR;
                       }
                    }
                }
                long end = System.nanoTime();
                result.duration = end - start;
            } catch (Exception e) {
                e.printStackTrace();
                result.status = Status.ERROR;
            }
            resultQueue.offer(result);
        }
    }
    public void doRunProduceThenConsume() throws InterruptedException{
        CountDownLatch producerLatch = new CountDownLatch(producerNum);
        CountDownLatch consumerLatch = new CountDownLatch(consumerNum);
        BlockingQueue<Result> producerResults = new LinkedBlockingQueue<Result>();
        BlockingQueue<Result> consumerResults = new LinkedBlockingQueue<Result>();
        long totalProducingTime = 0;
        long totalConsumingTime = 0;
        long start = System.nanoTime();
        // the producer start
        for(int i = 0; i < producerNum; i++) {
            Producer p = new Producer(producerLatch, producerResults);
            p.start();
        }

        for(int i = 0; i < producerNum; i++) {
            Result result = producerResults.take();
            assertEquals(result.status, Status.SUCCESS);
            totalProducingTime += result.duration;
        }
        long end = System.nanoTime();


        System.out.println("-----------------------------------------------");

        System.out.println("Producing test result:");
        System.out.printf("Total test time = %d ns.\n", (end - start));
        System.out.printf("Total item count = %d\n", totalItemCount);
        System.out.printf("Producer thread number = %d\n", producerNum);
        System.out.printf("Item message length = %d bytes\n", messageMaxLength);
        System.out.printf("Total producing time =  %d ns.\n", totalProducingTime);
        System.out.printf("Average producint time = %d ns.\n", totalProducingTime / producerNum);
        System.out.println("-----------------------------------------------");
        TestUtil.getSDBStats(db.getStats());

        // the consumer start
        start = System.nanoTime();
        for(int i = 0; i < consumerNum; i++) {
            Consumer c = new Consumer(consumerLatch, consumerResults);
            c.start();
        }

        for(int i = 0; i < consumerNum; i++) {
            Result result = consumerResults.take();
            assertEquals(result.status, Status.SUCCESS);
            totalConsumingTime += result.duration;
        }
        end = System.nanoTime();
        assertEquals(producingItemCount.get(), consumingItemCount.get());
        assertTrue(keysInMemoryQueue.isEmpty());

        System.out.println("Consuming test result:");
        System.out.printf("Total test time = %d ns.\n", (end - start));
        System.out.printf("Total item count = %d\n", totalItemCount);
        System.out.printf("Consumer thread number = %d\n", consumerNum);
        System.out.printf("Item message length = %d bytes\n", messageMaxLength);
        System.out.printf("Total consuming time =  %d ns.\n", totalConsumingTime);
        System.out.printf("Average consuming time = %d ns.\n", totalConsumingTime / consumerNum);
        System.out.println("-----------------------------------------------");
    }

    public void doRunMixed() throws Exception {
        //prepare
        CountDownLatch allLatch = new CountDownLatch(producerNum + consumerNum);
        BlockingQueue<Result> producerResults = new LinkedBlockingQueue<Result>();
        BlockingQueue<Result> consumerResults = new LinkedBlockingQueue<Result>();

        long totalProducingTime = 0;
        long totalConsumingTime = 0;

        long start = System.nanoTime();
        //run testing
        for(int i = 0; i < producerNum; i++) {
            Producer p = new Producer(allLatch, producerResults);
            p.start();
        }

        for(int i = 0; i < consumerNum; i++) {
            Consumer c = new Consumer(allLatch, consumerResults);
            c.start();
        }

        //verify and report
        for(int i = 0; i < producerNum; i++) {
            Result result = producerResults.take();
            assertEquals(result.status, Status.SUCCESS);
            totalProducingTime += result.duration;
        }

        for(int i = 0; i < consumerNum; i++) {
            Result result = consumerResults.take();
            assertEquals(result.status, Status.SUCCESS);
            totalConsumingTime += result.duration;
        }

        long end = System.nanoTime();

        assertEquals(producingItemCount.get(), consumingItemCount.get());

        System.out.println("-----------------------------------------------");
        System.out.printf("Total test time = %d ns.\n", (end - start));
        System.out.printf("Total item count = %d\n", totalItemCount);
        System.out.printf("Producer thread number = %d\n", producerNum);
        System.out.printf("Consumer thread number = %d\n", consumerNum);
        System.out.printf("Item message length = %d bytes\n", messageMaxLength);
        System.out.printf("Total consuming time =  %d ns.\n", totalConsumingTime);
        System.out.printf("Average consuming time = %d ns.\n", totalConsumingTime / consumerNum);
        System.out.printf("Total producing time =  %d ns.\n", totalProducingTime);
        System.out.printf("Average producing time = %d ns.\n", totalProducingTime / producerNum);
        System.out.println("-----------------------------------------------");
    }
}
