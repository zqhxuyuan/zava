package com.interview.design.questions;

import java.util.Iterator;

/**
 * Created_By: stefanie
 * Date: 14-12-7
 * Time: 下午5:23
 */
public class DZ11_CircularArray<T> implements Iterable<T>{
    private T[] items;
    private int head = 0;

    public DZ11_CircularArray(int size){
        items = (T[]) new Object[size];
    }

    private int convert(int index){
        int offset = (head + index) % items.length;
        return offset >= 0? offset : offset + items.length;
    }

    public void rotate(int shiftRight){
        head = convert(shiftRight);
    }

    public T get(int index){
        return items[convert(index)];
    }

    public void set(int index, T item){
        items[convert(index)] = item;
    }

    private class CircularArrayIterator<E> implements Iterator<E> {
        private int _current = -1;
        private E[] _items;

        private CircularArrayIterator(DZ11_CircularArray<E> array) {
            this._items = array.items;
        }

        @Override
        public boolean hasNext() {
            return _current < items.length - 1;
        }

        @Override
        public E next() {
            _current++;
            E item = _items[convert(_current)];
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can delete items during iteration");
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new CircularArrayIterator<T>(this);
    }


    public static void main(String[] args){
        int N = 10;
        DZ11_CircularArray<Integer> array = new DZ11_CircularArray<>(N);
        for(int i = 0; i < N; i++) array.set(i, i);
        System.out.println(array.get(-8));     //2

        array.rotate(4);
        for(int i = 0; i < N; i++) System.out.print(array.get(i) + ", ");
        System.out.println();

        for(Integer item : array){
            System.out.print(item + ", ");
        }
        System.out.println();
    }

}
