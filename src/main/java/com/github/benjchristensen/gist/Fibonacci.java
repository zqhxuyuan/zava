package com.github.benjchristensen.gist;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;

public class Fibonacci {

    public static void main(String... args) {
        // via Observable.create
        fib.take(13).forEach(System.out::println);

        // via scan
        Observable.range(0, 10).scan(new HashMap<String, Integer>(), (m, n) -> {
            Integer f1 = m.get("f1");
            Integer f2 = m.get("f2");
            f1 = f1 == null ? 0 : f1;
            f2 = f2 == null ? 1 : f2;
            int fn = f1 + f2;
            m.put("f1", f2);
            m.put("f2", fn);
            return m;
        })
                .filter(m -> m.size() == 2)
                .map(m -> m.get("f1") + m.get("f2"))
                //.startWith(Observable.from(0, 1, 1))
                .forEach(System.out::println);
    }

    static Observable<Integer> fib = Observable.create((Subscriber<? super Integer> s) -> {
        int f1 = 0, f2 = 1, fn;
        s.onNext(0);
        s.onNext(1);
        while (!s.isUnsubscribed()) {
            fn = f1 + f2;
            f1 = f2;
            f2 = fn;
            s.onNext(fn);
        }
    });
}