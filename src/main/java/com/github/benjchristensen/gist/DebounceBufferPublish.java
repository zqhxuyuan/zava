package com.github.benjchristensen.gist;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Variant of above that uses `publish(Func1<? super Observable<T>, ? extends Observable<R>> selector)`
 * which allows multicasting without the need to use `refcount()` which can result in race conditions.
 * */
public class DebounceBufferPublish {

    public static void main(String args[]) {
        /* The following will emit a buffered list as it is debounced */
        Observable<List<Integer>> buffered = intermittentBursts().take(20).publish(stream -> {
            // inside the `publish` function we can access `stream` in a multicasted manner
            return stream.buffer(stream.debounce(10, TimeUnit.MILLISECONDS));
        });

        buffered.toBlocking().forEach(System.out::println);
    }

    /**
     * This is an artificial source to demonstrate an infinite stream that bursts intermittently
     */
    public static Observable<Integer> intermittentBursts() {
        return Observable.create((Subscriber<? super Integer> s) -> {
            while (!s.isUnsubscribed()) {
                // burst some number of items
                for (int i = 0; i < Math.random() * 20; i++) {
                    s.onNext(i);
                }
                try {
                    // sleep for a random amount of time
                    // NOTE: Only using Thread.sleep here as an artificial demo.
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (Exception e) {
                    // do nothing
                }
            }
        }).subscribeOn(Schedulers.newThread()); // use newThread since we are using sleep to block
    }

}