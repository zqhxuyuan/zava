/*
 * Copyright 2011 by Alexei Kaigorodov
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.rfqu.ringBuffer.blockingQueues;

import java.util.concurrent.ExecutionException;

import com.github.rfqu.util.LongValue;
import org.junit.Assert;
import org.junit.Test;

public class ThreadedRingBufferTest {    

    static abstract class Worker implements Runnable {
        RingBuffer<LongValue>.Cursor cursor;
        int iterations;
        String name;

        public Worker(RingBuffer<LongValue>.Cursor cursor, int iterations, String name) {
            this.cursor = cursor;
            this.iterations = iterations;
            this.name = name;
        }

        @Override
        public void run() {
            System.out.printf("%s started\n", name);
            try {
                for (long position = 0; position<iterations; position++) {
                    act(position);
                }
            } catch (InterruptedException e) {
            }
            System.out.printf("%s; put waitCount=%,d; take waitCount=%,d\n"
                    , name, cursor.getPutWaitCount(), cursor.getTakeWaitCount());
       }

        protected abstract void act(long position) throws InterruptedException;
    }

    static class Writer extends Worker {

        public Writer(RingBuffer<LongValue>.Cursor cursor, int iterations) {
            super(cursor, iterations, "Writer");
        }

        protected void act(long position) throws InterruptedException {
            LongValue item = cursor.take();
            item.value=position;
            cursor.put(item);
        }
    }
    
    static class Reader extends Worker {

        public Reader(RingBuffer<LongValue>.Cursor cursor, int iterations) {
            super(cursor, iterations, "Reader");
        }

        protected void act(long position) throws InterruptedException {
            LongValue item = cursor.take();
            Assert.assertTrue(item.value == position);
            cursor.put(item);
        }
    }
    
    void test(int iterations) throws InterruptedException {
        System.out.printf("<<iterations=%,d\n", iterations);
        int bufSize = 256;
        RingBuffer<LongValue> ringBuffer=new RingBuffer<LongValue>(bufSize);
        RingBuffer<LongValue>.Cursor writeWindow=ringBuffer.new Writer();
        RingBuffer<LongValue>.Cursor readWindow=ringBuffer.new Reader(); 
        for (int k=0; k<bufSize; k++) {
            readWindow.put(new LongValue(-1));
        }
        Writer writer=new Writer(writeWindow, iterations);
        Reader reader=new Reader(readWindow, iterations);
        
        Thread rt=new Thread(reader);
        Thread wt=new Thread(writer);
        long start=System.currentTimeMillis();
        wt.start();
        rt.start();
        rt.join();
        wt.join();
        long end=System.currentTimeMillis();
        long elapsed = end-start;
        float throughput=iterations/(elapsed/1000f);
        System.out.printf("elapsed=%,d ms, throughput=%,d>>\n", elapsed, (int)throughput);
    }

    @Test
    public void test1() throws InterruptedException {
        int iterations = 1000000;
        test(iterations/2);
        test(iterations/2);
        test(iterations);
        test(iterations);
    }

    public static void main(String args[]) throws InterruptedException, ExecutionException {
        ThreadedRingBufferTest nt = new ThreadedRingBufferTest();
        nt.test1();
    }

}
