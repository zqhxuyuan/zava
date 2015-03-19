package com.zqh.actor;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Each CountActor will count the sum of 0 to 999, and then it will send the sum to SumActor where all the individual
 * sums got aggregated.
 */
class SumActor extends Actor<Long> {
    private long sum = 0;
    private int batchSize = 0;
    private CountDownLatch latch = null;

    public SumActor(int batchSize, CountDownLatch latch) {
        this.batchSize = batchSize;
        this.latch = latch;
    }

    @Override
    public void doWork(Long message) {
        sum += message;
        batchSize--;
        if (batchSize == 0) {
            this.stop();
            System.out.println(System.currentTimeMillis() + ", total is " + sum);
            latch.countDown();
        }
    }
}
class CountActor extends Actor<Long> {
    private long sum = 0;
    private SumActor sumActor;

    public CountActor(SumActor sumActor) {
        this.sumActor = sumActor;
    }

    @Override
    public void doWork(Long message) {
        if (message == -1) {
            this.stop();
            // send individual sum to SumActor for aggregation
            sumActor.addMessage(sum);
        }
        sum += message;
    }
}

public class ActorTest {
    private final int size = 1000;

    @Test
    public void testCounter() throws InterruptedException {
        System.out.println("start time " + System.currentTimeMillis());

        CountDownLatch latch = new CountDownLatch(1);
        final CountActor[] countActors = new CountActor[size];
        SumActor sumActor = new SumActor(size, latch);

        for (int i = 0; i < size; i++) {
            countActors[i] = new CountActor(sumActor);
            final int k = i;
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    countActors[k].addMessage((long) j);
                }
                countActors[k].addMessage((long) -1);
            }).start();
        }
        latch.await();
    }
}