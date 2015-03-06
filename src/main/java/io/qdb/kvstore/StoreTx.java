package io.qdb.kvstore;

import java.io.Serializable;

/**
 * A change to a data store. {@link KeyValueStoreSerializer}'s must be able to serialize and de-serialize
 * these.
 */
public class StoreTx<K, V> implements Serializable {

    public enum Operation { NOP, PUT, REMOVE, PUT_IF_ABSENT, REMOVE_KV, REPLACE, REPLACE_KVV }

    public String map;
    public Operation op;
    public K key;
    public V value;
    public V oldValue;

    public StoreTx() { }

    public StoreTx(String map, Operation op, K key) {
        this(map, op, key, null, null);
    }

    public StoreTx(String map, Operation op, K key, V value) {
        this(map, op, key, value, null);
    }

    public StoreTx(String map, Operation op, K key, V value, V oldValue) {
        this.map = map;
        this.op = op;
        this.key = key;
        this.value = value;
        this.oldValue = oldValue;
    }

    @Override
    public String toString() {
        return map + " " + op + " k=" + key + (value == null ? "" : " v=" + value) +
                (oldValue == null ? "" : " ov=" + oldValue);
    }

}
