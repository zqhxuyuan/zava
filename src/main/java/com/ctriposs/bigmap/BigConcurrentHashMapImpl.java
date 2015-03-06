package com.ctriposs.bigmap;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigConcurrentHashMapImpl implements IBigConcurrentHashMap {
	
	private final static Logger logger = LoggerFactory.getLogger(BigConcurrentHashMapImpl.class);

    /*
     * The basic strategy is to subdivide the table among Segments,
     * each of which itself is a concurrently readable hash table.
     */

    /* ---------------- Constants -------------- */

    /**
     * The default initial capacity for this table,
     * used when not otherwise specified in a constructor.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The default load factor for this table, used when not
     * otherwise specified in a constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The default concurrency level for this table, used when not
     * otherwise specified in a constructor.
     */
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    
    /**
     * The default map entry expiration purge interval
     */
    static final int DEFAULT_PURGE_INTERVAL = 1000 * 60 * 20;
    
    /**
     * Should the on disk map be reloaded into memory on map initialization
     */
    static final boolean DEFAULT_RELOAD_ON_STARTUP = false;

    /**
     * The maximum capacity, used if a higher value is implicitly
     * specified by either of the constructors with arguments.  MUST
     * be a power of two <= 1<<30 to ensure that entries are indexable
     * using ints.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The maximum number of segments to allow; used to bound
     * constructor arguments.
     */
    static final int MAX_SEGMENTS = 1 << 16; // slightly conservative

    /* ---------------- Fields -------------- */

    /**
     * Mask value for indexing into segments. The upper bits of a
     * key's hash code are used to choose the segment.
     */
    final int segmentMask;

    /**
     * Shift value for indexing within segments.
     */
    final int segmentShift;

    /**
     * The segments, each of which is a specialized hash table
     */
    final Segment<byte[]>[] segments;

    
	/**
	 * Factory managing the creation, recycle/reuse of map entries.
	 */
	final IMapEntryFactory mapEntryFactory;
	
	/**
	 * map file directory
	 */
	final String mapDir;
	/**
	 * map name
	 */
	final String mapName;

	/**
	 * Expiration purge timer
	 */
	Timer purgeTimer;
	
	final BigConfig config;
	
	final AtomicLong purgeCount = new AtomicLong(0);
	
    /* ---------------- Small Utilities -------------- */
	

    /**
     * Returns the segment that should be used for key with given hash
     * @param hash the hash code for the key
     * @return the segment
     */
    final Segment<byte[]> segmentFor(int hash) {
        return segments[(hash >>> segmentShift) & segmentMask];
    }
    

    /* ---------------- Inner Classes -------------- */

    /**
     * BigConcurrentHashMap list entry. Note that this is never exported
     * out as a user-visible Map.Entry.
     *
     */
    static final class HashEntry {
    	volatile long index;
        final int hash;
        HashEntry next;

        HashEntry(long index, int hash, HashEntry next) {
            this.index = index;
            this.hash = hash;
            this.next = next;
        }

        static HashEntry[] newArray(int i) {
            return new HashEntry[i];
        }
    }
    
    /**
     * Segments are specialized versions of hash tables.  This
     * subclasses from ReentrantLock opportunistically, just to
     * simplify some locking and avoid separate construction.
     */
    static final class Segment<V> extends ReentrantLock implements Serializable {

        private static final long serialVersionUID = 2249069246763182397L;

        /**
         * The number of elements in this segment's region.
         */
        transient volatile int count;

        /**
         * The table is rehashed when its size exceeds this threshold.
         * (The value of this field is always <tt>(int)(capacity *
         * loadFactor)</tt>.)
         */
        transient int threshold;

        /**
         * The per-segment table.
         */
        transient volatile HashEntry[] table;

        /**
         * The load factor for the hash table.  Even though this value
         * is same for all segments, it is replicated to avoid needing
         * links to outer object.
         * @serial
         */
        final float loadFactor;
        
    	/**
    	 * Factory managing the creation, recycle/reuse of map entries mapped to disk files.
    	 */
    	final IMapEntryFactory mapEntryFactory;

        Segment(int initialCapacity, float lf, IMapEntryFactory mapEntryFactory) {
        	super(false);
            loadFactor = lf;
            this.mapEntryFactory = mapEntryFactory;
            setTable(HashEntry.newArray(initialCapacity));
        }

        @SuppressWarnings("unchecked")
        static <V> Segment<V>[] newArray(int i) {
            return new Segment[i];
        }
        
        /**
         * Sets table to new HashEntry array.
         * Call only while holding lock or in constructor.
         */
        void setTable(HashEntry[] newTable) {
            threshold = (int)(newTable.length * loadFactor);
            table = newTable;
        }

        /**
         * Returns properly casted first entry of bin for given hash.
         */
        HashEntry getFirst(int hash) {
            HashEntry[] tab = table;
            return tab[hash & (tab.length - 1)];
        }
        
        /* Specialized implementations of map methods */

        byte[] get(final byte[] key, int hash) throws IOException {
            if (count != 0) { // read-volatile
	        	lock();
	        	try {
	        		int c = count - 1;
	                HashEntry[] tab = table;
	                int index = hash & (tab.length - 1);
	                HashEntry e = tab[index];
	                while (e != null) {
	                	MapEntry me = this.mapEntryFactory.findMapEntryByIndex(e.index);
	                    if (e.hash == hash && Arrays.equals(key, me.getEntryKey())) {
	                    	
	                    	if (this.isExpired(me)) {
	                    		this.mapEntryFactory.release(me);
	                            
	                            this.removeEntry(tab, index, e);
	                            
	                            count = c; // write-volatile
	                    		
	                    		return null;
	                    	} else {
	                    		me.putLastAccessedTime(System.currentTimeMillis());
	                    	    return me.getEntryValue();
	                    	}
	                    }
	                    e = e.next;
	                }
	        	} finally {
	        		unlock();
	        	}
            }
            return null;
        }
        
        void removeEntry(HashEntry[] tab, int index, HashEntry e) {
            HashEntry first = tab[index];
            
            if (first == e) {
            	tab[index] = e.next;
            	e.next = null; // ready for GC
            } else {
            	HashEntry p = first;
            	while(p.next != e) {
            		p = p.next;
            	}
            	p.next = e.next;
            	e.next = null; // ready for GC
            }
        }
        
        boolean containsKey(final byte[] key, int hash) throws IOException {
            if (count != 0) { // read-volatile
	        	lock();
		        try {
		        	int c = count - 1;
	                HashEntry[] tab = table;
	                int index = hash & (tab.length - 1);
	                HashEntry e = tab[index];
	                while (e != null) {
	                	MapEntry me = this.mapEntryFactory.findMapEntryByIndex(e.index);
	                    if (e.hash == hash && Arrays.equals(key, me.getEntryKey())) {
	                    	
	                    	if (this.isExpired(me)) {
	                    		this.mapEntryFactory.release(me);
	                            
	                            this.removeEntry(tab, index, e);
	                            
	                            count = c; // write-volatile
	                    		
	                    		return false;
	                    	} else {
	                    		me.putLastAccessedTime(System.currentTimeMillis());
	                    	    return true;
	                    	}
	                    	
	                    }
	                    e = e.next;
	                }    
	        	} finally {
	        		unlock();
	        	}
            }
            return false;
        }
        
        boolean replace(byte[] key, int hash, byte[] oldValue, byte[] newValue, long ttlInMs) throws IOException {
            lock();
            try {
                HashEntry e = getFirst(hash);
                MapEntry me = null;
                while (e != null) {
                	me = this.mapEntryFactory.findMapEntryByIndex(e.index);
                	
                	if (e.hash == hash && Arrays.equals(key, me.getEntryKey())) {
                		break;
                	}
                	
                    e = e.next;
                }

                boolean replaced = false;
                if (e != null && Arrays.equals(oldValue, me.getEntryValue())) {
                    replaced = true;
                    this.mapEntryFactory.release(me);
                    me = this.mapEntryFactory.acquire(key.length + newValue.length);
                    me.putKeyLength(key.length);
                    me.putValueLength(newValue.length);
                    me.putEntryKey(key);
                    me.putEntryValue(newValue);
                    me.putLastAccessedTime(System.currentTimeMillis());
                    me.putTimeToLive(ttlInMs);
                    
                    e.index = me.getIndex();
                }
                return replaced;
            } finally {
                unlock();
            }
        }
        
        byte[] replace(byte[] key, int hash, byte[] newValue, long ttlInMs) throws IOException {
            lock();
            try {
                HashEntry e = getFirst(hash);
                MapEntry me = null;
                while (e != null) {
                	me = this.mapEntryFactory.findMapEntryByIndex(e.index);
                	
                	if (e.hash == hash && Arrays.equals(key, me.getEntryKey())) {
                		break;
                	}
                	
                    e = e.next;
                }

                byte[] oldValue = null;
                if (e != null) {
                    oldValue = me.getEntryValue();
                    this.mapEntryFactory.release(me);
                    me = this.mapEntryFactory.acquire(key.length + newValue.length);
                    me.putKeyLength(key.length);
                    me.putValueLength(newValue.length);
                    me.putEntryKey(key);
                    me.putEntryValue(newValue);
                    me.putLastAccessedTime(System.currentTimeMillis());
                    me.putTimeToLive(ttlInMs);
                    
                    e.index = me.getIndex();
                }
                return oldValue;
            } finally {
                unlock();
            }
        }
        
        
        void restoreInUseMapEntry(MapEntry me, int hash) throws IOException {
        	lock();
        	try {
        		int c = count;
        		if (c++ > threshold) // ensure capacity
        			rehash();
                HashEntry[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry first = tab[index];
                
                tab[index] = new HashEntry(me.getIndex(), hash, first);
                count = c; // write-volatile
        	} finally {
        		unlock();
        	}
        }

        byte[] put(byte[] key, int hash, byte[] value, boolean onlyIfAbsent, long ttlInMs) throws IOException {
            lock();
            try {
                int c = count;
                if (c++ > threshold) // ensure capacity
                    rehash();
                HashEntry[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry first = tab[index];
                HashEntry e = first;
                MapEntry me = null;
                while (e != null) {
                	me = this.mapEntryFactory.findMapEntryByIndex(e.index);
                	
                	if (e.hash == hash && Arrays.equals(key, me.getEntryKey())) {
                		break;
                	}
                	
                    e = e.next;
                }

                byte[] oldValue;
                if (e != null) {                    
                    oldValue = me.getEntryValue();
                    if (!onlyIfAbsent) {
                        this.mapEntryFactory.release(me);
                        me = this.mapEntryFactory.acquire(key.length + value.length);
                        me.putKeyLength(key.length);
                        me.putValueLength(value.length);
                        me.putEntryKey(key);
                        me.putEntryValue(value);
                        me.putLastAccessedTime(System.currentTimeMillis());
                        me.putTimeToLive(ttlInMs);
                        
                        e.index = me.getIndex();
                    }
                }
                else {
                    oldValue = null;
                    
                    me = this.mapEntryFactory.acquire(key.length + value.length);
                    me.putKeyLength(key.length);
                    me.putValueLength(value.length);
                    me.putEntryKey(key);
                    me.putEntryValue(value);
                    me.putLastAccessedTime(System.currentTimeMillis());
                    me.putTimeToLive(ttlInMs);
                    
                    tab[index] = new HashEntry(me.getIndex(), hash, first);
                    count = c; // write-volatile
                }
                return oldValue;
            } finally {
                unlock();
            }
        }

        
        
        void rehash() {
            HashEntry[] oldTable = table;
            int oldCapacity = oldTable.length;
            if (oldCapacity >= MAXIMUM_CAPACITY)
                return;

            HashEntry[] newTable = HashEntry.newArray(oldCapacity<<1);
            threshold = (int)(newTable.length * loadFactor);
            int sizeMask = newTable.length - 1;
            for (HashEntry e : oldTable) {
                if (e != null) {
                	HashEntry p = e;
                	HashEntry q = e.next;
                	
                	while(true) {
                		int k = p.hash & sizeMask;
                		p.next = newTable[k];
                		newTable[k] = p;
                		
                		p = q;
                		if (p == null) break;
                		else {
                			q = p.next;
                		}
                	}
                }
            }
            table = newTable;
        }
        
        // Purge expired entries
        void purge() throws IOException {
        	if (count != 0) {
	        	lock();
	        	try {
		        	for(int index = 0; index < table.length; index++) {
		        		HashEntry e = table[index];
		        		while(e != null) {
		        			MapEntry me = this.mapEntryFactory.findMapEntryByIndex(e.index);
		                	
		                	HashEntry next = e.next;
		                	if (this.isExpired(me)) {
		                		this.mapEntryFactory.release(me);
		                		
		                		this.removeEntry(table, index, e);
		                		
		                		count--;
		                	}
		        			
		        			e = next;
		        		}
		        	}
	        	} finally {
	        		unlock();
	        	}
        	}
        }
        
        
        /**
         * Remove; match on key only if value null, else match both.
         * @throws IOException 
         */
        byte[] remove(final byte[] key, int hash, byte[] value) throws IOException {
            lock();
            try {
                int c = count - 1;
                HashEntry[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry e = tab[index];
                MapEntry me = null;
                while (e != null) {
                	me = this.mapEntryFactory.findMapEntryByIndex(e.index);
                	
                	if (e.hash == hash && Arrays.equals(key, me.getEntryKey())) {
                		break;
                	}
                	
                    e = e.next;
                }

                byte[] oldValue = null;
                if (e != null) {
                    if (value == null || Arrays.equals(value, me.getEntryValue())) {
                        oldValue = me.getEntryValue();
                        
	                	if (this.isExpired(me)) {
	                		oldValue = null;
	                	}
                        
                        this.mapEntryFactory.release(me);
                        
                        this.removeEntry(tab, index, e);
                        
                        count = c; // write-volatile
                    }
                }
                return oldValue;
            } finally {
                unlock();
            }
        }
        
        boolean isExpired(MapEntry me) {
        	// has the entry expired?
        	long ttlInMs = me.getTimeToLive();
        	boolean expired = ttlInMs > 0 && (System.currentTimeMillis() - me.getLastAccessedTime() > ttlInMs);
        	return expired;
        }
        
        void clear() {
            if (count != 0) {
                lock();
                try {
                    HashEntry[] tab = table;
                    for (int i = 0; i < tab.length ; i++)
                        tab[i] = null;
                    count = 0; // write-volatile
                } finally {
                    unlock();
                }
            }
        }
        
    }

	
    /* ---------------- Public operations -------------- */

    /**
     * Creates a new, empty map with the specified map directory, map name, initial
     * capacity, load factor and concurrency level.
     *
     * @param mapDir the target directory to store persisted map files
     * @param mapName the name of the map
     * @param config the hash map configuration including load factor, initial capacity,
     * concurrency level and purgeInterval in milliseconds.
     * @throws IOException the exception throws if failed to operate on memory mapped files
     * @throws IllegalArgumentException if the initial capacity is
     * negative or the load factor or concurrencyLevel or purgeIntervalInMs are
     * nonpositive.
     */
	public BigConcurrentHashMapImpl(String mapDir, String mapName, BigConfig config) throws IOException {
        if (!(config.getLoadFactor() > 0) || config.getInitialCapacity() < 0 || 
        		config.getConcurrencyLevel() <= 0 || config.getPurgeIntervalInMs() <= 10)
            throw new IllegalArgumentException();

        this.mapDir = mapDir;
        this.mapName = mapName;
        this.config = config;
        this.mapEntryFactory = new MapEntryFactoryImpl(mapDir, mapName);

        // Find power-of-two sizes best matching arguments
        int sshift = 0;
        int ssize = 1;
        while (ssize < config.getConcurrencyLevel()) {
            ++sshift;
            ssize <<= 1;
        }
        segmentShift = 32 - sshift;
        segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);

        int c = config.getInitialCapacity() / ssize;
        if (c * ssize < config.getInitialCapacity())
            ++c;
        int cap = 1;
        while (cap < c)
            cap <<= 1;

        for (int i = 0; i < this.segments.length; ++i)
            this.segments[i] = new Segment<byte[]>(cap, config.getLoadFactor(), this.mapEntryFactory);
        
        // reload on disk map entries into memory
        if (!((MapEntryFactoryImpl)this.mapEntryFactory).isEmpty()) {
        	if (config.isReloadOnStartup()) {
        	    this.reload();
        	} else {
            	this.mapEntryFactory.removeAll();
        	}
        }
        
        this.startPurgeTimer();
	}
	
	private void stopPurgeTimer() {
		if (this.purgeTimer != null) {
			this.purgeTimer.cancel();
			this.purgeTimer = null;
		}
	}
	
	private void startPurgeTimer() {
        purgeTimer = new Timer(mapName + "_purgeTimer");
        purgeTimer.schedule(new PurgeTimerTask(), config.getPurgeIntervalInMs(), config.getPurgeIntervalInMs());
	}
	
	/**
	 * Load the on-disk map entries and rebuild the in-memory index
	 * 
	 * @throws IOException
	 */
	void reload() throws IOException {
		MapEntryFactoryImpl factory = (MapEntryFactoryImpl)this.mapEntryFactory;
		
		long index = 0;
		while(index >= 0) {
			MapEntry me = factory.findMapEntryByIndex(index);
			if (me.isAllocated()) {
				factory.restore(me);
				if (me.isInUse()) {
					this.restoreInUseMapEntry(me);
				}
				
			} else {
				break;
			}
			index++;
		}
	}
	
    /**
	* Creates a new, empty map with default initial capacity (16), load factor (0.75), 
	* concurrencyLevel (16) and purgeIntervalInMs(20 minutes).
	*
	* @param mapDir the target directory to store persisted map files
	* @param mapName the name of the map
	* @throws IOException the exception throws if failed to operate on memory mapped files
	*/
	public BigConcurrentHashMapImpl(String mapDir, String mapName) throws IOException {
		this(mapDir, mapName, new BigConfig());
	}
	
	class PurgeTimerTask extends TimerTask {
		AtomicBoolean running = new AtomicBoolean(false);
		
		@Override
		public void run() {
			try {
				if (running.compareAndSet(false, true)) {
					try {
						purge();
						purgeCount.incrementAndGet();
					} finally {
						running.set(false);
					}
				}
			} catch (Throwable t) {
				logger.error("Fail to purge expired map entries", t);
			}
		}
		
	}
	
	void purge() throws IOException {
		for(Segment<byte[]> segment : segments) {
			segment.purge();
		}
	}
	
    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
	@Override
	public boolean isEmpty() {
		for(Segment<byte[]> segment : segments) {
			if (segment.count > 0) return false;
		}
		return true;
	}
	
    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this map
     */
	@Override
	public int size() {
		long total = 0;
		for(Segment<byte[]> segement : segments) {
			total += segement.count;
		}
		return (int)Math.min(Integer.MAX_VALUE, total);
	}
	
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code keys} such that {@code key.equals(k)},
     * then this method returns {@code keys}; otherwise it returns
     * {@code null}.  (There can be at most one such mapping.)
     * @throws RuntimeException throws if file IO operation fail
     * @throws NullPointerException if the specified key is null
     */
	@Override
	public byte[] get(byte[] key) {
		if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
		final int hash = Arrays.hashCode(key);
		
		try {
			return segmentFor(hash).get(key, hash);
		} catch (IOException e) {
			throw new RuntimeException("Fail to get key in the map", e);
		}
	}
	
    /**
     * Tests if the specified object is a key in this table.
     *
     * @param  key   possible key
     * @return <tt>true</tt> if and only if the specified object
     *         is a key in this table, as determined by the
     *         <tt>equals</tt> method; <tt>false</tt> otherwise.
     * @throws RuntimeException throws if file IO operation fail
     * @throws NullPointerException if the specified key is null
     */
	public boolean containsKey(byte[] key) {
		if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
		final int hash = Arrays.hashCode(key);
		try {
			return segmentFor(hash).containsKey(key, hash);
		} catch (IOException e) {
			throw new RuntimeException("Fail to run ontainsKey operation on the map", e);
		}
	}
	
    /**
     * Maps the specified key to the specified value in this table for the specified duration.
     * If the map previously contained a mapping for the key, the old value is
     * replaced by the specified value.
     * Neither the key nor the value can be null.
     *
     * <p> The value can be retrieved by calling the <tt>get</tt> method
     * with a key that is equal to the original key.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param ttlInMs the time in ms during which the entry can stay in the map (time-to-live). When
     * this time has elapsed, the entry will be evicted from the map automatically. A value of 0 for
     * this argument means "forever", i.e. <tt>put(key, value, 0)</tt> is equivalent to
     * <tt>put(key, value).
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws RuntimeException throws if file IO operation fail
     * @throws NullPointerException if the specified key or value is null
     */
	@Override
	public byte[] put(byte[] key, byte[] value, long ttlInMs) {
		if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
		if (value == null || value.length == 0) throw new NullPointerException("value is null or empty");
		if (ttlInMs < 0) throw new IllegalArgumentException("Invalid time to live value " + ttlInMs + ", it must be >= 0.");
		final int hash = Arrays.hashCode(key);
		
		try {
			return segmentFor(hash).put(key, hash, value, false, ttlInMs);
		} catch (IOException e) {
			throw new RuntimeException("Fail to put key/value in the map", e);
		}
	}
	
	void restoreInUseMapEntry(MapEntry me) {
		try {
			byte[] key = me.getEntryKey();
			if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
			if (me.getEntryValue() == null || me.getEntryValue().length == 0) throw new NullPointerException("value is null or empty");
			if (me.getTimeToLive() < 0) throw new IllegalArgumentException("Invalid time to live value " + me.getTimeToLive() + ", it must be >= 0.");
		
			final int hash = Arrays.hashCode(key);
			
			segmentFor(hash).restoreInUseMapEntry(me, hash);
			
		} catch (IOException e) {
			throw new RuntimeException("Fail to restore/put key/value in the map", e);
		}
	}
	
    /**
     * Maps the specified key to the specified value in this table.
     * If the map previously contained a mapping for the key, the old value is
     * replaced by the specified value.
     * Neither the key nor the value can be null.
     *
     * <p> The value can be retrieved by calling the <tt>get</tt> method
     * with a key that is equal to the original key.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws RuntimeException throws if file IO operation fail
     * @throws NullPointerException if the specified key or value is null
     */
	public byte[] put(byte[] key, byte[] value) {
		return this.put(key, value, 0L);
	}
	
    /**
     * Maps the specified key to the specified value in this table for the specified duration only if the key is absent.
     * Neither the key nor the value can be null.
     *
     * <p> The value can be retrieved by calling the <tt>get</tt> method
     * with a key that is equal to the original key.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param ttlInMs the time in ms during which the entry can stay in the map (time-to-live). When
     * this time has elapsed, the entry will be evicted from the map automatically. A value of 0 for
     * this argument means "forever", i.e. <tt>putIfAbsent(key, value, 0)</tt> is equivalent to
     * <tt>putIfAbsent(key, value).
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws RuntimeException throws if file IO operation fail
     * @throws NullPointerException if the specified key or value is null
     */
	@Override
	public byte[] putIfAbsent(byte[] key, byte[] value, long ttlInMs) {
		if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
		if (value == null || value.length == 0) throw new NullPointerException("value is null or empty");
		if (ttlInMs < 0) throw new IllegalArgumentException("Invalid time to live value " + ttlInMs + ", it must be >= 0.");
		final int hash = Arrays.hashCode(key);
		
		try {
			return segmentFor(hash).put(key, hash, value, true, ttlInMs);
		} catch (IOException e) {
			throw new RuntimeException("Fail to putIfAbsent key/value in the map", e);
		}
	}
	
    /**
     * Maps the specified key to the specified value in this table only if the key is absent.
     * Neither the key nor the value can be null.
     *
     * <p> The value can be retrieved by calling the <tt>get</tt> method
     * with a key that is equal to the original key.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws RuntimeException throws if file IO operation fail
     * @throws NullPointerException if the specified key or value is null
     */
	public byte[] putIfAbsent(byte[] key, byte[] value) {
		return this.putIfAbsent(key, value, 0L);
	}
	
    /**
     * Removes the key (and its corresponding value) from this map.
     * This method does nothing if the key is not in the map.
     *
     * @param  key the key that needs to be removed
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws RuntimeException throws if file IO operation fail
     * @throws NullPointerException if the specified key is null
     */
	@Override
	public byte[] remove(byte[] key) {
		if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
		final int hash = Arrays.hashCode(key);
		
		try {
			return segmentFor(hash).remove(key, hash, null);
		} catch (IOException e) {
			throw new RuntimeException("Fail to remove key in the map", e);
		}
	}
	
    /**
     * Removes the key (and its corresponding value) from this map.
     * This method does nothing if the key is not in the map.
     *
     * @param  key the key that needs to be removed
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws RuntimeException throws if file IO operation fail
     * @throws NullPointerException if the specified key is null
     */
	public boolean remove(byte[] key, byte[] value) {
		if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
		if (value == null || value.length == 0) throw new NullPointerException("value is null or empty");
		final int hash = Arrays.hashCode(key);
		
		try {
			return segmentFor(hash).remove(key, hash, value) != null;
		} catch (IOException e) {
			throw new RuntimeException("Fail to remove key/value in the map", e);
		}
	}
	
	public boolean replace(byte[] key, byte[] oldValue, byte[] newValue) {
		return this.replace(key, oldValue, newValue, 0L);
	}
	
    /**
     * 
     *
     * @throws NullPointerException if any of the arguments are null
     */
	public boolean replace(byte[] key, byte[] oldValue, byte[] newValue, long ttlInMs) {
		if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
		if (oldValue == null || oldValue.length == 0) throw new NullPointerException("oldValue is null or empty");
		if (newValue == null || newValue.length == 0) throw new NullPointerException("newValue is null or empty");
		if (ttlInMs < 0) throw new IllegalArgumentException("Invalid time to live value " + ttlInMs + ", it must be >= 0.");
		final int hash = Arrays.hashCode(key);
		
		try {
			return segmentFor(hash).replace(key, hash, oldValue, newValue, ttlInMs);
		} catch (IOException e) {
			throw new RuntimeException("Fail to replace key/value in the map", e);
		}
	}
	
	public byte[] replace(byte[] key, byte[] value) {
		return this.replace(key, value, 0L);
	}
	
    /**
     * 
     *
     * @throws NullPointerException if any of the arguments are null
     */
	public byte[] replace(byte[] key, byte[] value, long ttlInMs) {
		if (key == null || key.length == 0) throw new NullPointerException("key is null or empty");
		if (value == null || value.length == 0) throw new NullPointerException("oldValue is null or empty");
		if (ttlInMs < 0) throw new IllegalArgumentException("Invalid time to live value " + ttlInMs + ", it must be >= 0.");
		final int hash = Arrays.hashCode(key);
		
		try {
			return segmentFor(hash).replace(key, hash, value, ttlInMs);
		} catch (IOException e) {
			throw new RuntimeException("Fail to replace key/value in the map", e);
		}
	}
	
    /**
     * Removes all of the mappings from this map.
     */
	@Override
	public void clear() {
        for (Segment<byte[]> segment : segments) segment.clear();
    }

	@Override
	public void close() throws IOException {
		this.clear();
		this.stopPurgeTimer();
		this.mapEntryFactory.close();
	}

	@Override
	public void removeAll() throws IOException {
		this.clear();
		this.mapEntryFactory.removeAll();
	}

	@Override
	public IMMFStats getMemoryMappedFileStats() {
		return this.mapEntryFactory;
	}
}
