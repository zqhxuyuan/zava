package com.github.benjchristensen.gist;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

public class RetryWhenTests {

    public static void main(String[] args) {

        Observable.create((Subscriber<? super String> s) -> {
            s.onError(new RuntimeException("always fails"));
        }).retryWhen(attempts -> {
            return attempts.zipWith(Observable.range(1, 3), (n, i) -> i).flatMap(i -> {
                System.out.println("delay retry by " + i + " second(s)");
                return Observable.timer(i, TimeUnit.SECONDS);
            });
        }).toBlocking().forEach(System.out::println);

    }
}