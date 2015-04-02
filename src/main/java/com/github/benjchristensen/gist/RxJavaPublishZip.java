package com.github.benjchristensen.gist;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Example of using Observable.publish, observeOn and zip demonstrating backpressure
 */
public class RxJavaPublishZip {

    public static void main(String[] args) throws Exception{
        final AtomicInteger numEmitted = new AtomicInteger();

        Observable<String> strings = Observable.range(1, 1000000).doOnNext(i -> numEmitted.incrementAndGet())
                .publish(oi -> {
                    // schedule it so we are async and need backpressure
                    Observable<String> odd = oi.observeOn(Schedulers.computation())
                            .filter(i -> i % 2 != 0).map(i -> i + "-odd").map(s -> {
                                // make odd slow
                                try {
                                    Thread.sleep(1);
                                } catch (Exception e1) {
                                }
                                return s;
                            });
                    Observable<String> even = oi.observeOn(Schedulers.computation())
                            .filter(i -> i % 2 == 0).map(i -> i + "-even");
                    return Observable.zip(odd, even, (o, e) -> o + " " + e + "   Thread: " + Thread.currentThread());
                }).take(2000);

        strings.toBlocking().forEach(System.out::println);
        System.out.println("Number emitted from source (should be ~4000): " + numEmitted.get());
    }
}