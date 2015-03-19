package com.github.stakafum.mapreduce;

import java.util.*;

/**
 *
 * @author Saito Takafumi
 *
 * Suffle処理後にバリューをまとめるためのクラス
 * @param <V> Reduceフェーズの入力となるバリューのクラス
 */
public class GroupedValues<V> implements Iterable<V> , Iterator<V> {
    ArrayList<V> gValues;
    int index;

    GroupedValues(){
        this.gValues = new ArrayList<V>();
        this.index = 0;
    }

    GroupedValues(V value){
        this.gValues = new ArrayList<V>();
        this.gValues.add(value);
        this.index = 0;
    }

    void add(V v){
        gValues.add(v);
        index++;
    }

    public int getSize(){
        return this.gValues.size();
    }

    boolean hasValue(){
        return index <= 0 ? false : true;
    }

    V get(){
        V v = gValues.get(index);
        index--;
        return v;
    }

    @Override
    public Iterator<V> iterator() {
        return gValues.iterator();
    }

    @Override
    public boolean hasNext() {
        return index > 0;
    }

    @Override
    public V next() {
        V v = gValues.get(index);
        index--;
        return v;
    }

    @Override
    public void remove() {
        this.gValues.remove(this.index);
        this.index--;
    }
}