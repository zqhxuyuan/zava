/*
 * Copyright 2011 by Alexei Kaigorodov
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.rfqu.ringBuffer.threaded;

import java.util.concurrent.ExecutionException;

import com.github.rfqu.util.LongValue;
import junit.framework.Assert;

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
            System.out.printf("%s started; position=%,d\n", name, cursor.getPosition());
            try {
                for (long position = 0; ; ) {
                    cursor.waitLimit(position);
                    act(position);
                    position++;
                    cursor.setPosition(position);
                    if (position==iterations) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
            }
            System.out.printf("%s; waitCount=%,d; position=%,d\n"
                    , name, cursor.waitCount, cursor.getPosition());
       }

        protected abstract void act(long position);
    }

    static class Writer extends Worker {

        public Writer(RingBuffer<LongValue>.Cursor cursor, int iterations) {
            super(cursor, iterations, "Writer");
        }

        protected void act(long position) {
            LongValue item = cursor.get(position);
            item.value=position;
//          cursor.set(position, null);
        }
    }
    
    static class Reader extends Worker {

        public Reader(RingBuffer<LongValue>.Cursor cursor, int iterations) {
            super(cursor, iterations, "Reader");
        }

        protected void act(long position) {
            LongValue item = cursor.get(position);
            Assert.assertTrue(item.value==position);
//          cursor.set(position, null);
        }
    }
    
    void test(int iterations) throws InterruptedException {
        System.out.printf("<<iterations=%,d\n", iterations);
        int bufSize = 1024;
        RingBuffer<LongValue> ringBuffer=new RingBuffer<LongValue>(bufSize);
        RingBuffer<LongValue>.Cursor writeWindow=ringBuffer.writeWindow;
        RingBuffer<LongValue>.Cursor readWindow=ringBuffer.readWindow; 
        for (int k=0; k<bufSize; k++) {
            writeWindow.set(k, new LongValue(0));
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
