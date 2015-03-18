package com.github.rfqu.ringBuffer.threaded;

class BatchRingBuffer<T> {
    int bufSize;
    Object[] entries;
    Window writeWindow;
    Window readWindow;

    public BatchRingBuffer(int bufSize) {
        this.bufSize = bufSize;
        entries=new Object[bufSize];
        writeWindow=new WriteWindow();
        readWindow=new ReadWindow();
    }

    abstract class Window {
        protected long position;
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

        public synchronized long getPosition() {
            return position;
        }

        public synchronized void waitPosition(long position2) throws InterruptedException {
            while (position2>=position) {
                waitCount++;
                this.wait();
            }
        }

        public synchronized void setPosition(long position) {
            this.position=position;
            this.notifyAll();
        }
    }

    class WriteWindow extends Window {

        public long getLimit() {
            return readWindow.getPosition()+bufSize;
        }

        public void waitLimit(long limit) throws InterruptedException {
            readWindow.waitPosition(limit-bufSize);
        }
    }
    
    class ReadWindow extends Window {

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