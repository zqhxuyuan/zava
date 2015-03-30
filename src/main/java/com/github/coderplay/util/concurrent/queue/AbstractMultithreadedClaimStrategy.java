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
 * Basic class of multi-threaded {@link ClaimStrategy}
 *
 * 多线程版本的索取策略. 索取是生产者. 所以对应了多线程的生产者. 注意表示多个生产者!
 * @author Min Zhou (coderplay@gmail.com)
 */
public abstract class AbstractMultithreadedClaimStrategy implements ClaimStrategy {

    private final int bufferSize;
    private final Sequence claimSequence = new Sequence(Constants.INITIAL_CURSOR_VALUE);
    //多线程版本,每个线程都有自己的ThreadLocal对象. 单线程版本直接使用minGatingSequence
    private final ThreadLocal<MutableLong> minGatingSequenceThreadLocal =
            new ThreadLocal<MutableLong>() {
                @Override
                protected MutableLong initialValue() {
                    return new MutableLong(Constants.INITIAL_CURSOR_VALUE);
                }
            };

    public AbstractMultithreadedClaimStrategy(int bufferSize) {
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
        //Gating只是守卫者. 但是每个线程都有自己的minGatingSequenceThreadLocal对象
        //所以有多个线程时,要获取所有线程中最小的守卫者!
        final MutableLong minGatingSequence = minGatingSequenceThreadLocal.get();
        waitForCapacity(lowerCursor, minGatingSequence);

        //要索取序列号,还是通过ClaimSequence
        final long nextSequence = claimSequence.incrementAndGet();
        waitForFreeSlotAt(nextSequence, lowerCursor, minGatingSequence);

        return nextSequence;
    }

    @Override
    public long incrementAndGetInterruptibly(Sequence lowerCursor) throws InterruptedException {
        final MutableLong minGatingSequence = minGatingSequenceThreadLocal.get();
        if (waitForCapacity(lowerCursor, minGatingSequence))
            throw new InterruptedException();

        final long nextSequence = claimSequence.incrementAndGet();
        if (waitForFreeSlotAt(nextSequence, lowerCursor, minGatingSequence))
            throw new InterruptedException();

        return nextSequence;
    }

    @Override
    public long incrementAndGetInterruptibly(Sequence lowerCursor, long timeout, TimeUnit sourceUnit) throws InterruptedException {
        // TODO: complete the timeout feature
        final long timeoutMs = sourceUnit.toMillis(timeout);
        final long startTime = System.currentTimeMillis();
        final MutableLong minGatingSequence = minGatingSequenceThreadLocal.get();
        if(waitForCapacity(lowerCursor, minGatingSequence, timeout, startTime))
            throw new InterruptedException();

        final long elapsedTime = System.currentTimeMillis() - startTime;
        final long nextSequence = claimSequence.incrementAndGet();
        if (waitForFreeSlotAt(nextSequence, lowerCursor, minGatingSequence))
            throw new InterruptedException();

        return nextSequence;
    }

    @Override
    public boolean hasRemaining(final Sequence lowerCursor) {
        return hasRemaining(claimSequence.get(), lowerCursor);
    }

    /**
     * @return {@code true} if interrupted
     */
    private boolean waitForCapacity(final Sequence lowerCursor, final MutableLong minGatingSequence) {
        boolean interrupted = false;

        //调用该方法前,还没有使用claimSequence自增来索取下一个可用的序列号.
        //单线程的做法是使用claimSequence自增后,将nextSequence传递进来.
        final long wrapPoint = (claimSequence.get() + 1L) - bufferSize;
        if (wrapPoint > minGatingSequence.get()) {
            long minSequence;
            //claimSequence.get() + 1 - bufferSize > lowerCursor
            //claimSequence.get() - bufferSize = lowerCursor

            //要索取的下一个序列号 - 缓冲区大小 > 消费者的游标. 这种情况表示:消费者游标的位置还没有消费, 生产者就不能往这个位置生产消息
            //生产者的当前位置 - 缓冲区大小 = 消费者的游标.   生产者的当前位置+1=生产者的下一个可用序列号.
            //比如缓冲区大小=10, 生产者的当前位置=12, 消费者的游标=2. 按照上面的公式12-10=2.
            //即生产者下一个要索取的序列号=13, 等于 消费者下一个要消费的位置3. 消费者还没消费, 则生产者不能往3中写数据! 生产者就要等待!

            //不需要等待的条件是: claimSequence.get() - bufferSize < lowerCursor
            while (wrapPoint > (minSequence = lowerCursor.get())) {
                if (parkAndCheckInterrupt()) {
                    interrupted = true;
                    break;
                }
            }

            minGatingSequence.set(minSequence);
        }

        return interrupted;
    }

    /**
     * @return {@code true} if interrupted
     */
    private boolean waitForCapacity(final Sequence lowerCursor,
                                    final MutableLong minGatingSequence, final long timeout, final long start) {
        boolean interrupted = false;

        final long wrapPoint = (claimSequence.get() + 1L) - bufferSize;
        if (wrapPoint > minGatingSequence.get()) {
            long minSequence;
            while (wrapPoint > (minSequence = lowerCursor.get())) {
                if (parkAndCheckInterrupt()) {
                    interrupted = true;
                    break;
                }

                final long elapsedTime = System.currentTimeMillis() - start;
                if (elapsedTime > timeout) {
                    break;
                }
            }

            minGatingSequence.set(minSequence);
        }

        return interrupted;
    }

    /**
     *
     * @param sequence 生产者要索取的下一个可用序列号
     * @param lowerCursor 消费者的游标位置
     * @param minGatingSequence 最小守卫序列号
     * @return {@code true} if interrupted
     */
    private boolean waitForFreeSlotAt(final long sequence,
                                      final Sequence lowerCursor, final MutableLong minGatingSequence) {
        boolean interrupted = false;
        final long wrapPoint = sequence - bufferSize;

        if (wrapPoint > minGatingSequence.get()) {
            long minSequence;
            while (wrapPoint > (minSequence = lowerCursor.get())) {
                if (parkAndCheckInterrupt()) {
                    interrupted = true;
                    break;
                }
            }

            minGatingSequence.set(minSequence);
        }

        return interrupted;
    }

    /**
     * Convenience method to park and then check if interrupted
     *
     * @return {@code true} if interrupted
     */
    private final boolean parkAndCheckInterrupt() {
        LockSupport.parkNanos(1L);
        return Thread.interrupted();
    }

    private boolean hasRemaining(long sequence, final Sequence lowerCursor) {
        final long wrapPoint = (sequence + 1L) - bufferSize;
        final MutableLong minGatingSequence = minGatingSequenceThreadLocal.get();
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
