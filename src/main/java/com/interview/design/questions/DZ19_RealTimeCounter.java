package com.interview.design.questions;

import java.util.Date;

/**
 * Created_By: zouzhile
 * Date: 2/21/14
 * Time: 3:24 PM
 *
 * Given a timer time() with nanosecond accuracy and given the interface
 interface RealTimeCounter:
 void increment()
 int getCountInLastSecond()
 int getCountInLastMinute()
 int getCountInLastHour()
 int getCountInLastDay()
 implement the interface. The getCountInLastX functions should return the number of times increment was called in the last X.
 */
public class DZ19_RealTimeCounter {

    static class Timer {

        /*
          timer in nanoseconds accurracy
         */
        public static long time() {
            return new Date().getTime() * 1000;
        }

        /**
         *
         * @param start the start time in nanoseconds
         * @param end  the end time in nanoseconds
         * @return
         */
        public static int diff(long start, long end) {
            return (int) (end - start) / 1000000;
        }
    }

    class CyclicBuffer {

        int[] data = new int[86400]; //86400 second per day
        int endOffset = 0;

        /**
         *
         * @param value current counter value
         * @param seconds how many seconds passed since last append
         */
        public void append(int value, int seconds) {
            for(int i = 1; i <= seconds; i ++) {
                endOffset = (endOffset + i) % data.length;
                data[endOffset] = value;
            }
        }

        public int get(int distanceFromEnd) {
            int offset = (endOffset + data.length - distanceFromEnd) % data.length;
            return (int)data[offset];
        }
    }

    private int counter = 0;
    private long lastIncrementTime = -1;
    private CyclicBuffer buffer = new CyclicBuffer();

    public void increment() {
        long currentTime = Timer.time();

        if(lastIncrementTime < 0)
            lastIncrementTime = currentTime;

        int secondsSinceLastIncrement = Timer.diff(lastIncrementTime, currentTime);
        counter ++;
        buffer.append(counter, secondsSinceLastIncrement);
    }

    public int getCountInLastSecond() {
        return buffer.get(0) - buffer.get(1);
    }

    public int getCountInLastMinute() {
        return buffer.get(0) - buffer.get(60);
    }

    public int getCountInLastHour() {
        return buffer.get(0) - buffer.get(3600);
    }

    public int getCountInLastDay() {
        return buffer.get(0) - buffer.get(86399);
    }

}
