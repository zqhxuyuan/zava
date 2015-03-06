package com.interview.basics.model.collection.hash;


import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/15/14
 * Time: 3:14 PM
 */
class SetEntry<K> implements Entry<K, K>{
    int hash;
    K item;
    Entry<K, K> next;

    SetEntry(int hash, K item, Entry<K, K> next) {
        this.hash = hash;
        this.item = item;
        this.next = next;
    }

    @Override
    public int hash() {
        return hash;
    }

    @Override
    public K key() {
        return item;
    }

    @Override
    public K value() {
        return item;
    }

    @Override
    public Entry<K, K> next() {
        return next;
    }

    @Override
    public void setValue(K value) {
        item = value;
    }

    @Override
    public void setNext(Entry<K, K> next) {
        this.next = next;
    }
}

public class HashSet<T> implements Set<T> {
    HashContainer<T, T> container = new HashContainer<T, T>() {
        @Override
        protected Entry<T, T>[] initCapacity(int capacity) {
            return (Entry<T, T>[]) new SetEntry[capacity];
        }

        @Override
        protected Entry<T, T> getEntry(int hash, T key, T value, Entry<T, T> next) {
            return new SetEntry(hash, key, next);
        }
    };

    @Override
    public void add(T element) {
        container.put(element, element);
    }

    @Override
    public boolean contains(T element) {
        return container.get(element) != null;
    }

    @Override
    public T remove(T element) {
        return container.remove(element);
    }

    @Override
    public int size() {
        return container.count;
    }

    @Override
    public boolean isEmpty() {
        return container.count == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Iterator<Entry<T, T>> itr = container.iterator();
            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public T next() {
                return itr.next().value();
            }

            @Override
            public void remove() {
                itr.remove();
            }
        };
    }
}
