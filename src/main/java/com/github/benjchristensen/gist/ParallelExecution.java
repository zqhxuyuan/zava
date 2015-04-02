package com.github.benjchristensen.gist;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * https://gist.github.com/benjchristensen/a0350776a595fd6e3810
 */
public class ParallelExecution {

    public static void main(String[] args) {
        //        System.out.println("------------ mergingAsync");
        //        mergingAsync();
        //        System.out.println("------------ mergingSync");
        //        mergingSync();
        //        System.out.println("------------ mergingSyncMadeAsync");
        //        mergingSyncMadeAsync();
        //        System.out.println("------------ flatMapExampleSync");
        //        flatMapExampleSync();
        //        System.out.println("------------ flatMapExampleAsync");
        //        flatMapExampleAsync();
        System.out.println("------------ flatMapBufferedExampleAsync");
        flatMapBufferedExampleAsync();
        //        System.out.println("------------ flatMapWindowedExampleAsync");
        //        flatMapWindowedExampleAsync();
        //        System.out.println("------------");
    }

    private static void mergingAsync() {
        Observable.merge(getDataAsync(1), getDataAsync(2))
                .toBlocking().forEach(System.out::println);
    }

    /**
     * Merging async Observables subscribes to all of them concurrently.
     */
    private static void mergingSync() {
        // here you'll see the delay as each is executed synchronously
        Observable.merge(getDataSync(1), getDataSync(2))
                .toBlocking().forEach(System.out::println);
    }

    /**
     * If the Observables are synchronous they can be made async with `subscribeOn`
     */
    private static void mergingSyncMadeAsync() {
        // if you have something synchronous and want to make it async, you can schedule it like this
        // so here we see both executed concurrently
        Observable.merge(
                getDataSync(1).subscribeOn(Schedulers.io()),
                getDataSync(2).subscribeOn(Schedulers.io())
        )
                .toBlocking().forEach(System.out::println);
    }

    /**
     * flatMap uses `merge` so any async Observables it returns will execute concurrently.
     */
    private static void flatMapExampleAsync() {
        Observable.range(0, 5).flatMap(i -> {
            return getDataAsync(i);
        }).toBlocking().forEach(System.out::println);
    }

    /**
     * If synchronous Observables are merged (via flatMap here) then it will behave like `concat`
     * and execute each Observable (getDataSync here) synchronously one after the other.
     */
    private static void flatMapExampleSync() {
        Observable.range(0, 5).flatMap(i -> {
            return getDataSync(i);
        }).toBlocking().forEach(System.out::println);
    }

    /**
     * If a single stream needs to be split across multiple CPUs it is generally more efficient to do it in batches.
     *
     * The `buffer` operator can be used to batch into chunks that are then each processed on a separate thread.
     */
    private static void flatMapBufferedExampleAsync() {
        final AtomicInteger total = new AtomicInteger();
        Observable.range(0, 500000000)
                .doOnNext(i -> total.incrementAndGet())
                .buffer(100)
                .doOnNext(i -> System.out.println("emit " + i))
                .flatMap(i -> {
                    return Observable.from(i).subscribeOn(Schedulers.computation()).map(item -> {
                        // simulate computational work
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }
                        return item + " processed " + Thread.currentThread();
                    });
                }, Runtime.getRuntime().availableProcessors()).toBlocking().forEach(System.out::println);

        System.out.println("total emitted: " + total.get());
    }

    /**
     * Or the `window` operator can be used instead of buffer to process them as a stream instead of buffered list.
     */
    private static void flatMapWindowedExampleAsync() {
        Observable.range(0, 5000).window(500).flatMap(work -> {
            return work.observeOn(Schedulers.computation()).map(item -> {
                // simulate computational work
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
                return item + " processed " + Thread.currentThread();
            });
        }, Runtime.getRuntime().availableProcessors()).toBlocking().forEach(System.out::println);
    }

    // artificial representations of IO work
    static Observable<Integer> getDataAsync(int i) {
        return getDataSync(i).subscribeOn(Schedulers.io());
    }

    static Observable<Integer> getDataSync(int i) {
        return Observable.create((Subscriber<? super Integer> s) -> {
            // simulate latency
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            s.onNext(i);
            s.onCompleted();
        });
    }
}