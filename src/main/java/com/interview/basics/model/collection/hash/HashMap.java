package com.interview.basics.model.collection.hash;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/15/14
 * Time: 4:04 PM
 */
class MapEntry<K, V> implements Entry<K, V>{

    int hash;
    K key;
    V value;
    Entry<K, V> next;

    MapEntry(int hash, K key, V value, Entry<K, V> next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }

    @Override
    public int hash() {
        return hash;
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public Entry<K, V> next() {
        return next;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public void setNext(Entry<K, V> next) {
        this.next = next;
    }
}
public class HashMap<K, V> implements Map<K, V> {
    HashContainer<K, V> container = new HashContainer<K, V>() {
        @Override
        protected Entry<K, V>[] initCapacity(int capacity) {
            return (Entry<K, V>[]) new MapEntry[capacity];
        }

        @Override
        protected Entry<K, V> getEntry(int hash, K key, V value, Entry<K, V> next) {
            return new MapEntry(hash, key, value, next);
        }
    };
    @Override
    public void put(K key, V value) {
        container.put(key, value);
    }

    @Override
    public V get(K key) {
        return container.get(key);
    }

    @Override
    public boolean isEmpty() {
        return container.count == 0;
    }

    @Override
    public int size() {
        return container.count;
    }

    @Override
    public Set<K> keySet(){
        Set<K> keys = new HashSet<K>();
        for(int i = 0; i < container.table.length; i++){
            Entry<K, V> entry = container.table[i];
            while(entry != null){
                keys.add(entry.key());
                entry = entry.next();
            }
        }
        return keys;
    }



    @Override
    public V remove(K key) {
        return container.remove(key);
    }

    public Iterator<Entry<K, V>> iterator(){
        return container.iterator();
    }
}
