package com.zqh.cache;

/**
 * Created by zqhxuyuan on 15-3-23.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author dennis 类说明：当缓存数目不多时，才用缓存计数的传统LRU算法
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> implements Serializable {

    private static final int DEFAULT_CAPACITY = 100;

    /**
     * @uml.property  name="map"
     * @uml.associationEnd  inverse="this$0:com.xuyuan.cache.algorithm.LRUCache$ValueEntry" qualifier="key:java.lang.Object com.xuyuan.cache.algorithm.LRUCache$ValueEntry"
     */
    protected Map<K, ValueEntry> map;

    /**
     * @uml.property  name="lock"
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * @uml.property  name="readLock"
     */
    private final Lock readLock = lock.readLock();

    /**
     * @uml.property  name="writeLock"
     */
    private final Lock writeLock = lock.writeLock();

    /**
     * @uml.property  name="maxCapacity"
     */
    private volatile int maxCapacity;  //保持可见性

    public static int MINI_ACCESS = 5;

    public LRUCache() {
        this(DEFAULT_CAPACITY);
    }

    public LRUCache(int capacity) {
        if (capacity <= 0)
            throw new RuntimeException("缓存容量不得小于0");
        this.maxCapacity = capacity;
        this.map = new HashMap<K, ValueEntry>(maxCapacity);
    }

    public boolean ContainsKey(K key) {
        try {
            readLock.lock();
            return this.map.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    public V put(K key, V value) {
        try {
            writeLock.lock();
            if ((map.size() > maxCapacity - 1) && !map.containsKey(key)) {
                // System.out.println("开始");
                Set<Map.Entry<K, ValueEntry>> entries = this.map.entrySet();
                removeRencentlyLeastAccess(entries);
            }
            ValueEntry new_value = new ValueEntry(value);
            ValueEntry old_value = map.put(key, new_value);
            if (old_value != null) {
                new_value.count = old_value.count;
                return old_value.value;
            } else
                return null;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 移除最近最少访问
     */
    protected void removeRencentlyLeastAccess(
            Set<Map.Entry<K, ValueEntry>> entries) {
        // 最小使用次数
        long least = 0;
        // 访问时间最早
        long earliest = 0;
        K toBeRemovedByCount = null;
        K toBeRemovedByTime = null;
        Iterator<Map.Entry<K, ValueEntry>> it = entries.iterator();
        if (it.hasNext()) {
            Map.Entry<K, ValueEntry> valueEntry = it.next();
            least = valueEntry.getValue().count.get();
            toBeRemovedByCount = valueEntry.getKey();
            earliest = valueEntry.getValue().lastAccess.get();
            toBeRemovedByTime = valueEntry.getKey();
        }
        while (it.hasNext()) {
            Map.Entry<K, ValueEntry> valueEntry = it.next();
            if (valueEntry.getValue().count.get() < least) {
                least = valueEntry.getValue().count.get();
                toBeRemovedByCount = valueEntry.getKey();
            }
            if (valueEntry.getValue().lastAccess.get() < earliest) {
                earliest = valueEntry.getValue().count.get();
                toBeRemovedByTime = valueEntry.getKey();
            }
        }
        // System.out.println("remove:" + toBeRemoved);
        // 如果最少使用次数大于MINI_ACCESS，那么移除访问时间最早的项(也就是最久没有被访问的项）
        if (least > MINI_ACCESS) {
            map.remove(toBeRemovedByTime);
        } else {
            map.remove(toBeRemovedByCount);
        }
    }

    public V get(K key) {
        try {
            readLock.lock();
            V value = null;
            ValueEntry valueEntry = map.get(key);
            if (valueEntry != null) {
                // 更新访问时间戳
                valueEntry.updateLastAccess();
                // 更新访问次数
                valueEntry.count.incrementAndGet();
                value = valueEntry.value;
            }
            return value;
        } finally {
            readLock.unlock();
        }
    }

    public void clear() {
        try {
            writeLock.lock();
            map.clear();
        } finally {
            writeLock.unlock();
        }
    }

    public int size() {
        try {
            readLock.lock();
            return map.size();
        } finally {
            readLock.unlock();
        }
    }

    public long getCount(K key) {
        try {
            readLock.lock();
            ValueEntry valueEntry = map.get(key);
            if (valueEntry != null) {
                return valueEntry.count.get();
            }
            return 0;
        } finally {
            readLock.unlock();
        }
    }

    public Collection<Map.Entry<K, V>> getAll() {
        try {
            readLock.lock();
            Set<K> keys = map.keySet();
            Map<K, V> tmp = new HashMap<K, V>();
            for (K key : keys) {
                tmp.put(key, map.get(key).value);
            }
            return new ArrayList<Map.Entry<K, V>>(tmp.entrySet());
        } finally {
            readLock.unlock();
        }
    }

    class ValueEntry implements Serializable {
        private V value;

        private AtomicLong count;

        private AtomicLong lastAccess;

        public ValueEntry(V value) {
            this.value = value;
            this.count = new AtomicLong(0);
            lastAccess = new AtomicLong(System.nanoTime());
        }

        public void updateLastAccess() {
            this.lastAccess.set(System.nanoTime());
        }

    }
}