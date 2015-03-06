package com.interview.design.questions;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午5:15
 *
 * Design a data structure to achieve operation insert, delete, search and random access all in O(1)
 *  Array can insert, random access in O(1), could use to store element
 *  HashMap can insert, delete, search in O(1), could use as index <value, offset>
 *
 * logic for delete
 *  get the offset of the value, and put the last element to that offset.
 *  remember to update the index hashmap
 */
public class DZ15_MagicArray<T extends Comparable<T>> {
    private T[] store;
    private HashMap<T, Integer> index;
    private int size;

    public DZ15_MagicArray(int capacity) {
        this.store = (T[]) new Comparable[capacity];
        index = new HashMap<>();
    }

    public void add(T value) {
        if (size >= store.length) throw new ArrayIndexOutOfBoundsException("Array is full");
        else {
            store[size] = value;
            index.put(value, size);
            size++;
        }
    }

    public void delete(T value) {
        if (index.containsKey(value)) {
            int offset = index.get(value);
            index.remove(value);
            size--;
            if (size > 0) {
                T backup = store[size];
                store[offset] = backup;
                index.put(backup, offset);
            }
        }
    }

    public boolean contains(T value) {
        return index.containsKey(value);
    }

    public T get(int offset) {
        if (offset < 0 || offset >= store.length) throw new ArrayIndexOutOfBoundsException();
        if (offset < size) return store[offset];
        else return null;
    }

    public int size() {
        return size;
    }

    public void print() {
        for (int i = 0; i < this.size(); i++) {
            System.out.print(this.get(i) + ", ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        DZ15_MagicArray<Integer> array = new DZ15_MagicArray(10);
        for (int i = 0; i < 10; i++) {
            array.add(i);
        }
        array.print();
        try {
            array.add(11);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        System.out.println(array.contains(4));
        System.out.println(array.contains(7));
        System.out.println(array.contains(-1));
        System.out.println(array.contains(11));
        array.delete(10);
        array.print();
        System.out.println(array.contains(6));
        array.delete(6);
        System.out.println(array.contains(6));
        array.print();
    }
}
