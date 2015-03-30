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
 * Single-threaded publisher {@link ClaimStrategy}.
 *
 * @author Min Zhou (coderplay@gmail.com)
 */
public class SingleThreadedClaimStrategy implements ClaimStrategy {

    private static final int SPIN_TRIES = 100;

    private final int bufferSize;
    private final PaddedLong minGatingSequence = new PaddedLong(Constants.INITIAL_CURSOR_VALUE);
    private final PaddedLong claimSequence = new PaddedLong(Constants.INITIAL_CURSOR_VALUE);

    /**
     * Construct a new single threaded publisher {@link ClaimStrategy} for a given
     * buffer size.
     *
     * @param bufferSize for the underlying data structure.
     */
    public SingleThreadedClaimStrategy(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public long getSequence() {
        return claimSequence.get();
    }

    @Override
    public long incrementAndGet(Sequence lowerCursor) {
        //申请下一个sequence. 如果可以可以写.则往这个位置写入消息
        final long nextSequence = claimSequence.get() + 1L;
        claimSequence.set(nextSequence);

        //如果这个位置,还有消费者没有消费消息,则要等待消费者离开
        waitForFreeSlotAt(nextSequence, lowerCursor);
        return nextSequence;
    }

    /**
     *
     * @param sequence 生产者下一个可写入的位置
     * @param lowerCursor 消费者的位置
     * @return
     */
    private boolean waitForFreeSlotAt(final long sequence, Sequence lowerCursor) {
        boolean interrupted = false;

        final long wrapPoint = sequence - bufferSize;
        if (wrapPoint > minGatingSequence.get()) {
            int counter = SPIN_TRIES;
            long minSequence;

            /**
             * sequence - bufferSize > lowerCursor
             * sequence > bufferSize + lowerCursor时等待. 只有在Producer写过一圈后,才会>bufferSize
             * 假设bufferSize=10,lowerCursor=2. bufferSize+lowerCursor=12,当sequence=13时,Producer需要等待.
             * lowerCursor=2表示有消费者只消费到了index=2. 当Producer在12位置,可以正常往队列中写入消息.
             * 但是当Producer还要再写时,nextSequence=13,因为index=13%10=3>2.或者13>10+2. 所以不能写!
             *
             * 当sequence <= bufferSize+lowerCursor=12时,可直接写!
             *
             * seq=3   Producer生产了0,1,2,
             *         Consumer消费了0,1,2, lowerCursor=2
             *         Producer下一个要写入的seq=3, 3<=10+2, 可写
             * seq=13  Producer生产了0,1,2,3,4....9
             *         Consumer消费了0,1,2, lowerCursor=2
             *         Producer再生产10,11,12
             *         Producer下一个要写入的seq=13, 13>10+2, 不可写! 需等待!
             */
            while (wrapPoint > (minSequence = lowerCursor.get())) {
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
    public long incrementAndGetInterruptibly(Sequence lowerCursor)
            throws InterruptedException {
        final long nextSequence = claimSequence.get() + 1L;
        claimSequence.set(nextSequence);
        if (waitForFreeSlotAt(nextSequence, lowerCursor))
            throw new InterruptedException();
        return nextSequence;
    }

    @Override
    public long incrementAndGetInterruptibly(Sequence lowerCursor, long timeout,
                                             TimeUnit sourceUnit) throws InterruptedException {
        return 0;
    }

    @Override
    public void publish(long sequence, Sequence upperCursor) {
        upperCursor.set(sequence);
    }

    @Override
    public void publishInterruptibly(long sequence, Sequence upperCursor)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        upperCursor.set(sequence);
    }

    @Override
    public boolean hasRemaining(Sequence lowerCursor) {
        final long wrapPoint = (claimSequence.get() + 1L) - bufferSize;
        if (wrapPoint > minGatingSequence.get()) {
            long minSequence = lowerCursor.get();
            minGatingSequence.set(minSequence);

            if (wrapPoint > minSequence) {
                return false;
            }
        }

        return true;
    }
}
