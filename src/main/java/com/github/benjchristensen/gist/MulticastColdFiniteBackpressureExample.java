package com.github.benjchristensen.gist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Multicasting a cold, finite Observable and using onBackpressureBuffer/Drop to handle overflow
 *
 * This shows how a "reactive pull" compliant "cold" Observable, when multicasted, becomes "hot" and each Subscriber
 * must then choose its strategy for overflow.
 */
public class MulticastColdFiniteBackpressureExample {

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
         * This drops, so will receive with first 1024 (size of internal buffer) but miss the rest
         * because the source will finish emitting 5000 and emit onComplete before it can start again.
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
     * This is a simple example of an Observable Iterable using "reactive pull".
     */
    public static Observable<Integer> getData(int id) {
        // simulate a finite, cold data source
        final ArrayList<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < 5000; i++) {
            data.add(i + id);
        }
        return fromIterable(data).subscribeOn(Schedulers.computation());
    }

    /**
     * A more performant but more complicated implementation can be seen at:
     * https://github.com/Netflix/RxJava/blob/master/rxjava-core/src/main/java/rx/internal/operators/OnSubscribeFromIterable.java
     * <p>
     * Real code should just use Observable.from(Iterable iter) instead of re-implementing this logic.
     * <p>
     * This is being shown as a simplified version to demonstrate how "reactive pull" data sources are implemented.
     */
    public static Observable<Integer> fromIterable(Iterable<Integer> it) {
        // return as Observable (real code would likely do IO of some kind)
        return Observable.create((Subscriber<? super Integer> s) -> {
            final Iterator<Integer> iter = it.iterator();
            final AtomicLong requested = new AtomicLong();
            s.setProducer((long request) -> {
                /*
                 * We add the request but only kick off work if at 0.
                 *
                 * This is done because over async boundaries `request(n)` can be called multiple times by
                 * another thread while this `Producer` is still emitting. We only want one thread ever emitting.
                 */
                if (requested.getAndAdd(request) == 0) {
                    do {
                        if (s.isUnsubscribed()) {
                            return;
                        }
                        if (iter.hasNext()) {
                            s.onNext(iter.next());
                        } else {
                            s.onCompleted();
                        }
                    } while (requested.decrementAndGet() > 0);
                }
            });
        });
    }
}