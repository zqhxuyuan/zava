package com.github.jhusain.learnrxjava.grokking;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by zqhxuyuan on 15-4-7.
 */
public class Part3 {

    public static void main(String[] args) {

    }

    public static void errorHandling() throws Exception{
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) { System.out.println(s); }

            @Override
            public void onCompleted() {
                System.out.println("complete!");
            }

            @Override
            public void onError(Throwable e) { }
        };

        Observable.just("Hello, world!")
                //.map(s -> potentialException(s))
                //.map(s -> anotherPotentialException(s))
                .subscribe(mySubscriber);
    }
}
