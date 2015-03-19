package com.github.atemerev.hollywood.office;

import com.github.atemerev.hollywood.future.Promise;
import com.github.atemerev.hollywood.future.PromiseExecutorService;

import java.util.concurrent.Executors;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class Fax {

    private static Fax instance = null;

    private Fax() {
    }

    //只有一个传真哦
    public static Fax instance() {
        if (instance == null) {
            instance = new Fax();
        }
        return instance;
    }

    //线程池
    private PromiseExecutorService executor = new PromiseExecutorService(Executors.newFixedThreadPool(1));

    //发送一个传真: 从线程池中取出一个线程来执行
    public Promise<Void> send(FaxMessage message) {
        return executor.submit(new SendFaxTask(message));  
    }

    //发送传真的动作启用新线程. 因为耗时比较长
    class SendFaxTask implements Runnable {

        private FaxMessage message;

        SendFaxTask(FaxMessage message) {
            this.message = message;
        }

        public void run() {
            try {
                System.out.println("Starting...");
                //模拟耗时动作
                Thread.sleep(50);
                System.out.println("Sending " + message + " ...done.");
            } catch (InterruptedException e) {
                // ignore.
            }
        }
    }
}
