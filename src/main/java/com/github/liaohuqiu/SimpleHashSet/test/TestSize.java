package com.github.liaohuqiu.SimpleHashSet.test;

import com.github.liaohuqiu.SimpleHashSet.ObjectSizeFetcher;

import java.io.IOException;

public class TestSize {

    public static void main(String[] args) throws IOException {
        TestObject1 testObject1 = new TestObject1();
        TestObject2 testObject2 = new TestObject2();

        System.out.printf("size of object with int: %s\n", ObjectSizeFetcher.getObjectSize(testObject1));
        System.out.printf("size of object with 2 int: %s\n", ObjectSizeFetcher.getObjectSize(testObject2));
        System.out.printf("size of HashMapEntry: %s\n", ObjectSizeFetcher.getObjectSize(new HashMapEntry<String, String>("", "", 0, null)));
        System.out.printf("size of SimpleHashMapEntry: %s\n", ObjectSizeFetcher.getObjectSize(new SimpleHashSetEntry<String>(0, null)));
        System.out.println("wait");
        System.in.read();
    }

    static class HashMapEntry<K, V> {
        final K key;
        final int hash;
        V value;
        HashMapEntry<K, V> next;

        HashMapEntry(K key, V value, int hash, HashMapEntry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }
    }

    static class SimpleHashSetEntry<T> {

        private int mHash;
        private T mKey;
        private SimpleHashSetEntry<T> mNext;

        private SimpleHashSetEntry(int hash, T key) {
            mHash = hash;
            mKey = key;
        }
    }

    private static class TestObject1 {
        private int mInt1;
    }

    private static class TestObject2 {
        private int mInt1;
        private int mInt2;
    }
}