package com.github.benjchristensen.gist;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Simple pass-thru event bus with parallel processing of events and error handling.
 */
public class EventBusWithParallelProcessing {

    public static void main(String[] args) {
        MyEventBus bus = new MyEventBus();

        // perform IO in parallel for each event
        bus.toObservable().flatMap(o -> {
            return Observable.just(o).map(v -> {
                // simulate latent network call
                try { Thread.sleep(400); } catch (Exception e) {}
                return "IO-Response_" + v;
            }).subscribeOn(Schedulers.io());
        }).forEach(result -> System.out.println("IO => " + result));

        // perform computation in parallel for each event
        bus.toObservable().flatMap(o -> {
            return Observable.just(o).map(v -> {
                // simulate expensive computation
                try { Thread.sleep(200); } catch (Exception e) {}
                return "Computed_" + v;
            }).subscribeOn(Schedulers.computation());
        }).forEach(result -> System.out.println("Computation => " + result));

        // perform work for each event that sometimes results in errors
        bus.toObservable().flatMap(o -> {
            return Observable.just(o).map(v -> {
                if(v.equals("hello")) {
                    throw new RuntimeException("Simulated error processing -> " + v);
                }
                return "Processed_" + v;
            }).onErrorResumeNext(Observable.just("DefaultValueFor_" + o)).subscribeOn(Schedulers.computation());
        }).forEach(result -> System.out.println("Processed => " + result));

        bus.send(1);
        bus.send(11);
        bus.send(28);
        bus.send("hello");
        bus.send(5);
        bus.send("world");

        // Since we're doing work asynchronously above we need to wait on it
        // (There are other more "idiomatic" ways of doing this without sleeping
        // but they require changing to ReplaySubject or using CountdownLatches. I
        // chose to stay simple and obvious for this and leave the rest of the code
        // as intended for the example).
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    public static boolean IS_NUMBER(Object o) {
        if (o instanceof Number) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean IS_STRING(Object o) {
        if (o instanceof String) {
            return true;
        } else {
            return false;
        }
    }

    public static class MyEventBus {
        private final PublishSubject<Object> bus = PublishSubject.create();
        /**
         * If multiple threads are going to emit events to this then it must be made thread-safe like this instead:
         */
        //        private final Subject<Object, Object> bus = new SerializedSubject<Object, Object>(PublishSubject.create());

        public void send(Object o) {
            bus.onNext(o);
        }

        public Observable<Object> toObservable() {
            return bus;
        }
    }
}