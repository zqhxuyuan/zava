/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.coderplay.util.concurrent.queue;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A bounded {@linkplain BlockingQueue blocking queue} backed by a ring buffer.
 * Please note that the capacity of a instance of the queue (a.k.a. the buffer
 * size) should be a power of 2.
 *
 * 使用RingBuffer实现的有界阻塞队列.RingBuffer参考Disruptor.
 *
 * @author Min Zhou (coderplay@gmail.com)
 */
public class FastArrayBlockingQueue<E> extends AbstractQueue<E> implements
        BlockingQueue<E>, java.io.Serializable {

    private static final long serialVersionUID = -200258299566194572L;

    private final int indexMask;
    /** The queued items */
    private final E[] entries;

    /**
     * 为什么生产者是Claim索取策略. 消费者是Wait等待策略?
     * 消费者在消费消息时,如果生产者都没有生产消息,消费者就不能消费. 消费者是被动地等待!
     * 生产者生产消息,只要有可用的节点就开始生产. 它是主动地索取.
     *
     * **消费者等待消费消息, 生产者索取可用节点生产消息**
     */
    private final ClaimStrategy claimStrategy;
    private final WaitStrategy waitStrategy;

    //TODO ??
    //类似于访问队列的头指针和位置针. 消费者增加lowerCursor(head指针), 生产者消费upperCursor(tail指针)
    //lowerCursor到upperCursor区间的是队列中的数据???
    //lowerCursor是消费者消费的指针, upperCursor是生产者生产的最后一个消息的指针.
    private Sequence upperCursor = new Sequence(Constants.INITIAL_CURSOR_VALUE);
    private Sequence lowerCursor = new Sequence(Constants.INITIAL_CURSOR_VALUE);

    /**
     * Please note that the capacity of a instance of the queue
     * (a.k.a. the buffer size ) should be a power of 2.
     *
     * @param claimStrategy 生产者
     * @param waitStrategy 消费者
     */
    public FastArrayBlockingQueue(final ClaimStrategy claimStrategy, final WaitStrategy waitStrategy) {
        if (Integer.bitCount(claimStrategy.getBufferSize()) != 1) {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }

        this.claimStrategy = claimStrategy;
        this.waitStrategy = waitStrategy;
        //bufferSize的大小是2的幂次方.这样计算cursor在Buffer中的index直接通过&,而不是取模.
        //buffserSize=8,要计算cursor=12,其index=12&(8-1). 为什么要减一,因为8-1=7=0111
        this.indexMask = claimStrategy.getBufferSize() - 1;
        //队列的大小由生产者的Claim策略初始化时指定缓冲区的大小. 因为生产者负责往队列中写数据!
        this.entries = (E[]) new Object[claimStrategy.getBufferSize()];
    }

    //消费者
    @Override
    public E poll() {
        //队列为空,从队列poll出来的元素为null
        if (!waitStrategy.isEmpty(upperCursor))
            return null;

        //初始时upperCursor=-1,当调用一次offer,首先获取下一个可用节点的序列号nextSequence=0,
        //然后往buffer[0]写消息,同时会更新upperCursor=0. -->见offer方法
        //这里为什么也要传upperCursor. 和在offer方法中传lowerCursor一样.
        //nextSequence表示下一个要消费的序列号.如果能够获取到nextSequence
        long nextSequence = waitStrategy.incrementAndGet(upperCursor);

        //nextSequence就是消费者要消费的元素
        E e = entries[(int) nextSequence & indexMask];

        //更新lowerCursor的值为nextSequence. 表示消费者在nextSequence这个位置已经消费完了数据!
        //和生产者生产消息一样,只有生产者发起生产消息请求时,才会去获取下一个可用节点的序列号,如果下一个节点不可用,则等待.
        //消费者在消费消息时,也会首先获取下一个可以消费的节点的序列号.如果下一个节点不能被消费,则等待.

        //lowerCursor是消费者最后一次消费的位置.
        //这个lowerCursor会被用于生产者生产消息时判断是否可以获取下一个可用的节点.
        //当生产者下一个节点碰到lowerCursor的时候,则需要等待!
        waitStrategy.publish(nextSequence, lowerCursor);
        return e;
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException("This method is not supported yet.");
    }

    //生产者
    @Override
    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        //没有可用的节点
        if (!claimStrategy.hasRemaining(lowerCursor))
            return false;

        // obtain the next sequence of the queue for publishing
        //获取下一个序列号. 可以用来写的节点的序列号. 注意序列号和Buffer的index不同
        //序列号可以>BufferSize, 但是index的值只能是[0~BufferSize-1]
        //这里为什么传递**lowerCursor**. 这个游标是消费者的消费游标. 跟生产者生产消息有什么关系?
        //因为队列是环形缓冲区RingBuffer,要确保生产者即将写入的消息位置不能等于消费者要消费的游标位置!
        //如果生产者即将写入的位置=消费者消费的游标位置.生产者还要写的话,则会把新的数据覆盖掉消费者要消费的数据
        //这样消费者要消费数据时,会发现取到的数据不是之前的数据,而是被覆盖的了.这样会导致队列丢失掉一个消息!
        long nextSequence = claimStrategy.incrementAndGet(lowerCursor);

        // put e into the queue corresponding to the sequence
        //生产者往队列中添加数据,nextSequence是在正式写之前,使用ClaimStrategy发起请求获取下一个可用的节点的序列号
        //如果下一个节点不可用(比如有消费者等待消费这个节点的消息),则会一直等待,直到下一个节点可用,才会返回nextSequence.

        //使用indexMask的目的是当生产者写完了一圈后,如果开始位置可写,则覆盖开始位置继续写.
        //比如bufferSize=8,当0位置(index)可写,生产者写完sequence=7(写完一圈)后,
        //下一个要写入的序列号是nextSequence=7+1=8,其index=8&(8-1)=0,
        //因为index=0可写(消费者消费过了才可以),所以覆盖index=0为新写的消息.

        //注意:之前消费者一定消费过了index=0的消息,消息一旦消费过就没有用了.
        //如果一个生产者有多个消费者,每个消费者都要处理生产者发送的全部消息,
        //则只要有一个消费者在index=0没有消费完消息,生产者就不能继续写!只有在这个位置没有消费者等待消费才可以写!
        entries[(int) nextSequence & indexMask] = e;

        // publish element e
        //到这个地方了,生产者一定可以往nextSequence写数据,并且在上一句已经将nextSequence的位置进行了覆盖(当然之前没有数据就不叫覆盖)
        //则生产者要发布一个事件! 事件的内容是更新upperCursor的值为nextSequence.

        //所以upperCursor表示的是生产者最近写入的序列号. 而不是生产者下一个可用的序列号
        //获取下一个可用节点的序列号,只有在生产者发起生产消息请求时,才会去主动获取,然后往得到的nextSequence写入数据.
        //在生产完消息后,能做的就是更新upperCursor,即我最后写入的序列号是多少.

        //upperCursor是生产者最近写入的位置.
        //这个upperCursor会被用于消费者消费消息,获取下一个待消费的节点
        //如果消费者下一个要消费的节点碰到upperCursor,说明没有消息需要消费,消费者需要等待!
        claimStrategy.publish(nextSequence, upperCursor);
        return true;
    }

    @Override
    public void put(E e) throws InterruptedException {
        if (e == null)
            throw new NullPointerException();
        // obtain the next sequence of the queue for publishing
        long nextSequence = claimStrategy.incrementAndGetInterruptibly(lowerCursor);
        // put e into the queue corresponding to the sequence
        entries[(int) nextSequence & indexMask] = e;
        // publish element e
        claimStrategy.publishInterruptibly(nextSequence, upperCursor);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("This method is not supported yet.");
    }

    @Override
    public E take() throws InterruptedException {
        // obtain the next sequence of the queue for consuming
        long nextSequence = waitStrategy.incrementAndGetInterruptibly(upperCursor);
        // fetch element e from the queue corresponding to the sequence
        E e = entries[(int) nextSequence & indexMask];
        // consume element e
        waitStrategy.publishInterruptibly(nextSequence, lowerCursor);
        return e;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("This method is not supported yet.");
    }

    @Override
    public int remainingCapacity() {
        // Technically this method might return negative value
        long consumed = lowerCursor.get();
        long produced = upperCursor.get();
        return claimStrategy.getBufferSize() - (int) (produced - consumed);
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        throw new UnsupportedOperationException("" + "This method is not supported yet.");
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new UnsupportedOperationException("" + "This method is not supported yet.");
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("" + "This method is not supported yet.");
    }

    @Override
    public int size() {
        // Technically the returned size of this queue
        //队列的大小 = 生产者的序列号 - 消费者的序列号
        //注意:消费者的序列号不能大于生产者的序列号. 这里用序列号, 不能用index.
        //因为生产者的index可能小于消费者的index. 比如生产者的seq=12,index=2, 消费者的seq=3,index=3. 队列大小=12-3
        long consumed = lowerCursor.get();
        long produced = upperCursor.get();
        return (int) (produced - consumed);
    }

}
