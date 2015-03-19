package com.miriamlaurel.hollywood.office;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public class PostOffice {

    private static PostOffice instance;
    private int count = 0;
    private CountDownLatch latch;

    public static PostOffice instance() {
        if (instance == null) {
            instance = new PostOffice();
        }
        return instance;
    }

    private PostOffice() {
    }

    public void send(Collection<Letter> letters) {
        for (Letter letter : letters) {
            System.out.println("Sending letter: " + letter);
            count++;
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    public int getSendLettersCount() {
        return count;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
