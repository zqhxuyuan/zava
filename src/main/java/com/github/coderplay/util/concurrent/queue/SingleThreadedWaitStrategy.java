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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Single-threaded consumer {@link WaitStrategy}.
 * @author Min Zhou (coderplay@gmail.com)
 */
public class SingleThreadedWaitStrategy implements WaitStrategy {

    private static final int SPIN_TRIES = 100;

    private final PaddedLong minGatingSequence = new PaddedLong(Constants.INITIAL_CURSOR_VALUE);
    private final PaddedLong waitSequence = new PaddedLong(Constants.INITIAL_CURSOR_VALUE);

    @Override
    public void signalAllWhenBlocking() {
        // do nothing
    }

    @Override
    public long incrementAndGet(final Sequence upperCursor) {
        //取出当前的序列号+1
        final long nextSequence = waitSequence.get() + 1L;
        //将新的值设置为最新值
        waitSequence.set(nextSequence);

        //等待空闲Slot: 获取到下一个可以消费的节点的序列号. 如果没有可以消费的节点,则要等待
        //如果这个方法没有等待,则最后返回的nextSequence表示消费者下一个可以消费的节点的序列号.
        waitForFreeSlotAt(nextSequence, upperCursor);
        return nextSequence;
    }

    //http://ifeve.com/dissecting_the_disruptor_how_doi_read_from_the_ring_buffer/
    //每个消费者都需要找到下一个它要访问的序号.消费者处理完了Ring Buffer里序号8之前（包括8）的所有数据，那么它期待访问的下一个序号是9
    //消费者可以调用ConsumerBarrier对象的waitFor()方法，传递它所需要的下一个序号
    //final long availableSeq = consumerBarrier.waitFor(nextSequence); //nextSequence=9, 返回的availableSeq=12
    //ConsumerBarrier返回RingBuffer的最大可访问序号。ConsumerBarrier有一个WaitStrategy方法来决定它如何等待这个序号

    //接下来，消费者会一直原地停留，等待更多数据被写入Ring Buffer。并且，一旦数据写入后消费者会收到通知——节点9，10，11和12 已写入。
    //现在序号12到了，消费者可以让ConsumerBarrier去拿这些序号节点里的数据了。
    private boolean waitForFreeSlotAt(final long nextSequence, final Sequence upperCursor) {
        boolean interrupted = false;

        if (nextSequence > minGatingSequence.get()) {
            int counter = SPIN_TRIES;
            long minSequence;
            /**
             * 假设一开始Producer生产了0,1,2. upperCursor=2
             * 这时Consumer开始消费,一开始waitSequence=-1, +1=nextSequence=0, waitSequence=0
             *   则nextSequence<=upperCursor=2,可以消费,无需等待,直接返回0
             * Consumer继续消费,waitSequence=0, nextSequence=1, waitSequence=1
             *   nextSequence=1<=upperCursor=2,还可以消费,消费了1
             * Consumer继续消费,waitSequence=1, nextSequence=2, waitSequence=2
             *   nextSequence=2<=upperCursor=2,还可以消费,消费了2
             * 当Consumer还要继续消费时,waitSequence=2, nextSequence=3, waitSequence=3
             *   nextSequence=3>upperCursor=2, 不能消费!
             *
             * 因为Producer只生产了0,1,2, Consumer已经消费完了0,1,2, 现在队列里没有消息需要消费了
             * 如果Consumer还要消费消息,只能阻塞住! 直到Producer生产了新的消息,使得
             * Customer的nextSequence<=Producer.upperCursor时,Customer才有消息可以消费!
             *
             * 所以说这就是为什么在消费消息时,要传递生产者现在生产的位置upperCursor.
             */
            while (nextSequence > (minSequence = upperCursor.get())) {
                if (Thread.interrupted()) {
                    interrupted = true;
                    break;
                }
                counter = applyWaitMethod(counter);
            }
            minGatingSequence.set(minSequence);
        }

        return interrupted;
    }

    private int applyWaitMethod(int counter) {
        if (0 == counter) {
            Thread.yield();
        } else {
            --counter;
        }
        return counter;
    }


    @Override
    public long incrementAndGetInterruptibly(Sequence upperCursor)
            throws InterruptedException {
        final long nextSequence = waitSequence.get() + 1L;
        waitSequence.set(nextSequence);
        if (waitForFreeSlotAt(nextSequence, upperCursor))
            throw new InterruptedException();
        return nextSequence;
    }

    @Override
    public long incrementAndGetInterruptibly(Sequence lowerCursor,
                                             long timeout, TimeUnit sourceUnit)
            throws InterruptedException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void publish(long sequence, Sequence lowerCursor) {
        lowerCursor.set(sequence);
    }

    @Override
    public void publishInterruptibly(long sequence, Sequence lowerCursor) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        lowerCursor.set(sequence);
    }

    @Override
    public boolean isEmpty(final Sequence upperCursor) {
        final long nextSequence = (waitSequence.get() + 1L);
        final MutableLong minGatingSequence = this.minGatingSequence;
        if (nextSequence > minGatingSequence.get()) {
            long minSequence = upperCursor.get();
            minGatingSequence.set(minSequence);

            if (nextSequence > minSequence) {
                return false;
            }
        }
        return true;
    }

}
