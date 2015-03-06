package com.interview.algorithms.array;

/**
 * Created_By: zouzhile
 * Date: 9/21/14
 * Time: 2:57 PM
 */
public class C4_12_CyclicBuffer {

    private int capacity = 0;
    private int size = 0;
    private int[] values;

    private int writeOffset = 0;
    private int readOffset = -1;

    public C4_12_CyclicBuffer(int capacity) {
        this.capacity = capacity;
        values = new int[capacity];
    }

    public int next(){
        if(this.size < this.capacity && this.readOffset >= this.size) {
            return Integer.MIN_VALUE;
        }

        int value = values[readOffset];
        readOffset ++;
        readOffset %= this.capacity;
        return value;
    }

    public void append(int value) {
        values[writeOffset] = value;
        if(this.size < this.capacity)
            this.size ++;

        // cyclic write. The readOffset now points to the last written value
        // so increase readOffset by 1 so that it points to the earliest written value
        if(this.size == this.capacity && this.writeOffset == this.readOffset)
            readOffset ++;

        this.writeOffset ++;
        this.writeOffset %= this.capacity;
    }
}
