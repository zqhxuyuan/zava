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
 * Native Integer HashMap
 * This class is NOT Thread-Safe
 *
 * @param <V> type of values
 *
 * @author Guillermo Grandes / guillermo.grandes[at]gmail.com
 */
public class IntHashMap<V> implements Iterable<V> {
	private static final Logger log = Logger.getLogger(IntHashMap.class);

	private int elementCount;           //元素个数
	private IntEntry<V>[] elementData;  //数组的每个元素是Map中的每个条目

	private final float loadFactor;     //加载因子
	private int threshold;              //容量阈值
	private int defaultSize = 17;       //初始大小

	private GenericFactory<V> factory;

	/**
	 * Constructs a new {@code IntHashMap} instance with the specified capacity.
	 *
	 * @param capacity the initial capacity of this hash map.
	 * @param type class for values
	 */
	public IntHashMap(final int capacity, final Class<V> type) {
		factory = new GenericFactory<V>(type);
		defaultSize = primeSize(capacity);
		if (capacity >= 0) {
			elementCount = 0;
			elementData = newElementArray(capacity == 0 ? 1 : capacity);
			loadFactor = 0.75f; // Default load factor of 0.75
			initCache(elementData.length);
			computeMaxSize();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Constructs a new {@code IntHashMap} instance with default capacity (17).
	 *
	 * @param type class for values
	 */
	public IntHashMap(final Class<V> type) {
		this(17, type);
	}

	@SuppressWarnings("unchecked")
	private IntEntry<V>[] newElementArray(int s) {
		return new IntEntry[s];
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

	/**
	 * Clear the map
	 * @param shrink if true shrink the map to initial size
	 */
	@SuppressWarnings("unchecked")
	public void clear(final boolean shrink) {
		clearCache();
		if (elementCount > 0) {            
			elementCount = 0;            
		}
		if (shrink && (elementData.length > 1024) && (elementData.length > defaultSize)) {
			elementData = new IntEntry[defaultSize];
		}
		else {
			Arrays.fill(elementData, null);
		}
		computeMaxSize();
	}

	private void computeMaxSize() {
		threshold = (int) (elementData.length * loadFactor);
	}


	/**
	 * Returns the value of specified key.
	 *
	 * @param key the key.
	 * @return the value of the mapping with the specified key, or {@code null}
	 *         if no mapping for the specified key is found.
	 */
	public V get(final int key) {
        //做与操作, 比取模速度更快. 这里与之后,又进行了取模
		final int index = (key & 0x7FFFFFFF) % elementData.length;

        //根据key获取key所在的条目在在数组中的索引位置, 返回的是key代表的条目.
		IntEntry<V> m = elementData[index];
		while (m != null) {
            //找到key, 返回这个条目对应的值
			if (key == m.key) return m.value;
            //如果Map上有散列冲突, 则使用next指针寻找同一个key上的下一个条目
            //在数组同一个index处的IndexEntry,对于不同的key,可能都在这个index上. 即用链表的形式表示!
			m = m.nextInSlot;
		}
		return null;
	}

	/**
	 * Returns whether this map is empty.
	 *
	 * @return {@code true} if this map has no elements, {@code false}
	 *         otherwise.
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
        //要放到数组的哪个索引处
		int index = (key & 0x7FFFFFFF) % elementData.length;

        //获取出这个位置上已经存在的条目
		IntEntry<V> entry = elementData[index];
        //这个位置上已经有key了, 说明新添加的键值对和原先的key产生了散列冲突!
        //那么新添加的KV是添加到链表的表头还是表尾?
		while (entry != null && key != entry.key) {
            //1)entry是数组中原有的条目, 如果这个条目没有下一个条目, 则entry.nextInSlot=null, 导致entry=null
            //2)当然如果数组的index中原先没有条目,entry不会执行while语句, 最后的结果entry=null
            //3)如果当前条目entry有下一个条目, 比如k1->k3, entry=k1, k1=k1.next=k3
            //则还要进行while循环, k3=k3.next=null,最终也导致entry=null
            //4)要添加的元素在数组中已经存在,则entry!=null,最后会进行覆盖!
            //所以**put之前实际上是进行了一次链表的扫描!** 以判断链表中是否有要添加的元素.
			entry = entry.nextInSlot;
		}

		if (entry == null) {
            //在还没添加元素前,将数量+1, 并用加1后的数量判断是否超过阈值.因为不能在添加之后,才知道达到阈值了!
			if (++elementCount > threshold) {
                //重新进行hash
				rehash();
                //计算新的index
				index = (key & 0x7FFFFFFF) % elementData.length;
			}
            //创建一个新的条目
			entry = createHashedEntry(key, index);
		}

        //这个entry现在是我们新添加的元素! 当然如果链表中已经存在该entry,则进行覆盖!
		V result = entry.value;
		entry.value = value;
		return result;
	}

    //在每次新创建一个条目时, 会加到数组的index位置的链表的表头! 链表表示同一个index的多个有散列冲突的条目!
	IntEntry<V> createHashedEntry(final int key, final int index) {
        //删除元素时,会留下空闲空间, 才添加元素时, 优先使用空闲空间
		IntEntry<V> entry = reuseAfterDelete();
        //没有空闲空间, 则只能新创建一条条目了
		if (entry == null) {
			entry = new IntEntry<V>(key);
		} else {
            //有空闲空间, 就使用
			entry.key = key;
			entry.value = null;
		}

        //entry是最新的条目, 将它的nextInSlot设置为数组中index位置已经存在的条目.
        //即新添加的条目在链表的表头!
		entry.nextInSlot = elementData[index];
        //设置数组index处的链表的表头为当前新创建的条目
		elementData[index] = entry;
		return entry;
	}

    //重新hash时, 原先在数组中同一个index的条目,可能会被hash到不同的index中. 因为hash的基础是数组的长度. 数组长度变化,index也会变化.
	void rehash(final int capacity) {
        //capacity的容量翻倍了
		final int length = primeSize(capacity == 0 ? 1 : capacity << 1);
		if (log.isDebugEnabled()) log.debug(this.getClass().getName() + "::rehash() old=" + elementData.length + " new=" + length);

        //创建一个新的数组
		IntEntry<V>[] newData = newElementArray(length);
		for (int i = 0; i < elementData.length; i++) {
            //原数组中旧的每个条目
			IntEntry<V> entry = elementData[i];
			while (entry != null) {
                //entry表示的是条目的第一个元素, 因为一个条目会有多个键值对
                //用新的数组长度计算在新数组中的index,将被用于newData中!
				int index = (entry.key & 0x7FFFFFFF) % length;

                //找到这个条目的下一个元素
				IntEntry<V> next = entry.nextInSlot;
				entry.nextInSlot = newData[index];
				newData[index] = entry;
                //设置下一个要循环的entry为当前entry的next引用
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
		IntEntry<V> entry = removeEntry(key);
		if (entry == null) return null;

		V ret = entry.value;
        //回收删除的条目
		reuseAfterDelete(entry);

		return ret;
	}

    //删除元素要考虑同一个数组index处的元素的指针更新
	IntEntry<V> removeEntry(final int key) {
		IntEntry<V> last = null;

		final int index = (key & 0x7FFFFFFF) % elementData.length;
		IntEntry<V> entry = elementData[index];

		while (true) {
			if (entry == null) return null;

            //找到了这个条目
			if (key == entry.key) {
                //没有执行到if后面的语句, 即第一个条目就是要删除的条目
                //entry -> next
                //删除链表表头entry后
                //数组的index的条目就是entry的下一个条目
				if (last == null) {
					elementData[index] = entry.nextInSlot;
				} else {
                    //要删除的条目是entry,
                    //last -> entry -> next
                    //删除entry后, 要将last的下一个条目指向next
                    //last --> next
                    //这里不需要更新elementData[index]因为entry并不是链表的第一个条目. 第一个条目没有变化,就不需要更新!
					last.nextInSlot = entry.nextInSlot;
				}
				elementCount--;
				return entry;
			}

            //循环,在链表中找出要删除的条目
			last = entry; //上一个条目
            //下一个条目, 使用next指针指向下一个条目.作为下一次循环的条目
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

	private ArrayDeque<IntEntry<V>> cache;

	private void initCache(final int size) {
		cache = new ArrayDeque<IntEntry<V>>(size);
	}
	public void clearCache() { 
		cache.clear();
	}
	private IntEntry<V> reuseAfterDelete() {
		return cache.pollLast();
	}
	private void reuseAfterDelete(final IntEntry<V> entry) {
		entry.clean();
        //添加到缓存中. 用队列实现,往队列尾部添加元素
		cache.offerLast(entry);
	}

	// ========== Internal Entry 内部条目

	static final class IntEntry<V> {
		IntEntry<V> nextInSlot; //下一个条目. 因为不同的key可能会散列在数组的同一个索引位置, 所以通过next指针将相同散列的条目链接在一起
		int key;                //因为是Map,所以Map的每个条目是KeyValue键值对.
		V value;

		IntEntry(int theKey) {
			this.key = theKey;
			this.value = null;
		}
		void clean() {
			value = null;
			key = Integer.MIN_VALUE;
			nextInSlot = null;
		}
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
		return new IntHashMapIterator<V>(this);
	}

    /**
     * 内部类, 迭代器, 迭代获取Map中的条目. 因为Map的条目使用数组存储.
     * 数组的每个元素会存在散列冲突,冲突的条目会以链表的形式保存. 在访问数组的元素时,
     * 会首先访问数组索引的第一个条目,然后使用next指针,依次访问链表的每个条目元素. 当链表结束后,访问数组的下一个索引.
     *
     * associateMap.elementData
     * index    IntEntry     IntEntry
     *   0  -->  [K1,V1]
     *   1  -->
     *   2  -->  [K2,V2] --> [K4,V4]
     *   3  -->  [K6,V6] --> [K3,V3] --> K[8,V8]
     *   4  -->  [K7,V7]
     *
     *  第一次调用hasNext, entry=null, newPos=pos=0, ele[newPos]!=null, result=true, pos=0 *指向第一个数组索引
     *    hasNext=true --> next: _entry=entry=null, result=lastEntry=ele[pos=0]=ele[0],
     *                           pos+1=1  *指向下一个数组索引
     *                           entry=lastEntry.next=ele[0].next=null  数组第一个索引只有一个条目,当前这个索引元素没有下一个条目了
     *
     *  调用hasNext, entry=null, newPos=pos=1, ele[1]=null, newPos+1=2, result=false, pos=newPos=2 指向下一个数组索引 *
     *  调用hasNext, entry=null, newPos=pos=2, ele[2]!=null, result=true, pos=newPos=2
     *    haxNext=true --> next: _entry=entry=null,
     *                           result=lastEntry=ele[pos=2]=ele[2]=K2
     *                           pos+1=3  *指向下一个数组索引
     *                           entry=lastEntry.next=ele[2].next=K2.next=K4
     *  调用hasNext, entry!=null, return true
     *    haxNext=true --> next: _entry=entry=K4!=null,
     *                           lastEntry.next=K2.next=K4 == _entry
     *                           result=_entry=K4
     *                           entry=_entry.next=K4.next=null
     *
     *  当调用next时, 如果是第一次访问数组的索引(总是先访问数组索引的第一个条目,然后接着链表后的其他条目).
     *  设置result=lastEntry=数组索引的第一个条目, 并设置entry=lastEntry.next. 最后返回result, 即当前元素.
     *
     *  ********************************************************************************************
     *
     *  第一次调用数组的索引时, 指向的是这个数组索引元素的第一个条目.在返回第一个条目前,设置entry=lastEntry.next
     *  当然如果数组索引只有一个条目时, 因为没有下一个条目了,所以entry=null.
     *  第二种情况就是数组索引不止一个条目, 则设置entry为下一个条目!
     *   ____
     *  ||||||     EOL(EndOfLinkedList)
     *  lastEntry  entry=lastEntry.next=null
     *  result!
     *   ____       ____
     *  |||||| --> |    |
     *  lastEntry  entry
     *  result!
     *
     *  当访问链表的下一个元素(第二个)时, lastEntry指向的是上一次访问的条目, 而entry的值在上一步已经给出了, 所以result=entry
     *  并且为了统一entry的概念是:指向下一个条目, 要设置entry=entry.next. 这里同样面临者是否有下一个条目的问题.
     *
     *  下图中的两种情况对应了是否有下一个条目. *==>是调用next前后发生的变化.
     *   ____       ____                      ____       ____
     *  |    | --> ||||||   (EOL)      *==>  |    | --> ||||||     (EOL)
     *  lastEntry  entry                     lastEntry     |       entry=entry.next=null!
     *             result!                              result!
     *   ____       ____       ____           ____       ____       ____
     *  |    | --> |||||| --> |    |   *==>  |    | --> |||||| --> |    |
     *  lastEntry  entry                     lastEntry     |       entry
     *             result!                              result!
     *
     *  注意作为数组索引的第二个条目,并没有更新lastEntry. 因为在访问第一个条目时,lastEntry虽然指向第一个条目,但是
     *  作为lastEntry的含义上一个条目来说,数组索引的第一个条目并没有上一条条目! 所以当访问第二个条目时,lastEntry
     *  仍然指向第一个条目,它的字面含义上一个条目就说的通了,因为第二个条目的上一个条目是第一个条目.
     *
     *  来看下访问第三个条目发生了什么变化:
     *   ____       ____       ____           ____       ____       ____          __      __      ____
     *  |    | --> |    | --> ||||||   *==>  |    | --> |    | --> ||||||  *==>  |  | -> |  | -> ||||||    (EOL)
     *  lastEntry             entry                     lastEntry   entry                last    result!   entry=null!
     *            (A)                                   (B)                              (C)
     *  现在我们访问到了第三个条目(A),第三个条目的上一个条目应该是第二个条目:
     *  判断条件: lastEntry.next != entry, 设置lastEntry=lastEntry.next. 上图的中图(B)描述了这个变化.
     *  上图的右图(C)表示了entry就是我们要返回的result. 并且由于当前节点是链表的最后一个节点,所以下一个节点entry=null!
     *  当然如果当前访问的节点不是链表的最后一个节点, 则entry指向的是下一个节点. 以此类推...
     *
     */
	static class IntHashMapIterator<V> implements Iterator<V> {
		private int position = 0;       //数组的索引. 当访问到数组的一个新索引时,position+1

		boolean canRemove = false;
		IntEntry<V> entry;              //下一个条目, 当访问到数组的一个索引的链表的最后一个条目时,entry=null
		IntEntry<V> lastEntry;          //上一个条目
		final IntHashMap<V> associatedMap;

		IntHashMapIterator(IntHashMap<V> hm) {
			associatedMap = hm;
		}

        @Override
		public boolean hasNext() {
            //entry指向的就是下一个条目. 所以判断是否有下一个条目时,如果entry不为空,就说明有下一个元素
            //所以在调用next的时候,就应该在获取到元素之后, 把entry指向下一个元素(链表的下一个元素)
            //当到达一个数组索引元素的链表的尾部时,会使得entry=null,这时候要重新从下一个数组索引开始
			if (entry != null) {
				return true;
			}

            //数组
			IntEntry<V>[] elementData = associatedMap.elementData;
			int length = elementData.length;
            //如果在链表里,则不会执行这里. 如果第一次访问数组的某个索引,已经把position+1了.
            //这样第一次访问数组的下一个索引时,position的值就是上一个数组索引+1的值!
			int newPosition = position;
			boolean result = false;

            //不能超过数组的长度
			while (newPosition < length) {
                //数组的某个索引位置没有条目,则找下一个索引. 因为key散列到的数组位置可能有些index没有分布到条目
				if (elementData[newPosition] == null) {
                    //如果访问的这个数组索引为空,则跳过这个数组索引,访问下一个索引.
                    //如果没有这段话,在while前newPosition=position, 在while后position=newPosition其实没任何变化
                    //但正是因为这种第一次访问数组的索引时,可能这个位置并没有条目,所以position应该指向下一个数组的索引.
                    //所以position的自增操作发生在两个地方: 第一次访问数组的某个索引. 只要第一次访问,不管这个数组元素有没有条目,都+1!
                    //不同的是如果访问的数组索引位置为空,在hasNext时就自增, 而访问的数组索引位置有条目,在调用next时才自增!
                    //总的来说: position指向的是下一个数组索引.
					newPosition++;
				} 
				else {
                    //只有在entry==null时:第一次访问到数组的某个索引,如果这个数组有条目,则表示有下一个条目
                    //如果不是第一次访问数组的这个索引,比如在索引的链表里, 因为entry!=null,则不会执行到这里
                    //第一次访问数组的这个索引: 指的不仅仅是第一次调用hasNext,
                    //而是一个数组的某个索引访问完毕,第一次访问下一个数组的索引.
                    //我们把##非第一次##访问数组的某个索引,表示为之前访问过数组的这个索引,
                    //下一次的调用还是在这个数组所在的索引里:即访问的是链表.
					result = true;
					break;
				}
			}

            //访问到了哪个数组的索引. 在同一个数组索引的链表内,entry!=null,不会执行这里.因为position没有变化!
			position = newPosition;
			return result;
		}

        @Override
		public V next() {
            //在调用next获取元素前,先判断是否有下一个元素
			if (!hasNext()) throw new NoSuchElementException();

			IntEntry<V> result;
			IntEntry<V> _entry = entry;
            //什么时候entry=null, 第一次访问时, 或者数组的一个index访问完毕,整个index的链表位置访问完毕了.则要访问数组的下一个index!
			if (_entry == null) {
                //result = lastEntry = associatedMap.elementData[position] ; position++
                //result指向的是数组的index位置=position的链表的第一个条目. 在定位到数组新的index时,要将position索引加1指向数组的下一个index!
				result = lastEntry = associatedMap.elementData[position++];
                //将entry指向当前访问的元素的下一个元素. 如果lastEntry是数组的index的链表的最后一个条目,则会导致entry=lastEntry.next=null
				entry = lastEntry.nextInSlot;
			} else {
                //在数组的index的链表的第二个元素开始....调用next时,都在整个链表内操作. 即数组的同一个index的链表内!
				if (lastEntry.nextInSlot != _entry) {
					lastEntry = lastEntry.nextInSlot;
				}
				result = _entry;
				entry = _entry.nextInSlot;
			}
			canRemove = true;
			return result.value;
		}

        @Override
        //添加节点添加到链表表头, 删除节点时, 也应该首先删除链表表头节点
        //TODO remove什么时候被调用?
		public void remove() {
			if (!canRemove) throw new IllegalStateException();
			canRemove = false;

			if (lastEntry.nextInSlot == entry) {
				while (associatedMap.elementData[--position] == null) {
					// Skip
				}
                //ele[position]是数组索引的第一个条目/节点. 删除第一个节点后, 原先第二个节点会作为第一个节点
                //first = first.next. 这里的first即associatedMap.elementData[position]
				associatedMap.elementData[position] = associatedMap.elementData[position].nextInSlot;
				entry = null;
			} 
			else {
				lastEntry.nextInSlot = entry;
			}
			if (lastEntry != null) {
				IntEntry<V> reuse = lastEntry;
				lastEntry = null;
				associatedMap.reuseAfterDelete(reuse);
			}

			associatedMap.elementCount--;
		}
	}

	// =========================================

	/**
	 * Return an array with values in this map
	 * @return array with values
	 */
	public V[] getValues() {
		final V[] array = factory.newArray(elementCount);
		int i = 0;
        //this使用for循环,所以是个Iterator. V是调用Iterator.next获取的value
		for (final V v : this) {
			array[i++] = v;
		}
		return array;
	}

	// =========================================

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		if (false) {
			long capacity = 1;
			int count = 1;
			while (capacity < Integer.MAX_VALUE) {
				capacity = java.math.BigInteger.valueOf(capacity).nextProbablePrime().longValue();
				System.out.print(capacity + ", ");
				final double inc = Math.log(2)/Math.log(capacity<<5) * 10 + 1; 
				//System.out.println(inc);
				capacity *= inc; 
				if (count % 5 == 0) System.out.println();
				count++;
			}
			System.out.println(Integer.MAX_VALUE);
			System.out.println("------");

			System.out.println(count);
			System.out.println(PrimeFinder.nextPrime((int)1e6));
		}
		if (true) {
			IntHashMap<Integer> hash = new IntHashMap<Integer>(16, Integer.class);
			hash.put(1, 2);
			hash.put(2, 4);
            System.out.println("使用for模式:");
            for (Integer i : hash.getValues()) {
				System.out.println(i);
			}
            System.out.println("hash大小:"+hash.size());

            System.out.println("使用迭代模式:");

            for (IntHashMapIterator iterator = (IntHashMapIterator) hash.iterator(); iterator.hasNext();) {
                Integer intValue = (Integer)iterator.next();
                System.out.println(intValue);
                //iterator.remove();
            }
            System.out.println("hash大小:"+hash.size());
        }
	}
}
