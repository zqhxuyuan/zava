package io.qdb.kvstore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helps create a DataStore instance. This makes it possible for the data store to receive all its configuration
 * in the constructor without breaking clients when new parameters are added.
 */
public class KeyValueStoreBuilder<K, V> {

    private File dir;
    private KeyValueStoreSerializer serializer;
    private KeyValueStore.VersionProvider<V> versionProvider;
    private KeyValueStore.Listener<K, V> listener;
    private int txLogSizeM = 10;
    private int maxObjectSize = 100000;
    private int snapshotCount = 3;
    private int snapshotIntervalSecs = 60;
    private Map<String, Class> aliases = new HashMap<String, Class>();

    public KeyValueStoreBuilder() { }

    public KeyValueStore<K, V> create() throws IOException {
        if (dir == null) throw new IllegalStateException("dir is required");
        if (serializer == null) serializer = new GensonSerializer(aliases);
        if (versionProvider == null) versionProvider = new NopVersionProvider<V>();
        return new KeyValueStoreImpl<K, V>(serializer, versionProvider, listener, dir,
                txLogSizeM, maxObjectSize, snapshotCount, snapshotIntervalSecs);
    }

    /**
     * Directory for snapshots and the transaction log. Created if it does not exist. Must be writable.
     */
    public KeyValueStoreBuilder dir(File dir) {
        this.dir = dir;
        return this;
    }

    /**
     * Directory for snapshots and the transaction log. Created if it does not exist. Must be writable.
     */
    public KeyValueStoreBuilder dir(String dir) {
        this.dir = new File(dir);
        return this;
    }

    /**
     * The serializer is responsible for converting objects to/from byte streams. The default serializer stores
     * data as JSON.
     * @see #alias(String, Class)
     */
    public KeyValueStoreBuilder serializer(KeyValueStoreSerializer serializer) {
        this.serializer = serializer;
        return this;
    }

    /**
     * The default serializer includes the fully qualified name of each class in the JSON as a "@class" attribute.
     * Use this method to change what is stored for your classes e.g. addAlias(Foo.class, "foo") will store Foo
     * instances as "@class": "foo" instead of "@class": "com.acme.Foo". Note that if you supply your own serializer
     * aliases are ignored.
     */
    public KeyValueStoreBuilder alias(String alias, Class<?> forClass) {
        aliases.put(alias, forClass);
        return this;
    }

    /**
     * If you want to use optimistic locking then you must supply a version provider which is responsible for
     * getting and incrementing version numbers.
     */
    public KeyValueStoreBuilder versionProvider(KeyValueStore.VersionProvider<V> versionProvider) {
        this.versionProvider = versionProvider;
        return this;
    }

    /**
     * If you want to be notified when the store is changed then supply a listener.
     */
    public KeyValueStoreBuilder listener(KeyValueStore.Listener<K, V> listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Set the max size in M of the transaction log. Default is 10M.
     */
    public KeyValueStoreBuilder txLogSizeM(int txLogSizeM) {
        this.txLogSizeM = txLogSizeM;
        return this;
    }

    /**
     * Set the maximum size in bytes of stored objects. Default is 100000.
     */
    public KeyValueStoreBuilder maxObjectSize(int maxObjectSize) {
        this.maxObjectSize = maxObjectSize;
        return this;
    }

    /**
     * How many snapshot files should be kept? Default is 3.
     */
    public KeyValueStoreBuilder snapshotCount(int snapshotCount) {
        this.snapshotCount = snapshotCount;
        return this;
    }

    /**
     * How often should automatic snapshots be taken? Default is every 60 seconds if changes have been made to the
     * store. Snapshots are taken as quickly as possible if the transaction log is more than half full.
     */
    public KeyValueStoreBuilder snapshotIntervalSecs(int snapshotIntervalSecs) {
        this.snapshotIntervalSecs = snapshotIntervalSecs;
        return this;
    }

    private static class NopVersionProvider<V> implements KeyValueStore.VersionProvider<V> {
        public Object getVersion(V value) { return null; }
        public void incVersion(V value) { }
    }
}
