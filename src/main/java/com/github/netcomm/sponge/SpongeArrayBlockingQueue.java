package com.github.netcomm.sponge;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.netcomm.sponge.util.DataByteArrayOutputStream;
import com.github.netcomm.sponge.util.Utilities;

/**
 * A bounded {@linkplain BlockingQueue blocking queue} backed by an
 * array.  This queue orders elements FIFO (first-in-first-out).  The
 * <em>head</em> of the queue is that element that has been on the
 * queue the longest time.  The <em>tail</em> of the queue is that
 * element that has been on the queue the shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue.
 * <p>
 * <p>This is a classic &quot;bounded buffer&quot;, in which a
 * fixed-sized array holds elements inserted by producers and
 * extracted by consumers.  Once created, the capacity cannot be
 * increased.  Attempts to <tt>put</tt> an element into a full queue
 * will result in the operation blocking; attempts to <tt>take</tt> an
 * element from an empty queue will similarly block.
 * <p>
 * <p> This class supports an optional fairness policy for ordering
 * waiting producer and consumer threads.  By default, this ordering
 * is not guaranteed. However, a queue constructed with fairness set
 * to <tt>true</tt> grants threads access in FIFO order. Fairness
 * generally decreases throughput but reduces variability and avoids
 * starvation.
 * <p>
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link
 * Iterator} interfaces.
 * <p>
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements held in this collection
 * @author Doug Lea
 * @since 1.5
 */
public class SpongeArrayBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    /**
     * Serialization ID. This class relies on default serialization
     * even for the items array, which is default-serialized, even if
     * it is empty. Otherwise it could not be declared final, which is
     * necessary here.
     */
    private static final long serialVersionUID = -817911632652898426L;

    /**
     * The queued items
     */
    private final E[] items;
    /**
     * items index for next take, poll or remove
     */
    private int takeIndex;
    /**
     * items index for next put, offer, or add.
     */
    private int putIndex;
    /**
     * Number of items in the queue
     */
    private int count;

    /*
     * Concurrency control uses the classic two-condition algorithm
     * found in any textbook.
     */

    /**
     * Main lock guarding all access
     */
    private final ReentrantLock lock;
    /**
     * Condition for waiting takes
     */
    private final Condition notEmpty;
    /**
     * Condition for waiting puts
     */
    private final Condition notFull;

    /**
     * 是否启动持久化模式
     */
    private boolean isInPersistence = false;
    private MemoryItemList theMemoryItemList;

    // Internal helper methods

    /**
     * Circularly increment i.
     */
    final int inc(int i) {
        return (++i == items.length) ? 0 : i;
    }

    /**
     * Inserts element at current put position, advances, and signals.
     * Call only when holding lock.
     */
    private void insert(E x) {
        items[putIndex] = x;
        putIndex = inc(putIndex);
        ++count;
        notEmpty.signal();
    }

    /**
     * Extracts element at current take position, advances, and signals.
     * Call only when holding lock.
     */
    private E extract() {
        final E[] items = this.items;
        E x = items[takeIndex];
        items[takeIndex] = null;
        takeIndex = inc(takeIndex);
        --count;
        notFull.signal();
        return x;
    }

    /**
     * Utility for remove and iterator.remove: Delete item at position i.
     * Call only when holding lock.
     */
    void removeAt(int i) {
        final E[] items = this.items;
        // if removing front item, just advance
        if (i == takeIndex) {
            items[takeIndex] = null;
            takeIndex = inc(takeIndex);
        } else {
            // slide over all others up through putIndex.
            for (; ; ) {
                int nexti = inc(i);
                if (nexti != putIndex) {
                    items[i] = items[nexti];
                    i = nexti;
                } else {
                    items[i] = null;
                    putIndex = i;
                    break;
                }
            }
        }
        --count;
        notFull.signal();
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed)
     * capacity and default access policy.
     *
     * @param capacity the capacity of this queue 队列的容量
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than 1
     */
    public SpongeArrayBlockingQueue(int capacity, int oneBatchSzParm,
                                    SpongeService theSpongeServiceParm) throws Exception {
        if (oneBatchSzParm >= capacity) {
            throw new SpongeException("一次批量持久化大小不能大于队列容量");
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.items = (E[]) new Object[capacity]; //队列是用数组实现的,数组的容量
        lock = new ReentrantLock(false);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();

        //内存的任务项
        theMemoryItemList = new MemoryItemList(oneBatchSzParm, theSpongeServiceParm);
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed)
     * capacity and the specified access policy.
     *
     * @param capacity the capacity of this queue
     * @param fair     if <tt>true</tt> then queue accesses for threads blocked
     *                 on insertion or removal, are processed in FIFO order;
     *                 if <tt>false</tt> the access order is unspecified.
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than 1
     */
    public SpongeArrayBlockingQueue(int capacity, boolean fair,
                                    int oneBatchSzParm,
                                    SpongeService theSpongeServiceParm) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.items = (E[]) new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();

        theMemoryItemList = new MemoryItemList(oneBatchSzParm, theSpongeServiceParm);
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed)
     * capacity, the specified access policy and initially containing the
     * elements of the given collection,
     * added in traversal order of the collection's iterator.
     *
     * @param capacity the capacity of this queue
     * @param fair     if <tt>true</tt> then queue accesses for threads blocked
     *                 on insertion or removal, are processed in FIFO order;
     *                 if <tt>false</tt> the access order is unspecified.
     * @param c        the collection of elements to initially contain
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than
     *                                  <tt>c.size()</tt>, or less than 1.
     * @throws NullPointerException     if the specified collection or any
     *                                  of its elements are null
     */
    public SpongeArrayBlockingQueue(int capacity, boolean fair,
                                    Collection<? extends E> c,
                                    int oneBatchSzParm,
                                    SpongeService theSpongeServiceParm) {
        this(capacity, fair, oneBatchSzParm, theSpongeServiceParm);
        if (capacity < c.size())
            throw new IllegalArgumentException();

        for (Iterator<? extends E> it = c.iterator(); it.hasNext(); )
            add(it.next());
    }

    /**
     * Inserts the specified element at the tail of this queue if it is
     * possible to do so immediately without exceeding the queue's capacity,
     * returning <tt>true</tt> upon success and throwing an
     * <tt>IllegalStateException</tt> if this queue is full.
     *
     * @param e the element to add
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws IllegalStateException if this queue is full
     * @throws NullPointerException  if the specified element is null
     */
    public boolean add(E e) {
        return super.add(e);
    }

    /**
     * Inserts the specified element at the tail of this queue if it is
     * possible to do so immediately without exceeding the queue's capacity,
     * returning <tt>true</tt> upon success and <tt>false</tt> if this queue
     * is full.  This method is generally preferable to method {@link #add},
     * which can fail to insert an element only by throwing an exception.
     *
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            // 如满足下面条件,则进行持久化保存
            if (count == items.length || isInPersistence == true) {
                isInPersistence = true;
                theMemoryItemList.addOneRequest(e);
                return true;
            } else {
                insert(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the tail of this queue, waiting
     * for space to become available if the queue is full.
     *
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        final E[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            try {
                while (count == items.length)
                    notFull.await();
            } catch (InterruptedException ie) {
                notFull.signal(); // propagate to non-interrupted thread
                throw ie;
            }
            insert(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the tail of this queue, waiting
     * up to the specified wait time for space to become available if
     * the queue is full.
     *
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException {

        if (e == null) throw new NullPointerException();
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (; ; ) {
                if (count != items.length) {
                    insert(e);
                    return true;
                }
                if (nanos <= 0)
                    return false;
                try {
                    nanos = notFull.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    notFull.signal(); // propagate to non-interrupted thread
                    throw ie;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count == 0)
                return null;
            E x = extract();
            return x;
        } finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            try {
                // 如果当前内存中的任务数等于0，则从缓冲池里获取数据
                if (count == 0) {
                    theMemoryItemList.fetchData();
                }

                while (count == 0)
                    notEmpty.await();
            } catch (InterruptedException ie) {
                notEmpty.signal(); // propagate to non-interrupted thread
                throw ie;
            }
            E x = extract();
            return x;
        } finally {
            lock.unlock();
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (; ; ) {
                if (count != 0) {
                    E x = extract();
                    return x;
                }

                if (nanos <= 0)
                    return null;
                try {
                    nanos = notEmpty.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    notEmpty.signal(); // propagate to non-interrupted thread
                    throw ie;
                }

            }
        } finally {
            lock.unlock();
        }
    }

    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (count == 0) ? null : items[takeIndex];
        } finally {
            lock.unlock();
        }
    }

    // this doc comment is overridden to remove the reference to collections
    // greater in size than Integer.MAX_VALUE

    /**
     * Returns the number of elements in this queue.
     *
     * @return the number of elements in this queue
     */
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    // this doc comment is a modified copy of the inherited doc comment,
    // without the reference to unlimited queues.

    /**
     * Returns the number of additional elements that this queue can ideally
     * (in the absence of memory or resource constraints) accept without
     * blocking. This is always equal to the initial capacity of this queue
     * less the current <tt>size</tt> of this queue.
     * <p>
     * <p>Note that you <em>cannot</em> always tell if an attempt to insert
     * an element will succeed by inspecting <tt>remainingCapacity</tt>
     * because it may be the case that another thread is about to
     * insert or remove an element.
     */
    public int remainingCapacity() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return items.length - count;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present.  More formally, removes an element <tt>e</tt> such
     * that <tt>o.equals(e)</tt>, if this queue contains one or more such
     * elements.
     * Returns <tt>true</tt> if this queue contained the specified element
     * (or equivalently, if this queue changed as a result of the call).
     *
     * @param o element to be removed from this queue, if present
     * @return <tt>true</tt> if this queue changed as a result of the call
     */
    public boolean remove(Object o) {
        if (o == null) return false;
        final E[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = takeIndex;
            int k = 0;
            for (; ; ) {
                if (k++ >= count)
                    return false;
                if (o.equals(items[i])) {
                    removeAt(i);
                    return true;
                }
                i = inc(i);
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns <tt>true</tt> if this queue contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this queue contains
     * at least one element <tt>e</tt> such that <tt>o.equals(e)</tt>.
     *
     * @param o object to be checked for containment in this queue
     * @return <tt>true</tt> if this queue contains the specified element
     */
    public boolean contains(Object o) {
        if (o == null) return false;
        final E[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = takeIndex;
            int k = 0;
            while (k++ < count) {
                if (o.equals(items[i]))
                    return true;
                i = inc(i);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns an array containing all of the elements in this queue, in
     * proper sequence.
     * <p>
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this queue.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     * <p>
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray() {
        final E[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] a = new Object[count];
            int k = 0;
            int i = takeIndex;
            while (k < count) {
                a[k++] = items[i];
                i = inc(i);
            }
            return a;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns an array containing all of the elements in this queue, in
     * proper sequence; the runtime type of the returned array is that of
     * the specified array.  If the queue fits in the specified array, it
     * is returned therein.  Otherwise, a new array is allocated with the
     * runtime type of the specified array and the size of this queue.
     * <p>
     * <p>If this queue fits in the specified array with room to spare
     * (i.e., the array has more elements than this queue), the element in
     * the array immediately following the end of the queue is set to
     * <tt>null</tt>.
     * <p>
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     * <p>
     * <p>Suppose <tt>x</tt> is a queue known to contain only strings.
     * The following code can be used to dump the queue into a newly
     * allocated array of <tt>String</tt>:
     * <p>
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     *
     * @param a the array into which the elements of the queue are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException  if the runtime type of the specified array
     *                              is not a supertype of the runtime type of every element in
     *                              this queue
     * @throws NullPointerException if the specified array is null
     */
    public <T> T[] toArray(T[] a) {
        final E[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (a.length < count)
                a = (T[]) java.lang.reflect.Array.newInstance(
                        a.getClass().getComponentType(),
                        count
                );

            int k = 0;
            int i = takeIndex;
            while (k < count) {
                a[k++] = (T) items[i];
                i = inc(i);
            }
            if (a.length > count)
                a[count] = null;
            return a;
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.toString();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Atomically removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     */
    public void clear() {
        final E[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = takeIndex;
            int k = count;
            while (k-- > 0) {
                items[i] = null;
                i = inc(i);
            }
            count = 0;
            putIndex = 0;
            takeIndex = 0;
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    public int drainTo(Collection<? super E> c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        final E[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = takeIndex;
            int n = 0;
            int max = count;
            while (n < max) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
                ++n;
            }
            if (n > 0) {
                count = 0;
                putIndex = 0;
                takeIndex = 0;
                notFull.signalAll();
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        if (maxElements <= 0)
            return 0;
        final E[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = takeIndex;
            int n = 0;
            int sz = count;
            int max = (maxElements < count) ? maxElements : count;
            while (n < max) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
                ++n;
            }
            if (n > 0) {
                count -= n;
                takeIndex = i;
                notFull.signalAll();
            }
            return n;
        } finally {
            lock.unlock();
        }
    }


    /**
     * Returns an iterator over the elements in this queue in proper sequence.
     * The returned <tt>Iterator</tt> is a "weakly consistent" iterator that
     * will never throw {@link ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     *
     * @return an iterator over the elements in this queue in proper sequence
     */
    public Iterator<E> iterator() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return new Itr();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Iterator for ArrayBlockingQueue
     */
    private class Itr implements Iterator<E> {
        /**
         * Index of element to be returned by next,
         * or a negative number if no such.
         */
        private int nextIndex;

        /**
         * nextItem holds on to item fields because once we claim
         * that an element exists in hasNext(), we must return it in
         * the following next() call even if it was in the process of
         * being removed when hasNext() was called.
         */
        private E nextItem;

        /**
         * Index of element returned by most recent call to next.
         * Reset to -1 if this element is deleted by a call to remove.
         */
        private int lastRet;

        Itr() {
            lastRet = -1;
            if (count == 0)
                nextIndex = -1;
            else {
                nextIndex = takeIndex;
                nextItem = items[takeIndex];
            }
        }

        public boolean hasNext() {
            /*
             * No sync. We can return true by mistake here
             * only if this iterator passed across threads,
             * which we don't support anyway.
             */
            return nextIndex >= 0;
        }

        /**
         * Checks whether nextIndex is valid; if so setting nextItem.
         * Stops iterator when either hits putIndex or sees null item.
         */
        private void checkNext() {
            if (nextIndex == putIndex) {
                nextIndex = -1;
                nextItem = null;
            } else {
                nextItem = items[nextIndex];
                if (nextItem == null)
                    nextIndex = -1;
            }
        }

        public E next() {
            final ReentrantLock lock = SpongeArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (nextIndex < 0)
                    throw new NoSuchElementException();
                lastRet = nextIndex;
                E x = nextItem;
                nextIndex = inc(nextIndex);
                checkNext();
                return x;
            } finally {
                lock.unlock();
            }
        }

        public void remove() {
            final ReentrantLock lock = SpongeArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                int i = lastRet;
                if (i == -1)
                    throw new IllegalStateException();
                lastRet = -1;

                int ti = takeIndex;
                removeAt(i);
                // back up cursor (reset to front if was first element)
                nextIndex = (i == ti) ? takeIndex : i;
                checkNext();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 启动初始化时调用，看是否有没被消费的持久化任务
     *
     * @param theThreadPoolExecutorInsParm
     */
    public void doFetchData_init(ThreadPoolExecutor theThreadPoolExecutorInsParm) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            theMemoryItemList.fetchData_init(theThreadPoolExecutorInsParm);
        } finally {
            lock.unlock();
        }
    }

    // 内存数据
    class MemoryItemList {
        //一次批处理的数量
        private int oneBatchSz = 100;
        //用来保存阻塞队列满了之后的数据
        private Object[] itemArray;
        //计数器用来判断是否超过批处理的数据量,超过时,开始写磁盘
        private int count = 0;
        //服务类,用于获取相关持久层实现类,比如文件的实现方式
        private SpongeService theSpongeService;
        //字节输出流,默认1MB
        private DataByteArrayOutputStream theBytesOut = new DataByteArrayOutputStream(1 * 1024 * 1024);

        protected MemoryItemList(int oneBatchSzParm, SpongeService theSpongeServiceParm) {
            oneBatchSz = oneBatchSzParm;
            itemArray = new Object[oneBatchSz];
            theSpongeService = theSpongeServiceParm;
        }

        //添加一个请求. 在往阻塞队列中添加元素满了之后,会调用该方法暂时保存在一个临时数组中
        protected boolean addOneRequest(Object requestParm) {
            itemArray[count] = requestParm;
            count++;
            //满足进行一次批处理的条件,则将内存中临时数组的数据写入到文件中
            if (count >= oneBatchSz) {
                generateOneBatchBytes();
            }
            return true;
        }

        //生成一批数据
        private void generateOneBatchBytes() {
            theBytesOut.reset(); //position=0
            theBytesOut.setSize(6); //position的位置=6
            //从第6个字节开始填充数据
            try {
                //把临时数组中的数据都写到输出流中
                //写入数组中每个元素时,先写入这个元素占用的长度,然后才写入元素的值
                for (int i = 0; i < count; i++) {
                    //将数组对象转成字节数组.
                    byte[] tmpBytes = JSON.toJSONBytes(itemArray[i], SerializerFeature.WriteClassName);
                    //先写元素的长度
                    theBytesOut.write(Utilities.getBytesFromInt(tmpBytes.length));
                    //再写元素的字节数据
                    theBytesOut.write(tmpBytes);
                }

                byte[] tmpData = theBytesOut.getData();
                //在最开始将position设置为6, 现在开始填充前面6个字节
                // magic便签
                tmpData[0] = 7;
                tmpData[1] = 7;
                //int有4个字节,加上前面的2个字节,刚好是6个字节.
                //第二个字节后,写入的是整个batch的大小. 而前面for循环里的长度则是数组中每个元素自己的长度.
                Utilities.setBytesFromInt(theBytesOut.size(), tmpData, 2);

                //调用持久层实现类,将一批数据追加到文件中.
                //注意: theBytesOut跟具体持久化实现没有关系, 它本身是在内存中的二进制字节输出流.
                theSpongeService.getThePersistence().addOneBatchBytes(theBytesOut.getDataClone());
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                count = 0;
            }
        }

        //初始化时看看有没有需要消费的数据.
        private void fetchData_init(ThreadPoolExecutor theThreadPoolExecutorInsParm) {
            //有需要消费的数据
            if (theSpongeService.getThePersistence().isHaveDataInPersistence() == true) {
                try {
                    //取一批数据
                    byte[] tmpBytes = theSpongeService.getThePersistence().fetchOneBatchBytes();
                    int tmpLoadCnt = 0;
                    if (tmpBytes != null) {
                        long tmpStartTime = System.currentTimeMillis();
                        //前面6个字节是全局的
                        int tmpCurPosi = 6;
                        //读取一整批数据,其中写入数组时的每一项条目,都会被取出来作为任务执行
                        while (tmpCurPosi < tmpBytes.length) {
                            //写入的是数组,数组的每个元素首先写入长度,然后写入元素的值
                            int tmpOneItemLength = Utilities.getIntFromBytes(tmpBytes, tmpCurPosi);
                            tmpCurPosi += 4;
                            //字节数组的长度,创建对应长度的字节数组来存放里面的值
                            byte[] tmpOneItemBytes = new byte[tmpOneItemLength];
                            //数组中的每一项
                            System.arraycopy(tmpBytes, tmpCurPosi, tmpOneItemBytes, 0, tmpOneItemLength);
                            //还原/反序列化成任务
                            E tmpOneItem = (E) JSON.parse(tmpOneItemBytes);
                            //执行任务,我们取出任务的目的是为了执行它,就像从队列中取出任务也是执行任务
                            theThreadPoolExecutorInsParm.execute((Runnable) tmpOneItem);
                            //右移偏移量,处理数组的下一个元素
                            tmpCurPosi += tmpOneItemLength;
                            tmpLoadCnt ++;
                        }
                        //只要内存或磁盘中有数据,说明在持久化的状态. 即队列的负荷已经超过capacity了.
                        isInPersistence = true;
                        System.out.println(tmpLoadCnt + "条, 装载耗时 "+(System.currentTimeMillis() - tmpStartTime));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        //当队列为空时,看看内存或磁盘中有没有需要消费的数据
        //在往队列中添加任务时,当队列满了后,添加到内存以及持久化到磁盘后
        //如果这时要消费任务,首先还是从队列中取出任务进行执行, 只有当队列为空时,才去看看内存和磁盘中还有没有任务.
        //这也复合了FIFO的队列. 因为任务首先进入队列中,所以消费的时候可能也是先从队列中消费.
        private boolean fetchData() {
            boolean retBool = false;
            try {
                if (isInPersistence == true) {
                    //-----下面的代码和fetchData_init一模一样,不过它判断的条件是isInPersistence
                    //只有有持久化,才需要从持久化文件或者内存中读取.否则直接从队列中就可以读取
                    byte[] tmpBytes = theSpongeService.getThePersistence().fetchOneBatchBytes();
                    int tmpLoadCnt = 0;
                    if (tmpBytes != null) {
                        long tmpStartTime = System.currentTimeMillis();
                        int tmpCurPosi = 6;
                        while (tmpCurPosi < tmpBytes.length) {
                            int tmpOneItemLength = Utilities.getIntFromBytes(tmpBytes, tmpCurPosi);
                            tmpCurPosi += 4;
                            byte[] tmpOneItemBytes = new byte[tmpOneItemLength];
                            System.arraycopy(tmpBytes, tmpCurPosi, tmpOneItemBytes, 0, tmpOneItemLength);
                            E tmpOneItem = (E) JSON.parse(tmpOneItemBytes);
                            insert(tmpOneItem);
                            tmpCurPosi += tmpOneItemLength;
                            tmpLoadCnt ++;
                        }
                        System.out.println(tmpLoadCnt + "条, 装载耗时 "+(System.currentTimeMillis() - tmpStartTime));
                        retBool = true;
                    }

                    //没有需要消费的数据
                    if (tmpBytes == null) {
                        if (count > 0) {
                            //把内存中的数据放到队列中
                            for (int i = 0; i < count; i++) {
                                insert((E) itemArray[i]);
                            }
                            count = 0;
                            retBool = true;
                        }
                    }

                    //如果内存中和持久化文件都没有需要消费的数据,说明内存和持久化文件中的任务都执行完毕了.
                    if (retBool == false) {
                        System.out.println("没有持久化的任务需要处理了,队列模式切回到正常内存模式!!!");
                        isInPersistence = false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return retBool;
        }

        public int getOneBatchSz() {
            return oneBatchSz;
        }

        public void setOneBatchSz(int oneBatchSz) {
            this.oneBatchSz = oneBatchSz;
        }
    }
}