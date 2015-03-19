package com.miriamlaurel.hollywood.promise;

import com.miriamlaurel.hollywood.future.Promise;
import com.miriamlaurel.hollywood.future.PromiseExecutorService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class PromiseTest {

    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private PromiseExecutorService promiseExecutor = new PromiseExecutorService(executor);

    public @Test void testPromiseRun() throws Exception {

        final boolean executed[] = {false, false, false};

        final Promise<Integer> p0 = promiseExecutor.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                Thread.sleep(50); // Must be long enough, see step (*)
                executed[0] = true;
                return 6; // Determined by fair dice throw
            }
        });
        Assert.assertTrue(true); // Promise started

        // Let's append a promise while the original promise is working
        Promise<?> p1 = p0.append(new Runnable() {
            public void run() {
                executed[1] = true;
            }
        });

        Assert.assertFalse(executed[0]); // (*) Ensure the thread still works
        Assert.assertEquals(6, (int) p0.get()); // Join it and check if the result is OK
        Assert.assertTrue(executed[0]);

        Thread.sleep(10); // Let the continuation work for some time
        Assert.assertTrue(executed[1]); // Ensure it is now done

        // And now let's append a promise to an already finished promise:
        Promise<?> p2 = p1.append(new Runnable() {
            public void run() {
                executed[2] = true;
            }
        });

        // It must have been immediately executed in our thread.
        Assert.assertTrue(executed[2]);
        Assert.assertTrue(p2.isDone());
    }
}
