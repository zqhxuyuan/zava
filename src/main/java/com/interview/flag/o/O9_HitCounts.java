package com.interview.flag.o;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created_By: stefanie
 * Date: 15-1-13
 * Time: 下午1:36
 */
public class O9_HitCounts {

    class Timer{
        long lastTime = -1;
        int precision = 1;

        public Timer(int precision){
            this.precision = precision;
        }
        public long time() {
            return new Date().getTime() / precision;
        }
        public long diff(){
            long current = time();
            long diff = 0;
            if(lastTime != -1) diff = current - lastTime;
            lastTime = current;
            return diff;
        }
    }

    class CyclicBuffer{
        Timer timer;
        int capacity;
        int unit;
        int[] buffer;
        int index = 0;

        public CyclicBuffer(int capacity, int precision){
            this.unit = 1000 / precision;
            this.capacity = capacity * this.unit + 1;
            buffer = new int[this.capacity];
            timer = new Timer(precision);
        }

        public void append(int increment){
            long shift = timer.diff();
            if(shift == 0){
                buffer[index] += increment;
            } else {
                int value = buffer[index] + increment;
                for(int i = 1; i <= shift; i++){
                    buffer[(index + i) % capacity] = value;
                }
                index = (int) (index + shift) % capacity;
            }
        }

        public int get(int secondBeforeNow){
            secondBeforeNow *= this.unit;
            int offset = (index + capacity - secondBeforeNow) % capacity;
            return buffer[offset];
        }
    }

    CyclicBuffer buffer = new CyclicBuffer(5 * 60, 500);     //the max range is 5 minutes, record precision is 500 million seconds.

    public void hit(){
        buffer.append(1);
    }

    public int getHitsInLastSecond(){
        buffer.append(0);
        return buffer.get(0) - buffer.get(1);
    }

    public int getHitsInLastMinute(){
        buffer.append(0);
        return buffer.get(0) - buffer.get(60);
    }

    public int getHitsInLastFiveMinute(){
        buffer.append(0);
        return buffer.get(0) - buffer.get(300);
    }

    public static void main(String[] args) throws InterruptedException {
        O9_HitCounts counter = new O9_HitCounts();

        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 60; j++) {
                TimeUnit.MILLISECONDS.sleep(900);
                for (int k = 0; k < 5; k++) counter.hit();

                System.out.println(counter.getHitsInLastSecond()); //5
                System.out.println(counter.getHitsInLastMinute()); //330
                System.out.println(counter.getHitsInLastFiveMinute());  //330 * 5
            }

        }

    }
}
