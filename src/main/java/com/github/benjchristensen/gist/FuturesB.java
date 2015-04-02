package com.github.benjchristensen.gist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * https://gist.github.com/benjchristensen/4671081
 */
public class FuturesB {

    /**
     * Demonstrate how futures can easily become blocking and prevent other work
     * from being performed asynchronously.
     */
    public static void run() throws Exception {
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        try {
            // get f3 with dependent result from f1
            Future<String> f1 = executor.submit(new CallToRemoteServiceA());
            Future<String> f3 = executor.submit(new CallToRemoteServiceC(f1.get()));

            /* The work below can not proceed until f1.get() completes even though there is no dependency */

            // also get f4/f5 after dependency f2 completes
            Future<Integer> f2 = executor.submit(new CallToRemoteServiceB());
            Future<Integer> f4 = executor.submit(new CallToRemoteServiceD(f2.get()));
            Future<Integer> f5 = executor.submit(new CallToRemoteServiceE(f2.get()));

            System.out.println(f3.get() + " => " + (f4.get() * f5.get()));
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Demonstrates how reordering of Future.get() can improve the situation
     * but that it still doesn't address differing response latencies of f1 and f2.
     */
    public static void run2() throws Exception {
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        try {
            // kick of f1/f2 in parallel
            Future<String> f1 = executor.submit(new CallToRemoteServiceA());
            Future<Integer> f2 = executor.submit(new CallToRemoteServiceB());

            // get f3 with dependent result from f1 (blocks)
            Future<String> f3 = executor.submit(new CallToRemoteServiceC(f1.get()));

            /* The work below can not proceed until f1.get() completes even if f2.get() is done. */

            // get f4/f5 after dependency f2 completes (blocks)
            Future<Integer> f4 = executor.submit(new CallToRemoteServiceD(f2.get()));
            Future<Integer> f5 = executor.submit(new CallToRemoteServiceE(f2.get()));

            System.out.println(f3.get() + " => " + (f4.get() * f5.get()));
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Demonstrate how changing where threads are injected can solve the issue of run2()
     * at the cost of incidental complexity being added to the code.
     * <p>
     * This same example could be accomplished by refactoring CallToRemoteServiceC
     * to accept a Future<String> instead of String but the principle is the same.
     */
    public static void run3() throws Exception {
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        try {
            // kick of f1/f2 in parallel
            final Future<String> f1 = executor.submit(new CallToRemoteServiceA());
            Future<Integer> f2 = executor.submit(new CallToRemoteServiceB());

            // spawn in another thread so waiting on f1 for f3 doesn't block f4/f5
            Future<String> f3 = executor.submit(new Callable<String>() {

                @Override
                public String call() throws Exception {
                    // get f3 with dependent result from f1 (blocks)
                    return new CallToRemoteServiceC(f1.get()).call();
                }

            });

            /* The following can now proceed as soon as f2.get() completes even if f1.get() isn't done yet */

            // get f4/f5 after dependency f2 completes (blocks)
            Future<Integer> f4 = executor.submit(new CallToRemoteServiceD(f2.get()));
            Future<Integer> f5 = executor.submit(new CallToRemoteServiceE(f2.get()));

            System.out.println(f3.get() + " => " + (f4.get() * f5.get()));
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Demonstrate typical handling of responding to Futures as they complete.
     * <p>
     * This successfully executes multiple calls in parallel but then synchronously handles
     * each response in the order they were put in the list rather than the order they complete.
     */
    public static void run4() throws Exception {
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        try {
            List<Future<?>> futures = new ArrayList<Future<?>>();

            // kick off several async tasks
            futures.add(executor.submit(new CallToRemoteServiceA()));
            futures.add(executor.submit(new CallToRemoteServiceB()));
            futures.add(executor.submit(new CallToRemoteServiceC("A")));
            futures.add(executor.submit(new CallToRemoteServiceC("B")));
            futures.add(executor.submit(new CallToRemoteServiceC("C")));
            futures.add(executor.submit(new CallToRemoteServiceD(1)));
            futures.add(executor.submit(new CallToRemoteServiceE(2)));
            futures.add(executor.submit(new CallToRemoteServiceE(3)));
            futures.add(executor.submit(new CallToRemoteServiceE(4)));
            futures.add(executor.submit(new CallToRemoteServiceE(5)));

            // as each completes do further work

            for (Future<?> f : futures) {
                /* this blocks so even if other futures in the list complete earlier they will wait until this one is done */
                doMoreWork(f.get());
            }
        } finally {
            executor.shutdownNow();
        }
    }

    private static void doMoreWork(Object s) {
        // do work
        System.out.println("do more work => " + s);
    }

    /**
     * Demonstrate polling approach to handling Futures as they complete.
     * <p>
     * This becomes unwieldy and error prone quickly.
     */
    public static void run5() throws Exception {
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        try {
            List<Future<?>> futures = new ArrayList<Future<?>>();

            // kick off several async tasks
            futures.add(executor.submit(new CallToRemoteServiceA()));
            futures.add(executor.submit(new CallToRemoteServiceB()));
            futures.add(executor.submit(new CallToRemoteServiceC("A")));
            futures.add(executor.submit(new CallToRemoteServiceC("B")));
            futures.add(executor.submit(new CallToRemoteServiceC("C")));
            futures.add(executor.submit(new CallToRemoteServiceD(1)));
            futures.add(executor.submit(new CallToRemoteServiceE(2)));
            futures.add(executor.submit(new CallToRemoteServiceE(3)));
            futures.add(executor.submit(new CallToRemoteServiceE(4)));
            futures.add(executor.submit(new CallToRemoteServiceE(5)));

            // as each completes do further work

            // keep polling until all work is done
            while (futures.size() > 0) {
                // use an iterator so we can remove from it
                Iterator<Future<?>> i = futures.iterator();
                while (i.hasNext()) {
                    Future<?> f = i.next();
                    if (f.isDone()) {
                        // only do work if the Future is done
                        doMoreWork(f.get());
                        i.remove();
                    }
                    // otherwise we continue to the next Future
                }
            }
        } finally {
            executor.shutdownNow();
        }
    }

    public static void main(String args[]) {
        try {
            long start = System.currentTimeMillis();
            run();
            System.out.println("Finished in: " + (System.currentTimeMillis() - start) + "ms");

            run2();
            run3();
            run4();
            run5();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final class CallToRemoteServiceA implements Callable<String> {
        @Override
        public String call() throws Exception {
            // simulate fetching data from remote service
            Thread.sleep(100);
            return "responseA";
        }
    }

    private static final class CallToRemoteServiceB implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            // simulate fetching data from remote service
            Thread.sleep(40);
            return 100;
        }
    }

    private static final class CallToRemoteServiceC implements Callable<String> {

        private final String dependencyFromA;

        public CallToRemoteServiceC(String dependencyFromA) {
            this.dependencyFromA = dependencyFromA;
        }

        @Override
        public String call() throws Exception {
            // simulate fetching data from remote service
            Thread.sleep(60);
            return "responseB_" + dependencyFromA;
        }
    }

    private static final class CallToRemoteServiceD implements Callable<Integer> {

        private final Integer dependencyFromB;

        public CallToRemoteServiceD(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Integer call() throws Exception {
            // simulate fetching data from remote service
            Thread.sleep(140);
            return 40 + dependencyFromB;
        }
    }

    private static final class CallToRemoteServiceE implements Callable<Integer> {

        private final Integer dependencyFromB;

        public CallToRemoteServiceE(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Integer call() throws Exception {
            // simulate fetching data from remote service
            Thread.sleep(55);
            return 5000 + dependencyFromB;
        }
    }
}