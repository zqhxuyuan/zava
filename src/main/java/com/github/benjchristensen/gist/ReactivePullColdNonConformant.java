package com.github.benjchristensen.gist;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Handling an Observable Iterable without Reactive Pull using onBackpressureBuffer
 *
 * This demonstrates a "cold" Observable that does not use "reactive pull" and how to handle it.
 */
public class ReactivePullColdNonConformant {

    public static void main(String[] args) {
        /*
         * The Observable does not support "reactive pull" so will fail with MissingBackpressureException.
         *
         * The `onBackpressureBuffer()` and `onBackpressureDrop()` operators are used to define strategies to deal with
         * source Observables that don't support backpressure.
         */
        getData(1).onBackpressureBuffer().observeOn(Schedulers.computation()).toBlocking().forEach(System.out::println);
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
        return fromIterableWithoutReactivePull(data);
    }


    /**
     * This does not implement "reactive pull" so will "firehose" all data at the Subscriber.
     */
    public static Observable<Integer> fromIterableWithoutReactivePull(Iterable<Integer> it) {
        return Observable.create((Subscriber<? super Integer> s) -> {
            for(Integer i : it) {
                if(s.isUnsubscribed()) {
                    return;
                }
                s.onNext(i);
            }
            s.onCompleted();
        });
    }
}