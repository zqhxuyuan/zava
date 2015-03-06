package com.interview.basics.model.collection.heap;

import com.interview.basics.model.collection.list.ArrayList;
import com.interview.basics.model.collection.list.List;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/1/14
 * Time: 1:26 PM
 */
public class BinaryArrayHeap<T extends Comparable<T>> implements Heap<T>{
    int type;
    protected List<T> store = new ArrayList<T>();

    public BinaryArrayHeap() {
        this.type = MAX_HEAD;
    }

    public BinaryArrayHeap(int type) {
        this.type = type;
    }

    public void add(T element){
        store.add(element);
        swim(store.size() - 1);
    }

    public T getHead(){
        return store.get(0);
    }

    public T pollHead(){
        T temp = store.get(0);
        store.set(0, store.get(store.size()-1));
        store.remove(store.size() - 1);
        sink(0);
        return temp;
    }

//    public boolean contains(T element){
//        return store.contains(element);
//    }

    public int size(){
        return store.size();
    }

    private void sink(int k) {
        //index start from 0, so the K -> 2K+1, 2K+2
        while(2*k + 1 < store.size()){
            int j = 2*k + 1;
            if(type == MAX_HEAD){
                if( j + 1 < store.size() && store.get(j).compareTo(store.get(j + 1)) < 0) j++;
                if(store.get(k).compareTo(store.get(j)) > 0) break;
            } else {
                if( j + 1 < store.size() && store.get(j).compareTo(store.get(j + 1)) > 0) j++;
                if(store.get(k).compareTo(store.get(j)) < 0) break;
            }
            swap(j, k);
            k = j;
        }
    }

    private void swim(int k){
        while(true){
            int p = (k-1)/2;
            if(type == MAX_HEAD){
                if(k <= 0 || p < 0 || store.get(p).compareTo(store.get(k)) >= 0) break;
            } else {
                if(k <= 0 || p < 0 || store.get(p).compareTo(store.get(k)) <= 0) break;
            }
            swap(p, k);
            k = p;
        }
    }

    private void swap(int i, int j){
        T temp = store.get(i);
        store.set(i, store.get(j));
        store.set(j, temp);
    }

    public boolean contains(T k){
        return contains(0, k);
    }

    private boolean contains(int i, T k){
        if(store.get(i).equals(k)) return true;
        if(type == MAX_HEAD){
            if(store.get(i).compareTo(k) < 0) return false;
            if( 2 * i + 1 < store.size() && contains(2 * i + 1, k)) return true;
            if( 2 * i + 2 < store.size() && contains(2 * i + 2, k)) return true;
            return false;
        } else {
            if(store.get(i).compareTo(k) > 0) return false;
            if( 2 * i + 1 < store.size() && contains(2 * i + 1, k)) return true;
            if( 2 * i + 2 < store.size() && contains(2 * i + 2, k)) return true;
            return false;
        }
    }
}
