package com.github.benjchristensen.gist;

import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Multicasting a cold, infinite Observable and using onBackpressureBuffer/Drop to handle overflow
 *
 * This shows how a "reactive pull" compliant "cold" Observable, when multicasted, becomes "hot" and each Subscriber
 * must then choose its strategy for overflow.
 */
public class MulticastColdInfiniteBackpressureExample {

    public static void main(String[] args) {

        final CountDownLatch latch = new CountDownLatch(2);

        // multicast a "cold" source
        ConnectableObservable<Integer> source = getData(1).publish();

        /**
         * This buffers so will get the first 2000 of 5000 emitted
         */
        source.onBackpressureBuffer().observeOn(Schedulers.computation())
                .map(i -> "one => " + i).take(2000).finallyDo(() -> latch.countDown()).forEach(System.out::println);

        /**
         * This drops, so will receive with first 1024 (size of internal buffer) and then pick up in the stream again
         * when it can consume more and get large values like 159023.
         */
        source.onBackpressureDrop().observeOn(Schedulers.computation())
                .map(i -> "two => " + i).take(2000).finallyDo(() -> latch.countDown()).forEach(System.out::println);

        source.connect();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Not actually infinite, but large enough to behave such for this example.
     */
    public static Observable<Integer> getData(int id) {
        return Observable.range(id, Integer.MAX_VALUE).subscribeOn(Schedulers.computation());
    }
}