package com.github.jhusain.learnrxjava.grokking;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqhxuyuan on 15-4-2.
 */
public class SetupImpl implements Setup {

    static List<Record> list = new ArrayList<>();
    static Map<String, List<String>> map = new HashMap<>();

    static class Record{
        String name;
        String title;
        List<String> tags;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    static {
        Record record = new Record();
        record.setName("abc");
        record.setTags(Lists.newArrayList("new","sport","movie"));
        list.add(record);

        record = new Record();
        record.setName("cde");
        record.setTags(Lists.newArrayList("new", "music"));
        list.add(record);

        record = new Record();
        record.setName("def");
        record.setTags(Lists.newArrayList("music","sport","movie"));
        list.add(record);

        for(Record r : list){
            map.put(r.getName(), r.getTags());
        }
    }

    @Override
    public Observable<List<String>> query(String name) {
        return Observable.just(map.get(name));
    }
    public static Observable<List<String>> query2(String name){
        return  new SetupImpl().query(name);
    }

    public static String key = "abc";

    public static void main(String[] args) {
        flatMap();
    }

    public static void firstQuery(){
        //query的返回值是Observable<T>, 可以在Observable上调用subscribe方法
        //subscribe方法处理的每个元素都是T的值: 即这里都是List<String>
        //这样subscribe就可以循环List<String>处理每个url了
        query2(key)
                .subscribe(urls -> {
                    for (String url : urls) {
                        System.out.println(url);
                    }
                });

        //This answer is highly unsatisfactory because I lose the ability to transform the data stream.
        //If I wanted to modify each URL, I'd have to do it all in the Subscriber.
        //We're tossing all our cool map() tricks out the window!

        query2(key)
                .subscribe(urls -> {
                    //Observable.from(), that takes a collection of items and emits each them one at a time
                    //上面使用for each循环. 这里使用from, 每次会取出urls集合中的一项, 发送给subscriber
                    Observable.from(urls)
                            //got multiple, nested subscriptions now! WTF! 这里又有一个subscribe!
                            .subscribe(url -> System.out.println(url));
                });
    }


    public static void flatMap() {
        query2(key)
                .flatMap(
                    new Func1<List<String>, Observable<String>>() {
                        //the new Observable returned is what the Subscriber sees
                        //It doesn't receive a List<String> - it gets a series of
                        //individual Strings as returned by Observable.from()
                        //参数是List<T>, 通过from,返回的是一个新的Observable, 它获得了一系列的单独的T,发送给subscribe使用
                        @Override
                        public Observable<String> call(List<String> urls) {
                            return Observable.from(urls);
                        }
                    }
                )
                .subscribe(url -> System.out.println(url));

        System.out.println("----------------------------");

        query2(key)
                .flatMap(urls -> Observable.from(urls))
                .subscribe(url -> System.out.println(url));

        System.out.println("----------------------------");

        query2(key)
                .flatMap(urls -> Observable.from(urls))
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        return getTitle(url);
                    }
                })
                .subscribe(title -> System.out.println(title));

        System.out.println("----------------------------");

        //composing multiple independent methods returning Observables together
        query2(key)
                .flatMap(urls -> Observable.from(urls))
                .flatMap(url -> getTitle(url))
                .subscribe(title -> System.out.println(title));
    }

    private static Observable<String> getTitle(String url) {
        return Observable.just("title:"+url);
    }

    public static void filter(){
        query2(key)
//                .flatMap(urls -> Observable.from(urls))
//                .flatMap(url -> getTitle(url))
                //filter() emits the same item it received, but only if it passes the boolean check
                .filter(title -> title != null)
                //only show 5 results at most
                .take(5)
                //save each title to disk along the way
                //doOnNext() allows us to add extra behavior each time an item is emitted
                .doOnNext(title -> saveTitle(title))
                .subscribe(title -> System.out.println(title));
    }

    private static <R> void saveTitle(R title) {

    }
}
