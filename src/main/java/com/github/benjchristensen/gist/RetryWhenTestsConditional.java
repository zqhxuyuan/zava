package com.github.benjchristensen.gist;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscriber;

public class RetryWhenTestsConditional {

    public static void main(String[] args) throws Exception{

        AtomicInteger count = new AtomicInteger();
        Observable.create((Subscriber<? super String> s) -> {
            if (count.getAndIncrement() == 0) {
                s.onError(new RuntimeException("always fails"));
            } else {
                s.onError(new IllegalArgumentException("user error"));
            }
        }).retryWhen(attempts -> {
            return attempts.flatMap(n -> {
                if (n.getCause() instanceof IllegalArgumentException) {
                    System.out.println("don't retry on IllegalArgumentException... allow failure");
                    return Observable.error(n.getCause());
                } else {
                    System.out.println(n.getCause() + " => retry after 1 second");
                    return Observable.timer(1, TimeUnit.SECONDS);
                }
            });
        })
                .toBlocking().forEach(System.out::println);
    }


    public void testComment1() throws Exception{
//        Observable
//                .defer(() -> bucket.get("id"))
//                .map(document -> {
//                    document.content().put("modified", new Date().getTime());
//                    return document;
//                })
//                .flatMap(bucket::replace)
//                .retryWhen(attempts ->
//                                attempts.flatMap(n -> {
//                                    if (!(n.getThrowable() instanceof CASMismatchException)) {
//                                        return Observable.error(n.getThrowable());
//                                    }
//                                    return Observable.timer(1, TimeUnit.SECONDS);
//                                })
//                )
//                .subscribe();
    }
}