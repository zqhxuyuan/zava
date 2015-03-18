package com.github.rfqu.ringBuffer.threaded;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class RingBuffer<T> {
    int bufSize;
    Object[] entries;
    Cursor writeWindow;
    Cursor readWindow;

    public RingBuffer(int bufSize) {
        this.bufSize = bufSize;
        entries=new Object[bufSize];
        writeWindow=new WriteCursor();
        readWindow=new ReadCursor();
    }

    abstract class Cursor {
        protected long position;
        private final Lock lock = new ReentrantLock();
        private final Condition posChanged  = lock.newCondition();
        int waitCount=0;        

        @SuppressWarnings("unchecked")
        public T get(long position) {
            return (T)entries[(int)(position%bufSize)];
        }

        public void set(long position, T object) {
            entries[(int)(position%bufSize)]=object;
        }

        public abstract void waitLimit(long position2) throws InterruptedException;

        public abstract long getLimit();

        public long getPosition() {
            lock.lock();
            try {
               return position;
            } finally {
               lock.unlock();
            }
        }

        /** not for public use */
        void waitPosition(long position2) throws InterruptedException {
            lock.lock();
            try {
                while (position2>=position) {
                    waitCount++;
                    posChanged.await();
                }
            } finally {
                lock.unlock();
            }
        }

        public void setPosition(long position) {
            lock.lock();
            try {
                this.position=position;
                posChanged.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    class WriteCursor extends Cursor {

        public long getLimit() {
            return readWindow.getPosition()+bufSize;
        }

        public void waitLimit(long limit) throws InterruptedException {
            readWindow.waitPosition(limit-bufSize);
        }
    }
    
    class ReadCursor extends Cursor {

        @Override
        public long getLimit() {
            return writeWindow.getPosition();
        }

        @Override
        public void waitLimit(long limit) throws InterruptedException {
            writeWindow.waitPosition(limit);
        }
    }
}