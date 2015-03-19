package com.miriamlaurel.hollywood.office;

import com.miriamlaurel.hollywood.future.Promise;
import com.miriamlaurel.hollywood.future.PromiseExecutorService;

import java.util.concurrent.Executors;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class Fax {

    private static Fax instance = null;

    public static Fax instance() {
        if (instance == null) {
            instance = new Fax();
        }
        return instance;
    }

    private Fax() {
    }


    private PromiseExecutorService executor = new PromiseExecutorService(Executors.newFixedThreadPool(1));

    public Promise<Void> send(FaxMessage message) {
        return executor.submit(new SendFaxTask(message));  
    }

    class SendFaxTask implements Runnable {

        private FaxMessage message;

        SendFaxTask(FaxMessage message) {
            this.message = message;
        }

        public void run() {
            try {
                System.out.println("Starting...");
                Thread.sleep(50);
                System.out.println("Sending " + message + " ...done.");
            } catch (InterruptedException e) {
                // ignore.
            }
        }
    }
}
