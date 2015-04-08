package com.github.jhusain.learnrxjava.grokking;

import rx.Observable;

import java.util.List;

/**
 * Created by zqhxuyuan on 15-4-2.
 */
public interface Setup {

    // Returns a List of website URLs based on a text search
    Observable<List<String>> query(String text);
}
