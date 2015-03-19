package com.github.atemerev.hollywood.office;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

// 邮局
public class PostOffice {

    private static PostOffice instance;

    //邮局的信件的数量
    private int count = 0;

    //倒计时计数器
    private CountDownLatch latch;

    public static PostOffice instance() {
        if (instance == null) {
            instance = new PostOffice();
        }
        return instance;
    }

    private PostOffice() {
    }

    //当发送信件的时候,计数器会减少.当计数器=0时,会通知除于latch等待状态的线程往下执行
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
