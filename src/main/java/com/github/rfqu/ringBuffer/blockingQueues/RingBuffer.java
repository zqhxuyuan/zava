package com.github.rfqu.ringBuffer.blockingQueues;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class RingBuffer<T> {
    int bufSize;
    Object[] entries;
    WindowQueue writeQueue;
    WindowQueue readQueue;
    final Lock lock = new ReentrantLock();
    final Condition posChanged  = lock.newCondition();

    public RingBuffer(int bufSize) {
        this.bufSize = bufSize;
        entries=new Object[bufSize];
        writeQueue=new WriteQueue();
        readQueue=new ReadQueue();
    }

    abstract class WindowQueue {
        long lowLimit;
        long position;
        private long putWaitCount;
        private long takeWaitCount;

        public void put(T element) throws InterruptedException {
            lock.lock();
            try {
                while (getHiLimit()==position) {
                    putWaitCount++;
                    posChanged.await();
                }
                entries[(int)(position%bufSize)]=element;
                position++;
                posChanged.signal();
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("unchecked")
        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (position==lowLimit) {
                    takeWaitCount++;
                    posChanged.await();
                }
                posChanged.signal();
                return (T)entries[(int)(lowLimit++%bufSize)];
            } finally {
                lock.unlock();
            }
        }

        abstract long getHiLimit();
    }
    
    class ReadQueue extends WindowQueue {

        @Override
        long getHiLimit() {
            return writeQueue.lowLimit+bufSize;
        }
    }

    class WriteQueue extends WindowQueue {

        long getHiLimit() {
            return readQueue.lowLimit;
        }
    }
    
    abstract class Cursor {
        public abstract void put(T element) throws InterruptedException;
        public abstract T take() throws InterruptedException;

        public abstract long getPutWaitCount();
        public abstract long getTakeWaitCount();
    }
    
    public class Writer extends Cursor {

        @Override
        public void put(T element) throws InterruptedException {
            writeQueue.put(element);
        }

        @Override
        public T take() throws InterruptedException {
            return readQueue.take();
        }

        public long getPutWaitCount() {
            return writeQueue.putWaitCount;
        }

        public long getTakeWaitCount() {
            return readQueue.takeWaitCount;
        }
    }
    
    public class Reader extends Cursor {

        @Override
        public void put(T element) throws InterruptedException {
            readQueue.put(element);
        }

        @Override
        public T take() throws InterruptedException {
            return writeQueue.take();
        }

        public long getPutWaitCount() {
            return readQueue.putWaitCount;
        }

        public long getTakeWaitCount() {
            return writeQueue.takeWaitCount;
        }
    }
}