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
package com.github.ggrandes.kvstore.structures.set;

import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Native Int SortedArray
 * This class is NOT Thread-Safe
 *
 * @author Guillermo Grandes / guillermo.grandes[at]gmail.com
 */
public class SortedIntArraySet {
	public static final int NULL_VALUE = Integer.MIN_VALUE;
	private static final Logger log = Logger.getLogger(SortedIntArraySet.class);
	//
	public int[] keys;
    //注意allocated的值和数组的索引的区别. 假设数组有5个元素, 则allocated=5, 数组最大的索引是4
	public int allocated = 0;  //已经分配的数量

    /**
	 * Create with initial size
	 * @param size
	 */
	public SortedIntArraySet(final int size) {
		allocArray(size);
	}

    /**
	 * Alloc array
	 * @param size
	 */
	private final void allocArray(final int size) {
		keys = new int[size];
	}

    /**
	 * Resize array
	 */
	private final void resizeArray() {
		if (log.isDebugEnabled())
			log.debug("resizeArray size=" + keys.length + " newsize=" + (keys.length << 1));
		final int[] newkeys = new int[keys.length << 1]; // double space

        //allocated表示数组的长度. 也就是arraycopy需要从源数组中复制的长度
		System.arraycopy(keys, 0, newkeys, 0, allocated);
		keys = newkeys;
	}

	/**
	 * Find slot by key 根据key寻找这个key将要存放的slot位置.
     * 因为Set是要有序的. 所以使用二分查找.
	 * @param searchKey 要查找的键
	 * @return 键在有序集合中的位置
	 */
	private final int findSlotByKey(final int searchKey) {
        //二分查找的from=0, end是数组的长度array.length, 而不是最后一个元素的index
		return Arrays.binarySearch(keys, 0, allocated, searchKey);
	}

    /**
	 * Is empty? 初始时allocated=0, 表示没有分配任何元素
	 * @return
	 */
	public boolean isEmpty() { // empty
		return (allocated <= 0);
	}
	/**
	 * Is full?
	 * @return
	 */
	private final boolean isFull() { // full
		if (log.isDebugEnabled())
			log.debug("allocated=" + allocated + " keys.length=" + keys.length);
		return (allocated >= keys.length);
	}
	/**
	 * Clear all elements
	 */
	public void clear() {
		Arrays.fill(keys, NULL_VALUE);
		allocated = 0;
	}
	/**
	 * insert element 插入一个元素
     * 往指定的位置插入一个元素, 这个指定位置的元素以及之后的元素都要后移一位
     *
     * 0  1  3  5  7  9
     *     ^2
     * findSlotByKey=2, srcPos=2
     *
     * Coppy(eles1, 2, eles1, 3, (allocated-srcPos))    allocated=6,srcPos=2,需要一定6-2=4位
     * eles1: 0 1 3 5 7 9      从eles1的第2个位置开始,复制到eles1的第3个位置开始,一共复制4位
     * eles1: 0 1 3 3 5 7 9
     *
     * move完后,要将srcPos的位置设置为要插入的元素.
	 */
	private final void moveElementsRight(final int[] elements, final int srcPos) {
		if (log.isDebugEnabled())
			log.debug("moveElementsRight(" + srcPos + ") allocated=" + allocated + ":" + keys.length + ":" + (allocated - srcPos) + ":" + (keys.length - srcPos - 1));
		System.arraycopy(elements, srcPos, elements, srcPos + 1, (allocated - srcPos));
	}

	/**
	 * remove element 删除一个元素
     * 0 1 2 3 5 7 9
     *     ×
     * findSlotByKey=2, srcPos=2,
     * 从当前要删除的位置的下一个位置开始,复制(allocated - srcPos - 1), 复制后的位置的开始位置是srcPos
	 */
	private final void moveElementsLeft(final int[] elements, final int srcPos) {
		if (log.isDebugEnabled())
			log.debug("moveElementsLeft(" + srcPos + ") allocated=" + allocated + ":" + keys.length + ":" + (allocated - srcPos - 1) + ":" + (keys.length - srcPos - 1));
		System.arraycopy(elements, srcPos + 1, elements, srcPos, (allocated - srcPos - 1));
	}

	/**
	 * remove key
	 * @param key
	 * @return
	 */
	public boolean remove(final int key) {
		int slot = findSlotByKey(key);
		if (slot >= 0) {
			return removeSlot(slot);
		}
        //如果slot<0, 表示没有找到key,就无法删除了!
		return false;
	}
    /**
     * remove slot
     * @param slot 实际上是数组的索引
     * @return
     */
    private final boolean removeSlot(final int slot) {
        if (slot < 0) {
            log.error("faking slot=" + slot + " allocated=" + allocated);
            return false;
        }
        if (slot < allocated) {
            moveElementsLeft(keys, slot);
        }
        //数组的容量减1
        if (allocated > 0) allocated--;
        if (log.isDebugEnabled())
            log.debug("erased up key=" + keys[allocated]);

        //现在allocated实际上指向还没删除前的最后一个元素的索引. 因为allocated--, 即allocated=array.length-1
        //将要删除的元素设置为null
        keys[allocated] = NULL_VALUE;
        return true;
    }

	/**
	 * put key
	 * @param key
	 * @return
	 */
	public boolean put(final int key) {
		if (isFull()) { // full
			resizeArray();
		}
		int slot = findSlotByKey(key);
        //二分查找,找到key的话,slot>0
		if (slot >= 0) {
			if (log.isDebugEnabled())
				log.debug("key already exists: " + key);
			return false; // key already exist
		}
        //如果没有找到key的话,返回的是一个负数
		slot = ((-slot) - 1);
		return addSlot(slot, key);
	}
	/**
	 * add slot 往指定的位置添加一个key
	 * @param slot
	 * @param key
	 * @return
	 */
	private final boolean addSlot(final int slot, final int key) {
		if (slot < allocated) {
			moveElementsRight(keys, slot);
		}
		allocated++;
		keys[slot] = key;
		return true;
	}

	/**
	 * Returns the first (lowest) element currently in this set.
	 */
	public int first() {
		return keys[0];
	}
	/**
	 * Returns the last (highest) element currently in this set.
	 */
	public int last() {
		if (allocated == 0)
			return NULL_VALUE;
        //数组的容量-1, 就是数组已填充的最后一个元素的索引
		return keys[allocated-1];
	}
	/**
	 * Returns the greatest element in this set less than or equal to the given element, or NULL_VALUE if there is no such element.	
	 * @param key
	 */
	public int floor(final int key) {
		return getRoundKey(key, false, true);
	}
	/**
	 * Returns the least element in this set greater than or equal to the given element, or NULL_VALUE if there is no such element.
	 * @param key
	 */
	public int ceiling(final int key) {
		return getRoundKey(key, true, true);
	}
	/**
	 * Returns the greatest element in this set strictly less than the given element, or NULL_VALUE if there is no such element.
	 * @param key
	 */
	public int lower(final int key) {
		return getRoundKey(key, false, false);
	}
	/**
	 * Returns the least element in this set strictly greater than the given element, or NULL_VALUE if there is no such element.
	 * @param key
	 */
	public int higher(final int key) {
		return getRoundKey(key, true, false);
	}
	/**
	 * find key
	 * @param key
	 * @param upORdown
	 * @param acceptEqual
	 * @return
	 */
	private final int getRoundKey(final int key, final boolean upORdown, final boolean acceptEqual) {
		if (isEmpty()) return NULL_VALUE;
		int slot = findSlotByKey(key);
		if (upORdown) {
			// ceiling / higher
			slot = ((slot < 0) ? (-slot)-1 : (acceptEqual ? slot : slot+1));
			if (slot >= allocated) {
				return NULL_VALUE;
			}
		}
		else {
			// floor / lower
			slot = ((slot < 0) ? (-slot)-2 : (acceptEqual ? slot : slot-1));
			if (slot < 0) {
				return NULL_VALUE;
			}
		}
		return keys[slot];
	}	
	/**
	 * Returns true if this set contains the specified element.
	 * @param key
	 * @return
	 */
	public boolean contains(final int key) {
		int slot = findSlotByKey(key);
		return (slot >= 0);
	}
	/**
	 * Returns the element at the specified position in his internal array.
	 * @param slot
	 * @return
	 */
	public int get(final int slot) {
		if ((slot < 0) || (slot >= allocated))
			return NULL_VALUE;
		return keys[slot];
	}
	/**
	 * Returns the number of elements in this set (its cardinality).
	 * @return
	 */
	public int size() {
		return allocated;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < allocated; i++) {
			final int k = keys[i];
			sb.append(k).append("|");
		}
		if (allocated > 0)
			sb.setLength(sb.length() - 1);
		sb.append("]");
		return sb.toString();
	}

	public IntIterator iterator() {
		return new IntIterator(this);
	}

	static class IntIterator {
		final SortedIntArraySet associatedSet;
		int nextEntry = 0;
		int lastReturned = -1;

		public IntIterator(final SortedIntArraySet associatedSet) {
			this.associatedSet = associatedSet;
			nextEntry = 0;
		}

		public boolean hasNext() {
			return (nextEntry < associatedSet.allocated);
		}

		public void remove() {
			if (lastReturned == -1)
				throw new IllegalStateException();

			associatedSet.removeSlot(lastReturned);
			lastReturned = -1;
		}

		public int next() {
			lastReturned = nextEntry;
			return associatedSet.keys[nextEntry++];
		}
	}

	/**
	 * Test
	 * @param args
	 */
	public static void main(final String[] args) {
		SortedIntArraySet s = new SortedIntArraySet(3);
		s.put(90);
		s.put(10);
		s.put(20);
		s.put(30);
		System.out.println("toString()=" + s.toString());
		s.remove(10);
		s.put(40);
		System.out.println("toString()=" + s.toString());
		System.out.println("first=" + s.first());
		System.out.println("last()=" + s.last());
		System.out.println("floor(15)=" + s.floor(15));
		System.out.println("ceiling(15)=" + s.ceiling(15));
		System.out.println("lower(15)=" + s.lower(15));
		System.out.println("higher(15)=" + s.higher(15));
		System.out.println("floor(20)=" + s.floor(20));
		System.out.println("ceiling(20)=" + s.ceiling(20));
		System.out.println("lower(20)=" + s.lower(20));
		System.out.println("higher(20)=" + s.higher(20));
		System.out.println("floor(0)=" + s.floor(0));
		System.out.println("ceiling(0)=" + s.ceiling(0));
		System.out.println("lower(0)=" + s.lower(0));
		System.out.println("higher(0)=" + s.higher(0));
		System.out.println("floor(9999)=" + s.floor(9999));
		System.out.println("ceiling(9999)=" + s.ceiling(9999));
		System.out.println("lower(9999)=" + s.lower(9999));
		System.out.println("higher(9999)=" + s.higher(9999));
		System.out.println("contains(20)=" + s.contains(20));
		System.out.println("contains(9999)=" + s.contains(9999));
		System.out.println("size()=" + s.size());
		System.out.println("-------- iter begin");
		IntIterator iter = s.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
		System.out.println("-------- iter end");
		System.out.println("-------- for begin");
		for (int i = -1; i <= s.size(); i++) {
			System.out.println(s.get(i));
		}
		System.out.println("-------- for end");
		s.clear();
		System.out.println("first=" + s.first());
		System.out.println("last()=" + s.last());
	}

}
