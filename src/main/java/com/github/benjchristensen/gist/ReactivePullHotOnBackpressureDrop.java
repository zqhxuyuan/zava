package com.github.benjchristensen.gist;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Handling a hot Observable producing faster than the Subscriber with onBackpressureDrop
 *
 * This demonstrates how to use onBackpressureDrop when a hot stream doesn't itself handle "reactive pull"
 */
public class ReactivePullHotOnBackpressureDrop {

    public static void main(String[] args) {
        hotStream().onBackpressureDrop() // without this it will receive a MissingBackpressureException
                .observeOn(Schedulers.computation())
                .map(ReactivePullHotOnBackpressureDrop::doExpensiveWork)
                .toBlocking().forEach(System.out::println);
    }

    /**
     * Simulate a "slow consumer" doing expensive work, slowing that the producer is emitting.
     */
    public static int doExpensiveWork(int i) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // do nothing
        }
        return i;
    }

    /**
     * This is an artificial source to demonstrate an infinite stream that emits randomly
     */
    /**
     * This is an artificial source to demonstrate an infinite stream that emits randomly
     */
    public static Observable<Integer> hotStream() {
        return Observable.create((Subscriber<? super Integer> s) -> {
            int i = 0;
            while (!s.isUnsubscribed()) {
                s.onNext(i++);
                try {
                    // sleep for a random amount of time
                    // NOTE: Only using Thread.sleep here as an artificial demo.
                    Thread.sleep((long) (Math.random() * 10));
                } catch (Exception e) {
                    // do nothing
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }
}