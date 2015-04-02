package com.github.benjchristensen.gist;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Simple pass-thru event bus with error handling and reconnect.
 */
public class EventBus {

    public static void main(String[] args) {
        MyEventBus bus = new MyEventBus();

        bus.toObservable().filter(EventBus::IS_NUMBER).forEach(n -> System.out.println("Got number: " + n));
        bus.toObservable().filter(EventBus::IS_STRING).forEach(System.out::println);

        // something that can fail (it assumes Integer)
        bus.toObservable().map(o -> {
            if (((Integer) o) > 10) {
                return "Greater than 10";
            } else {
                return "Less than or equal to 10";
            }
        }).doOnError(e -> System.err.println(e.getMessage()))
                .retry() // reconnects to bus if an error occurs
                .forEach(System.out::println);

        bus.send(1);
        System.out.println("-----------------------");
        bus.send(11);
        System.out.println("-----------------------");
        bus.send(28);
        System.out.println("-----------------------");
        bus.send("hello");
        System.out.println("-----------------------");
        bus.send(5);
        System.out.println("-----------------------");
        bus.send("world");
        System.out.println("-----------------------");

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