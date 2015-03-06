package com.interview.basics.model.collection.hash;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/15/14
 * Time: 3:29 PM
 */
public interface Map<K, V> {
    public void put(K key, V value);
    public V get(K key);
    public boolean isEmpty();
    public int size();
    public Set<K> keySet();
    public V remove(K key);
}
