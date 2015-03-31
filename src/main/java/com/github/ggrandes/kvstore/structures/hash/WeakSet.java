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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import com.github.ggrandes.kvstore.utils.PrimeFinder;

/**
 * A hashtable-based Set implementation with weak values. An entry in a
 * 基于hashTable实现的集合, 使用weak值. WeakSet中的一个条目会被自动移除: 它的值不再被经常使用时.
 * WeakSet will automatically be removed when its value is no longer in
 * ordinary use. More precisely, the presence of a given value will
 * 更精确地说:给定一个值,如果它存了, 也不会阻止垃圾回收其将它回收.
 * not prevent the value from being discarded by the garbage collector, that is,
 * made finalizable, finalized, and then reclaimed. When a value has been
 * discarded its entry is effectively removed from the set, so this class
 * 当一个值被丢弃后,它的条目也会从集合中被删除掉.
 * behaves somewhat differently from other Set implementations.
 * <p>
 * This class is NOT Thread-Safe
 * 
 * @see java.util.WeakHashMap
 * 
 * @author Guillermo Grandes / guillermo.grandes[at]gmail.com
 */
public class WeakSet<T> {

	private int elementCount;
	private Entry<T>[] elementData;

	private final float loadFactor;
	private int threshold;
	private int defaultSize = 17;

	/**
	 * Reference queue for cleared WeakEntry 引用队列,用来清除条目
	 */
	private final ReferenceQueue<T> queue = new ReferenceQueue<T>();

	/**
	 * Constructs a new {@code WeakSet} instance with the specified capacity.
	 * 
	 * @param capacity the initial capacity of this set.
	 * @param type class for values
	 */
	public WeakSet(final int capacity) {
		defaultSize = primeSize(capacity);
		if (capacity >= 0) {
			elementCount = 0;
			elementData = newElementArray(capacity < 0 ? 1 : capacity);
			loadFactor = 0.75f; // Default load factor of 0.75
			computeMaxSize();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Constructs a new {@code WeakSet} instance with default capacity (17).
	 */
	public WeakSet() {
		this(17);
	}

	/**
	 * Check for equal objects
	 * 
	 * @param o1
	 * @param o2
	 * @return true if equals
	 */
	private final static boolean eq(Object o1, Object o2) {
		return ((o1 == o2) || o1.equals(o2));
	}

	/**
	 * Removes all values from this WeakSet, leaving it empty.
	 * 
	 * @see #isEmpty
	 * @see #size
	 */
	public final void clear() {
		clear(true);
	}

	/**
	 * Clear the set
	 * 
	 * @param shrink if true, shrink the set to initial size
	 */
	public void clear(final boolean shrink) {
		while (queue.poll() != null);
		if (elementCount > 0) {
			elementCount = 0;
		}
		if (shrink && (elementData.length > 1024) && (elementData.length > defaultSize)) {
			elementData = newElementArray(defaultSize);
		} else {
			Arrays.fill(elementData, null);
		}
		computeMaxSize();
		while (queue.poll() != null);
	}

	/**
	 * Returns the specified value.
	 * 
	 * @param value the value.
	 * @return the value, or {@code null} if not found the specified value
	 */
	public T get(final T value) {
		expungeStaleEntries();
		//
		final int index = (value.hashCode() & 0x7FFFFFFF) % elementData.length;
		Entry<T> m = elementData[index];
		while (m != null) {
			if (eq(value, m.get()))
				return m.get();
            //由于不同节点可能散列到数组的同一个索引上. 这些不同的节点会以散列表的形式保存
            //所以在获取value对应的节点时,当定位到数组的索引index后. 还不能立即确定第一个节点就是要查的.
            //因为这个数组索引上是一个链表节点. 所以如果没有找到,就遍历链表. 通过条目的next引用,不断往右移动指针来控制循环.
			m = m.nextInSlot;
		}
		return null;
	}

	/**
	 * Returns whether this set is empty.
	 * 
	 * @return {@code true} if this set has no elements, {@code false} otherwise.
	 * @see #size()
	 */
	public final boolean isEmpty() {
		return (size() == 0);
	}

	/**
	 * Puts the specified value in the set.
	 * 
	 * @param value the value.
	 * @return the value of any previous put or {@code null} if there was no such value.
	 */
	public T put(final T value) {
		expungeStaleEntries();
		//
		final int hash = value.hashCode();
		int index = (hash & 0x7FFFFFFF) % elementData.length;
		Entry<T> entry = elementData[index];
		while (entry != null && !eq(value, entry.get())) {
            //和IntHashMap的逻辑类似,会遍历数组索引上的链表节点, 因为链表中可能已经有这个节点了,则进行更新!
			entry = entry.nextInSlot;
		}

		if (entry == null) {
			if (++elementCount > threshold) {
				expandElementArray(elementData.length);
				index = (hash & 0x7FFFFFFF) % elementData.length;
			}
			entry = createHashedEntry(value, index);
			return null;
		}

		final T result = entry.get();
		return result;
	}

	private final Entry<T> createHashedEntry(final T valye, final int index) {
        //纵观本类,并没有找到queue.offer.而对于队列而言,加入队列,才使得队列中有元素.
        //这里在创建一个条目的时候,传递了queue对象,实际上就是把当前Entry条目加入到了queue中了!
		Entry<T> entry = new Entry<T>(valye, queue);
        //类似于HashMap底层使用数组来存储时,同一个数组索引位置会有多个条目.
        //所以设置新创建的条目是数组索引的第一个条目②.它的下一个条目指向原先的第一个条目①
		entry.nextInSlot = elementData[index]; //①
		elementData[index] = entry; //②
		return entry;
	}

	private final void computeMaxSize() {
		threshold = (int) (elementData.length * loadFactor);
	}

	@SuppressWarnings("unchecked")
	private final Entry<T>[] newElementArray(int s) {
		return new Entry[s];
	}

	private final void expandElementArray(final int capacity) {
		final int length = primeSize(capacity < 0 ? 1 : capacity << 1);
		final Entry<T>[] newData = newElementArray(length);
        //循环将旧数组中的条目复制到新扩容的数组中
		for (int i = 0; i < elementData.length; i++) {
			Entry<T> entry = elementData[i];
            //回收旧数组的空间.
			elementData[i] = null;
			while (entry != null) {
                //数组索引存储者链表, 取到一个条目后, 要找到下一个条目, 下一个条目也要重新放到扩容数组中.
				final Entry<T> next = entry.nextInSlot;
				final T value = entry.get();
				if (value == null) {
                    //在扩容时,如果这个条目的值为空, 说明已经被垃圾回收了,则不放入新数组中
					entry.nextInSlot = null;
					elementCount--;
				} else {
                    //计算旧数组中的条目在新数组中的索引位置
					final int index = (entry.hash & 0x7FFFFFFF) % length;
                    //最近取到的条目胡会放在链表的表头.类似于添加条目时,加在链表表头.
                    //比如数组索引index中已经有一个条目了,一个新的条目如果其index相同,则新条目会加到旧条目的前面
					entry.nextInSlot = newData[index];   //加入一个新条目时,要做2件事情, 一是更新next引用=旧的数组索引的第一个条目
					newData[index] = entry;              //二是将自己设为数组索引的第一个条目.
				}
                //下次循环的节点是当前节点的下一个节点.以此类推
				entry = next;
			}
		}
		elementData = newData;
		computeMaxSize();
	}

    //删除过期的条目
	@SuppressWarnings("unchecked")
	private final void expungeStaleEntries() {
		Entry<T> entry;
        //从queue中弹出一个条目. 因为在创建条目的时候传入了queue实际上就是将条目加入到queue中
        //一旦从queue中要弹出一个条目,这个条目就是要被删除的了!
		while ((entry = (Entry<T>) queue.poll()) != null) {
			final int i = (entry.hash & 0x7FFFFFFF) % elementData.length;

            //在数组中的位置. 注意prev不一定就是我们要删除的节点entry!
			Entry<T> prev = elementData[i];
			Entry<T> p = prev;
			while (p != null) {
                //下一个条目
				Entry<T> next = p.nextInSlot;
                //找到要删除的节点. 即当前循环访问到的节点p == 要删除的节点entry
				if (p == entry) {
					if (prev == entry) {
                        //如果是数组索引的第一个节点. 则删除第一个条目后,下一个条目next变成第一个条目了elementData[i].
						elementData[i] = next;
					} else {
                        //不是第一个节点,因为我们知道当前要删除的节点的上一个节点是prev, 将prev的next设置成next. 这样entry就会从中间被删除了.
						prev.nextInSlot = next;
					}
                    //回收entry
					entry.nextInSlot = null;
					elementCount--;
					break;
				}
                //如果循环的节点p不是我们要删除的节点
				prev = p;  //记录本次处理的节点,这样下一次循环时,知道prev上次处理了哪个节点.
				           //记录prev的目的是找到要删除的节点后,这个prev的下一个节点引用会被更新为要删除的节点的下一个节点.
				p = next;  //将p指向下一个节点,这样下次循环时, 针对的就是下一个节点了. 以此类推,在没找到时,一直下一个
			}
		}
	}

	/**
	 * Removes the specified value from this set.
	 * 
	 * @param value the value to remove.
	 * @return the value removed or {@code null} if not found
	 */
	public T remove(final T value) {
		expungeStaleEntries();
		//
		final Entry<T> entry = removeEntry(value);
		if (entry == null)
			return null;
		final T ret = entry.get();
		return ret;
	}

	private final Entry<T> removeEntry(final T value) {
		Entry<T> last = null;

		final int index = (value.hashCode() & 0x7FFFFFFF) % elementData.length;
		Entry<T> entry = elementData[index];

		while (true) {
			if (entry == null)
				return null;

			if (eq(value, entry.get())) {
				if (last == null) {
					elementData[index] = entry.nextInSlot;
				} else {
					last.nextInSlot = entry.nextInSlot;
				}
				elementCount--;
				return entry;
			}

			last = entry;
			entry = entry.nextInSlot;
		}
	}

	/**
	 * Returns the number of elements in this set.
	 * 
	 * @return the number of elements in this set.
	 */
	public int size() {
		if (elementCount == 0)
			return 0;
		expungeStaleEntries();
		return elementCount;
	}

	// ========== Internal Entry

    //WeakSet中的条目继承了软引用. 软引用对象在不使用时会被GC回收
	private static final class Entry<T> extends WeakReference<T> {
		private final int hash;
		private Entry<T> nextInSlot;

		private Entry(final T value, ReferenceQueue<T> queue) {
			super(value, queue);
			hash = (value.hashCode() & 0x7FFFFFFF);
		}
	}

	// ========== Prime Finder

	private static final int primeSize(final int capacity) {
		return PrimeFinder.nextPrime(capacity);
	}

}
