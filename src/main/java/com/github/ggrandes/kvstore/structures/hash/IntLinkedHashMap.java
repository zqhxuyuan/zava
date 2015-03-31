/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.github.ggrandes.kvstore.structures.hash;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.ggrandes.kvstore.utils.GenericFactory;
import com.github.ggrandes.kvstore.utils.PrimeFinder;
import org.apache.log4j.Logger;

/**
 * Native Integer LinkedHashMap
 * This class is NOT Thread-Safe
 * @param <V> type of values
 *
 * @author Guillermo Grandes / guillermo.grandes[at]gmail.com
 */
public class IntLinkedHashMap<V> implements Iterable<V> {
	private static final Logger log = Logger.getLogger(IntLinkedHashMap.class);

	private int elementCount;
	private IntLinkedEntry<V>[] elementData;

	private final float loadFactor;
	private int threshold;
	private int defaultSize = 17;

	private GenericFactory<V> factory;

	/**
	 * The head of the doubly linked list. 双向链表的头指针
	 */
	private transient IntLinkedEntry<V> header;

	/**
	 * The iteration ordering method for this linked hash map: <tt>true</tt>
	 * for access-order, <tt>false</tt> for insertion-order.
     *
     * 访问顺序: true表示按照访问的顺序, false表示插入的顺序
	 */
	private final boolean accessOrder;

	/**
	 * Constructs a new {@code IntLinkedHashMap} instance with the specified capacity.
	 *
	 * @param capacity the initial capacity of this hash map.
	 * @throws IllegalArgumentException when the capacity is less than zero.
	 */
	public IntLinkedHashMap(final Class<V> type) {
		this(17, type, false);
	}
	public IntLinkedHashMap(final int capacity, final Class<V> type) {
		this(capacity, type, false);
	}
	public IntLinkedHashMap(final int capacity, final Class<V> type, final boolean accessOrder) {
		this.accessOrder = accessOrder;
		//
		factory = new GenericFactory<V>(type);
		defaultSize = primeSize(capacity);
		if (capacity >= 0) {
			elementCount = 0;
			elementData = newElementArray(defaultSize);
			loadFactor = 0.75f; // Default load factor of 0.75
			initCache(elementData.length);
			computeMaxSize();
		} else {
			throw new IllegalArgumentException();
		}
		// Initializes the chain. 初始化链表
		initChain();
	}

	@SuppressWarnings("unchecked")
	private IntLinkedEntry<V>[] newElementArray(int s) {
		return new IntLinkedEntry[s];
	}

	/**
	 * Removes all mappings from this hash map, leaving it empty.
	 *
	 * @see #isEmpty
	 * @see #size
	 */
	public void clear() {
		clear(true);
	}

	public void clear(final boolean shrink) {
		clearCache();
		if (elementCount > 0) {            
			elementCount = 0;            
		}
		if (shrink && (elementData.length > 1024) && (elementData.length > defaultSize)) {
			elementData = newElementArray(defaultSize);
		}
		else {
			Arrays.fill(elementData, null);
		}
		computeMaxSize();
		initChain();
	}

	private void initChain() {
		// Initializes the chain.
        //双向链表的头指针不存储条目. 初始化时, 头指针,它的before,after都指向自己
		header = new IntLinkedEntry<V>(-1);
		header.before = header.after = header;
	}

	private void computeMaxSize() {
		threshold = (int) (elementData.length * loadFactor);
	}


	/**
	 * Returns the value of the mapping with the specified key.
	 *
	 * @param key the key.
	 * @return the value of the mapping with the specified key, or {@code null}
	 *         if no mapping for the specified key is found.
	 */
	public V get(final int key) {
		final int index = (key & 0x7FFFFFFF) % elementData.length;

		IntLinkedEntry<V> m = elementData[index];
		while (m != null) {
			if (key == m.key) {
                //如果按照访问的顺序迭代map的元素. 则访问一次,这个元素会被加入到链表的尾部.
                //注意: 链表的header之后的是最近不常访问的条目, 链表的尾部是最近访问的元素.
				if (accessOrder) {
					//if (log.isDebugEnabled()) log.debug("reliking " + this.key);
                    //先把这个元素删除掉
					m.remove();
                    //然后加入到链表尾部. 这样这个元素就变成了最近访问的元素了
					m.addBefore(header);  //使当前元素m成为header的before. 即before.after=m. m is the last element of LHM
				}
				return m.value;
			}
            //访问时,还是通过数组, 而不是通过双向链表. 双向链表用在迭代获取所有条目时使用:从头指针遍历链表
            //因为数组可以直接定位到key对应的index, 然后再从数组索引的链表(不是双向链表)里再循环找出key对应的条目.
			m = m.nextInSlot;
		}
		return null;
	}


	/**
	 * Returns whether this map is empty.
	 *
	 * @return {@code true} if this map has no elements, {@code false} otherwise.
	 * @see #size()
	 */
	public boolean isEmpty() {
		return (elementCount == 0);
	}

	/**
	 * Maps the specified key to the specified value.
	 *
	 * @param key   the key.
	 * @param value the value.
	 * @return the value of any previous mapping with the specified key or
	 *         {@code null} if there was no such mapping.
	 */
	public V put(final int key, final V value) {
		int index = (key & 0x7FFFFFFF) % elementData.length;

		IntLinkedEntry<V> entry = elementData[index];
		while (entry != null && key != entry.key) {
            //遍历数组索引上的链表
			entry = entry.nextInSlot;
		}

		if (entry == null) {
			// Remove eldest entry if instructed, else grow capacity if appropriate
            // header.before是最近的条目, header.after是最旧的条目
			IntLinkedEntry<V> eldest = header.after; //最旧的条目
			++elementCount;
			if (removeEldestEntry(eldest)) {  //如果我们的策略要求删除最旧的条目
				remove(eldest.key);
			} else {
				if (elementCount > threshold) {
					rehash();
					index = (key & 0x7FFFFFFF) % elementData.length;
				}
			}
			entry = createHashedEntry(key, index);
		}

		V result = entry.value;
		entry.value = value;
		return result;
	}

	IntLinkedEntry<V> createHashedEntry(final int key, final int index) {
		IntLinkedEntry<V> entry = reuseAfterDelete();
		if (entry == null) {
			entry = new IntLinkedEntry<V>(key);
		} 
		else {
			entry.key = key;
			entry.value = null;
		}

        //创建的新的条目作为数组索引的链表表头, 同时也要加到双向链表的表尾
		entry.nextInSlot = elementData[index];  //当前条目的下一个条目是原先数组索引的第一个条目
		elementData[index] = entry; //现在当前条目作为数组索引的第一个条目了
		entry.addBefore(header); // LinkedList 加入到双向链表的表尾
		return entry;
	}

	void rehash(final int capacity) {
		final int length = primeSize(capacity == 0 ? 1 : capacity << 1);
		if (log.isDebugEnabled()) log.debug(this.getClass().getName() + "::rehash() old=" + elementData.length + " new=" + length);

		IntLinkedEntry<V>[] newData = newElementArray(length);
		for (int i = 0; i < elementData.length; i++) {
			IntLinkedEntry<V> entry = elementData[i];
			while (entry != null) {
				int index = (entry.key & 0x7FFFFFFF) % length;
				IntLinkedEntry<V> next = entry.nextInSlot;
				entry.nextInSlot = newData[index];
				newData[index] = entry;
				entry = next;
			}
		}
		elementData = newData;
		computeMaxSize();
	}

	void rehash() {
		rehash(elementData.length);
	}

	/**
	 * Removes the mapping with the specified key from this map.
	 *
	 * @param key the key of the mapping to remove.
	 * @return the value of the removed mapping or {@code null} if no mapping
	 *         for the specified key was found.
	 */
	public V remove(final int key) {
        //key对应的条目
		IntLinkedEntry<V> entry = removeEntry(key);
		if (entry == null) return null;
		V ret = entry.value;
		reuseAfterDelete(entry);

		return ret;
	}

	public V removeEldest() {
		final IntLinkedEntry<V> eldest = header.after;
		V ret = eldest.value;
		remove(eldest.key);
		return ret;
	}

	IntLinkedEntry<V> removeEntry(final int key) {
		IntLinkedEntry<V> last = null;

		final int index = (key & 0x7FFFFFFF) % elementData.length;
		IntLinkedEntry<V> entry = elementData[index];

		while (true) {
			if (entry == null) return null;

            //找到了要删除的条目entry
			if (key == entry.key) {
				if (last == null) {
					elementData[index] = entry.nextInSlot;
				} else {
					last.nextInSlot = entry.nextInSlot;
				}
				--elementCount;
                //从双向链表中删除条目
				entry.remove();
				return entry;
			}

            //数组索引的链表,如果没有找到,则使用next指针右移
            //同时要保存last, 因为找到要删除的条目后,last要指向删除条目的下一个条目.
			last = entry;
			entry = entry.nextInSlot;
		}
	}

	/**
	 * Returns the number of elements in this map.
	 *
	 * @return the number of elements in this map.
	 */
	public int size() {
		return elementCount;
	}

	// ========== Entry Cache

	/*
	private transient Entry<V> reuseAfterDelete = null;

	private void initCache(int size) {}
	private void clearCache() {}

	private Entry<V> reuseAfterDelete() {
		final Entry<V> ret = reuseAfterDelete;
		reuseAfterDelete = null;
		return ret;
	}
	private void reuseAfterDelete(final Entry<V> entry) {
		entry.clean();
		reuseAfterDelete = entry;
	}
	 */

	private ArrayDeque<IntLinkedEntry<V>> cache;

	private void initCache(final int size) {
		cache = new ArrayDeque<IntLinkedEntry<V>>(size);
	}

    public void clearCache() {
		cache.clear();
	}

    //添加条目时, 如果有空闲空间,则优先使用空闲空间, 而不用重新创建新的条目
    private IntLinkedEntry<V> reuseAfterDelete() {
		final IntLinkedEntry<V> reuse = cache.pollLast();
		if (reuse != null) {
			if (log.isDebugEnabled()) log.debug("reusing IntLinkedEntry<V>=" + reuse.hashCode() + " cacheSize=" + cache.size());
		}
		return reuse;
	}

    //删除后, 条目占用的空间会被加入到缓存列表中
    private void reuseAfterDelete(final IntLinkedEntry<V> entry) {
		entry.clean();
		cache.offerLast(entry);
	}

	// ========== Internal Entry

	protected static final class IntLinkedEntry<V> {
		// These fields comprise the doubly linked list used for iteration.
        //双向链表的前一个,后一个条目的指针
		private IntLinkedEntry<V> before, after;
		//数组索引中的链表的下一个条目的指针
		private IntLinkedEntry<V> nextInSlot;
		protected int key;
		protected V value;

		IntLinkedEntry(final int theKey) {
			this.key = theKey;
			this.value = null;
		}

		private void clean() {
			value = null;
			key = Integer.MIN_VALUE;
			nextInSlot = null;
			before = null;
			after = null;
		}

		/**
		 * Removes this entry from the linked list.
         *  ____          ____          ____
         * |    |  <-->  |    |  <-->  |    |
         * before        entry         after
         *
         *    __________________________
         *  _|__         ____         _ |_
         * |    |       |    |       |    |
         * before       entry        after
		 */
		private void remove() {
			before.after = after;
			after.before = before;
		}

		/**
		 * Inserts this entry before the specified existing entry in the list.
         * 将当前条目插入到指定的条目之前. 即指定条目的before指针指向当前条目
         *
         * 在put和get时, 会将当前条目(this)加入到双向链表的尾部, 即header(existingEntry)的前面
         *    ____________________________
         *  _|__          ____          __|_      ____
         * |    |  <-->  |    |  <-->  |    |    |    |
         * header         1st           2nd       entry
         * after                        before
         * entry.after                  entry.before
         *
         *    _________after.before=this______________
         *  _|__          ____          ____        __|_
         * |    |  <-->  |    |  <-->  |    | -->  |    |
         * header         1st           2nd         entry
         *                             before.after=this
         *
         * 添加一个条目, 一共有4个指针.
         * 1. 当前条目到前一个条目  before
         * 2. 前一个条目到当前条目  before.after=this
         * 3. 当前条目到下一个条目  after
         * 4. 下一个条目到当前条目  after.before=this
		 */
		private void addBefore(IntLinkedEntry<V> existingEntry) {
            //设置当前条目的after, before. 即从当前条目触发的指针
			after  = existingEntry;         //③
			before = existingEntry.before;  //①
            //设置其他条目到当前条目的指针.
			before.after = this;            //②
			after.before = this;            //④
		}

		/**
		 * Returns the key corresponding to this entry.
		 *
		 * @return the key corresponding to this entry
		 */
		public int getKey() {
			return key;
		}
		
		/**
		 * Returns the value corresponding to this entry.
		 *
		 * @return the value corresponding to this entry
		 */
		public V getValue() {
			return value;
		}
	}

	// ========== Linked List

	/**
	 * Returns <tt>true</tt> if this map should remove its eldest entry.
	 * This method is invoked by <tt>put</tt> and <tt>putAll</tt> after
	 * inserting a new entry into the map.  It provides the implementor
	 * with the opportunity to remove the eldest entry each time a new one
	 * is added.  This is useful if the map represents a cache: it allows
	 * the map to reduce memory consumption by deleting stale entries.
	 *
	 * <p>Sample use: this override will allow the map to grow up to 100
	 * entries and then delete the eldest entry each time a new entry is
	 * added, maintaining a steady state of 100 entries.
	 * <pre>
	 *     private static final int MAX_ENTRIES = 100;
	 *
	 *     protected boolean removeEldestEntry(IntLinkedEntry eldest) {
	 *        return size() > MAX_ENTRIES;
	 *     }
	 * </pre>
	 *
	 * <p>This method typically does not modify the map in any way,
	 * instead allowing the map to modify itself as directed by its
	 * return value.  It <i>is</i> permitted for this method to modify
	 * the map directly, but if it does so, it <i>must</i> return
	 * <tt>false</tt> (indicating that the map should not attempt any
	 * further modification).  The effects of returning <tt>true</tt>
	 * after modifying the map from within this method are unspecified.
	 *
	 * <p>This implementation merely returns <tt>false</tt> (so that this
	 * map acts like a normal map - the eldest element is never removed).
	 *
	 * @param    eldest The least recently inserted entry in the map, or if
	 *           this is an access-ordered map, the least recently accessed
	 *           entry.  This is the entry that will be removed it this
	 *           method returns <tt>true</tt>.  If the map was empty prior
	 *           to the <tt>put</tt> or <tt>putAll</tt> invocation resulting
	 *           in this invocation, this will be the entry that was just
	 *           inserted; in other words, if the map contains a single
	 *           entry, the eldest entry is also the newest.
	 * @return   <tt>true</tt> if the eldest entry should be removed
	 *           from the map; <tt>false</tt> if it should be retained.
	 */
	protected boolean removeEldestEntry(IntLinkedEntry<V> eldest) {
		return false;
	}

	// ========== Prime Finder

	private static final int primeSize(final int capacity) {
		//return java.math.BigInteger.valueOf((long)capacity).nextProbablePrime().intValue();
		return PrimeFinder.nextPrime(capacity);
	}

	// ========== Iterator

	/**
	 * @returns iterator over values in map
	 */
	public Iterator<V> iterator() {
		return new IntLinkedHashMapIterator<V>(this);
	}

	static class IntLinkedHashMapIterator<V> implements Iterator<V> {
		final IntLinkedHashMap<V> associatedMap;
		IntLinkedEntry<V> nextEntry    = null; //下一个条目
		IntLinkedEntry<V> lastReturned = null;

		public IntLinkedHashMapIterator(final IntLinkedHashMap<V> associatedMap) {
			this.associatedMap = associatedMap;
            //初始化时,指向第一个条目. 即header的after
			nextEntry = associatedMap.header.after;
		}

        //从header开始遍历链表, 通过after指针右移. 链表的最后一个条目的after是head
        //只要下一个条目不是head, 就说明还有下一个条目!
		public boolean hasNext() {
			return nextEntry != associatedMap.header;
		}

		public void remove() {
			if (lastReturned == null)
				throw new IllegalStateException();

			associatedMap.remove(lastReturned.key);
			lastReturned = null;
		}

		IntLinkedEntry<V> nextEntry() {
			if (nextEntry == associatedMap.header)
				throw new NoSuchElementException();

			IntLinkedEntry<V> e = lastReturned = nextEntry;
            //通过after指针指向下一个条目
			nextEntry = e.after;
			return e;
		}
		public V next() { return nextEntry().value; }
	}

	// =========================================

	public V[] getValues() {
		final V[] array = factory.newArray(elementCount);
		int i = 0;
		for (final V v : this) {
			array[i++] = v;
		}
		return array;
	}

	// =========================================

	public static void main(String[] args) {
		IntLinkedHashMap<Integer> hash = new IntLinkedHashMap<Integer>(16, Integer.class, true) {
			/*protected boolean removeEldestEntry(IntLinkedEntry<Integer> eldest) {
				System.out.println("---- begin");
				for (Integer i : this) {
					System.out.println(i);
				}
				System.out.println("---- end");
				return (size() > 3);
	        }*/
		};
		for (int i = 1; i < 6; i++) { // 1...4
			hash.put(i, i);
		}
		hash.put(3, 3);
		hash.put(3, 3);
		hash.put(3, 3);
		hash.put(3, 3);
		hash.get(3);
		//hash.remove(3);
		for (Integer i : hash) {
			System.out.println(i);
		}
		System.out.println("---");
		while (hash.size() > 0) {
			System.out.println("remove value=" + hash.removeEldest());
		}
		System.out.println("---");

		for (Integer i : hash) {
			System.out.println(i);
		}
	}
}
