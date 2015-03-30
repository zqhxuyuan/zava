/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.coderplay.javaopt.queue;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * <ul>
 * <li>Lock free, observing single writer principal.
 * </ul>
 */
public final class P1C1QueueStep0<E> implements Queue<E> {
  private final E[] buffer;

  //采用头指针和尾指针来划定范围:队列中未消费的元素
  //如果没有用2个指针,而是用数组的索引来表示队列. 要么在生产数据时要么在消费数据时,都会移动数组中的其他元素.
  //假设生产数据往填充的数据后追加元素,在消费数据时,当从数组左端开始消费数据,则删除数组的第一元素,后面的元素都要左移一位.
  //你可能会说我消费数据时也从已经填充的数组最后一个开始消费.这样就不需要移动了啊.首先这个没有满足FIFO.
  //其次,如果在读数据的同时发生写数据.导致都是操作数组的填充数据的最后一位.
  private volatile long tail = 0;
  private volatile long head = 0;

  @SuppressWarnings("unchecked")
  public P1C1QueueStep0(final int capacity) {
    buffer = (E[]) new Object[capacity];
  }

  public boolean add(final E e) {
    if (offer(e)) {
      return true;
    }

    throw new IllegalStateException("Queue is full");
  }

  public boolean offer(final E e) {
    if (null == e) {
      throw new NullPointerException("Null is not a valid element");
    }

    final long currentTail = tail;
    final long wrapPoint = currentTail - buffer.length;
    //head<=currentTail - buffer.length
    //currentTail-head >= bufer.length, false
    //currentTail-head < buffer.length, true

    //|<---------buffer.length--------->|
    //|---------------------------------|
    //|head                  |currentTail
    //|<--currentTail-head-->|

    //在添加元素之前,如果tail指针到达队列的大小,则不能往队列里添加元素
    if (head <= wrapPoint) {
      return false;
    }

    //在缓冲区的currentTail添加元素
    buffer[(int) (currentTail % buffer.length)] = e;
    //tail指针右移一位. 一开始时tail=0,表示下次写数据时往数组的第0个位置写(第一个元素)
    //当添加一共元素后,tail=1,表示下次写的时候往数组的第一个位置写(第二个元素)
    tail = currentTail + 1;

    return true;
  }

  public E poll() {
    final long currentHead = head;
    //currentHead不能等于tail,比如初始化时,currentHead=head=0,tail=0,这时候队列中没有元素,也就不能poll
    if (currentHead >= tail) {
      return null;
    }

    //先确定在数组中的位置
    final int index = (int) (currentHead % buffer.length);
    //通过数组索引获取元素
    final E e = buffer[index];
    //内存回收
    buffer[index] = null;
    //读指针右移一位,指向下次读的位置. 和写一样:tail指向下次写的位置
    head = currentHead + 1;

    return e;
  }

  //删除元素和poll一样
  public E remove() {
    final E e = poll();
    if (null == e) {
      throw new NoSuchElementException("Queue is empty");
    }

    return e;
  }

  //获取队列的第一个元素
  public E element() {
    final E e = peek();
    if (null == e) {
      throw new NoSuchElementException("Queue is empty");
    }

    return e;
  }

  //peek并不删除元素
  public E peek() {
    return buffer[(int) (head % buffer.length)];
  }

  //队列是由头指针和尾指针维护的. 所以队列的大小=尾指针的位置-头指针的位置
  public int size() {
    return (int) (tail - head);
  }

  //tail是往队列中写数据+1,head是消费队列中的数据+1. 当两者指针相遇时,队列中没有需要消费的数据了,队列为空
  public boolean isEmpty() {
    return tail == head;
  }

  public boolean contains(final Object o) {
    if (null == o) {
      return false;
    }

    //队列中是否包含某个元素. 这个元素必须没有被消费过. 即如果已经被消费过,这个元素会被poll出去.就不存在在数组中了
    //head指针到tail指针之间的是未消费的数据. 循环的结束位置并没有到tail的位置. 因为一旦head=tail,则队列为空!
    //注意:即使你想从数组的0开始,但是因为head之前的在被poll出去后,那个位置被置为null了!所以你想查,也查不到被消费过的数据了!
    for (long i = head, limit = tail; i < limit; i++) {
      final E e = buffer[(int) (i % buffer.length)];
      if (o.equals(e)) {
        return true;
      }
    }

    return false;
  }

  public Iterator<E> iterator() {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  public <T> T[] toArray(final T[] a) {
    throw new UnsupportedOperationException();
  }

  public boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  public boolean containsAll(final Collection<?> c) {
    for (final Object o : c) {
      if (!contains(o)) {
        return false;
      }
    }

    return true;
  }

  public boolean addAll(final Collection<? extends E> c) {
    for (final E e : c) {
      add(e);
    }

    return true;
  }

  public boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    Object value;
    do {
      value = poll();
    } while (null != value);
  }
}
