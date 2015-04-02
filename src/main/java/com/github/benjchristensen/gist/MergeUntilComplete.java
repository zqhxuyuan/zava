package com.github.benjchristensen.gist;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class MergeUntilComplete {

    public static void main(String[] args) {

        Observable<String> t1 = Observable.timer(0, 100, TimeUnit.MILLISECONDS).map(i -> "A-" + i);
        Observable<String> t2 = Observable.timer(0, 300, TimeUnit.MILLISECONDS).take(5).map(i -> "B-" + i);

        Observable.merge(t1.materialize(), t2.materialize()).takeWhile(n -> n.isOnNext())
                .dematerialize().toBlocking().forEach(System.out::println);
    }
}