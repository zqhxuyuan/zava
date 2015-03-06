package com.ctriposs.bigmap;

import java.io.Closeable;
import java.io.IOException;

public interface IBigConcurrentHashMap extends Closeable {
	
    /**
     * Removes all mappings from this hash map, leaving it empty.
     *
     * @see #isEmpty
     * @see #size
     */
	public void clear();
	
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
	public byte[] get(byte[] key);
	
    /**
     * Returns whether this map is empty.
     *
     * @return {@code true} if this map has no elements, {@code false}
     *         otherwise.
     * @see #size()
     */
	public boolean isEmpty();
	
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
	public byte[] put(byte[] key, byte[] value, long ttlInMs);
	
	
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
	public byte[] putIfAbsent(byte[] key, byte[] value, long ttlInMs);
	
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
	public byte[] remove(byte[] key);
	
    /**
     * Returns the number of elements in this map.
     *
     * @return the number of elements in this map.
     */
	public int size();
	
	/**
	 * remove all data in the map, including backing file.
	 * 
	 * @throws IOException exception throws during file IO operation.
	 */
	public void removeAll() throws IOException;
	
	/**
	 * Stats for memory mapped file
	 * 
	 * @return file stats
	 */
	public IMMFStats getMemoryMappedFileStats();
}
