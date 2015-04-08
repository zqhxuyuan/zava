package com.github.jhusain.learnrxjava.grokking;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zqhxuyuan on 15-4-2.
 *
 * Ref: http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/
 */
public class HelloWorld {

    public static void main(String[] args) {
        //helloword();

        //simpleCode();

        oneShort();
    }

    public static void helloword(){
        /**
         * When the subscription is made,
         * myObservable calls the subscriber's onNext() and onComplete() methods.
         * As a result, mySubscriber outputs "Hello, world!" then terminates.
         */

        //create a basic Observable
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext("Hello, world!");
                        sub.onCompleted();
                    }
                }
        );

        //create a Subscriber to consume the data
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

        //hook them up to each other using subscribe()
        myObservable.subscribe(mySubscriber);
    }

    public static void simpleCode(){
        //emits a single item then completes
        Observable<String> myObservable = Observable.just("Hello, world!");

        //don't care about onCompleted() nor onError(), so instead we can
        //use a simpler class to define what to do during onNext()
        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        };

        //Actions can define each part of a Subscriber
        //we only need the first parameter, because we're ignoring onError() and onComplete()
        myObservable.subscribe(onNextAction);
    }

    public static void oneShort(){
        Observable.just("Hello, world!")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                    }
                });
    }

    public static void java8Style(){
        Observable.just("Hello, world!")
                .subscribe(s -> System.out.println(s));
    }

    public static void firstTransformation(){
        //control over your Observable
        Observable.just("Hello, world! -Dan")
                .subscribe(s -> System.out.println(s));

        //modifying our Subscriber instead
        Observable.just("Hello, world!")
                .subscribe(s -> System.out.println(s + " -Dan"));

        //Subscribers are supposed to be the thing that reacts, not the thing that mutates
    }

    //Operators can be used in between the source Observable and
    //the ultimate Subscriber to manipulate emitted items
    public static void introduceOperators(){
        //the map() operator can be used to transform one emitted item into another
        Observable.just("Hello, world!")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s + " -Dan";
                    }
                })
                .subscribe(s -> System.out.println(s));

        //lambdas
        Observable.just("Hello, world!")
                .map(s -> s + " -Dan")
                .subscribe(s -> System.out.println(s));

        //Our map() operator is basically an Observable that transforms an item.
        //We can chain as many map() calls as we want together,
        //polishing the data into a perfect, consumable form for our end Subscriber
    }

    public static void moreMapOperators(){
        //we started with a String but our Subscriber receives an Integer
        Observable.just("Hello, world!")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.hashCode();
                    }
                })
                .subscribe(i -> System.out.println(Integer.toString(i)));

        Observable.just("Hello, world!")
                .map(s -> s.hashCode())
                .subscribe(i -> System.out.println(Integer.toString(i)));

        //we want our Subscriber to do as little as possible
        Observable.just("Hello, world!")
                .map(s -> s.hashCode())
                .map(i -> Integer.toString(i))
                .subscribe(s -> System.out.println(s));

        //We just added some transformational steps in between
        Observable.just("Hello, world!")
                .map(s -> s + " -Dan")
                .map(s -> s.hashCode())
                .map(i -> Integer.toString(i))
                .subscribe(s -> System.out.println(s));
    }
}
