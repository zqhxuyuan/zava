package com.interview.basics.model.collection.hash;

import java.util.Iterator;

interface Entry<K, V> {
    public int hash();
    public K key();
    public V value();
    public Entry<K, V> next();
    public void setValue(V value);
    public void setNext(Entry<K, V> next);
}
public abstract class HashContainer<K, V> {

    protected float loadFactor;
    protected int capacity;
    protected int count;
    protected int threshold;

    protected transient Entry<K,V>[] table;

    protected abstract Entry<K, V>[] initCapacity(int capacity);
    protected abstract Entry<K, V> getEntry(int hash, K key, V value, Entry<K, V> next);

    @SuppressWarnings("unchecked")
    public HashContainer(int initialCapacity, float loadFactor) {
        if(initialCapacity < 0 || loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException(String.format("Illegal capacity or loadFactor [capacity=%s,loadFactor=%s]", initialCapacity, loadFactor));
        this.capacity = initialCapacity;
        this.loadFactor = loadFactor;
        table = initCapacity(this.capacity);
        this.threshold = (int)(this.capacity * this.loadFactor);
    }

    public HashContainer(){
        this(100, 0.75f);
    }

    public synchronized V get(K key) {
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % this.capacity;

        for(Entry<K,V> entry = this.table[index]; entry != null; entry = entry.next()){
            if(entry.hash() == hash && entry.key().equals(key)){
                return entry.value();
            }
        }
        return null;
    }

    public synchronized V put(K key, V value) {
        if (key == null || value == null)
            throw new IllegalArgumentException("Null is not allowed for key or value");
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % this.capacity;

        for(Entry<K,V> entry = this.table[index]; entry != null; entry = entry.next()){
            if(entry.hash() == hash && entry.key().equals(key)){
                V oldValue = entry.value();
                entry.setValue(value);
                return oldValue;
            }
        }

        if(count >= threshold){
            rehash();
            index = (hash & 0x7FFFFFFF) % this.capacity;
        }

        Entry<K,V> entry = this.table[index];
        table[index] = getEntry(hash, key, value, entry);
        this.count ++;
        return null;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        int newCapacity = this.capacity * 2 + 1;
        Entry<K,V>[] newTable = initCapacity(newCapacity);

        for(int i = this.capacity - 1; i >= 0 ; i --){
            for(Entry<K,V> entry = this.table[i]; entry != null; ) {
                Entry<K,V> nextEntry = entry.next();
                int index = (entry.hash() & 0x7FFFFFFF) % newCapacity;
                entry.setNext(newTable[index]);
                newTable[index] = entry;
                entry = nextEntry;
            }
        }

        this.capacity = newCapacity;
        this.table = newTable;
        this.threshold =  (int) (this.capacity * this.loadFactor);
    }

    public synchronized V remove(K key){
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % this.capacity;

        Entry<K,V> previous = null;

        for(Entry<K,V> entry = this.table[index]; entry != null; previous=entry, entry = entry.next()){
            if(entry.hash() == hash && entry.key().equals(key)){
                V value = entry.value();
                if(previous == null) {
                    this.table[index] = entry.next();
                } else {
                    previous.setNext(entry.next());
                }
                this.count--;
                entry = null;
                return value;
            }
        }
        return null;
    }

    public Iterator<Entry<K, V>> iterator(){
        return new Iterator<Entry<K, V>>() {
            int cursor = -1;
            int tableIndex = -1;
            Entry<K, V> entry = null;
            @Override
            public boolean hasNext() {
                return cursor + 1 < count;
            }

            @Override
            public Entry<K, V> next() {
                cursor++;
                if(entry!= null && entry.next() != null) {
                    entry = entry.next();
                    return entry;
                }
                while(++tableIndex < table.length && table[tableIndex] == null){};
                entry = table[tableIndex];
                return entry;
            }

            @Override
            public void remove() {
                if(entry.next() != null){
                    entry.setValue(entry.next().value());
                    entry.setNext(entry.next().next());
                } else {
                    entry = null;
                }
            }
        };
    }
}
