package com.zqh.cache;

/**
 * Created by zqhxuyuan on 15-3-23.
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;

/**
 * 利用LinkedHashMap实现简单的缓存， 必须实现removeEldestEntry方法，具体参见JDK文档
 *
 * LRU算法是通过双向链表来实现，当某个位置被命中，通过调整链表的指向将该位置调整到头位置，新加入的内容直接放在链表头，
 * 如此一来，最近被命中的内容就向链表头移动，需要替换时，链表最后的位置就是最近最少使用的位置。
 *
 * LRU算法还可以通过计数来实现，缓存存储的位置附带一个计数器，当命中时将计数器加1，替换时就查找计数最小的位置并替换，结合访问时间戳来实现。
 * 这种算法比较适合缓存数据量较小的场景，显然，遍历查找计数最小位置的时间复杂度为O(n)。
 * 我实现了一个，结合了访问时间戳，当最小计数大于MINI_ACESS时(这个参数的调整对命中率有较大影响），就移除最久没有被访问的项：
 * @author dennis http://www.blogjava.net/killme2008/archive/2007/09/29/149645.html
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    /**
     * @uml.property  name="maxCapacity"
     */
    private final int maxCapacity;

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * @uml.property  name="lock"
     */
    private final Lock lock = new ReentrantLock();

    public LRULinkedHashMap(int maxCapacity) {
        super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
    @Override
    public boolean containsKey(Object key) {
        try {
            lock.lock();
            return super.containsKey(key);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public V get(Object key) {
        try {
            lock.lock();
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            lock.lock();
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        try {
            lock.lock();
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        try {
            lock.lock();
            super.clear();
        } finally {
            lock.unlock();
        }
    }

    public Collection<Map.Entry<K, V>> getAll() {
        try {
            lock.lock();
            return new ArrayList<Map.Entry<K, V>>(super.entrySet());
        } finally {
            lock.unlock();
        }
    }
}