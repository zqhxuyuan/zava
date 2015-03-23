package com.zqh.base;

/**
 * Created by zqhxuyuan on 15-3-21.
 */
public class TestRef {

    public static void main(String[] args) {
        Node n1 = new Node();
        Node n2 = n1;

        n1.setValue(1);
        System.out.println(n2.getValue());

        n1.setWritePos(10);

    }
}

class Node{
    int value;

    int readPos;
    int writePos;
    int limit;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getReadPos() {
        return readPos;
    }

    public void setReadPos(int readPos) {
        this.readPos = readPos;
    }

    public int getWritePos() {
        return writePos;
    }

    public void setWritePos(int writePos) {
        this.writePos = writePos;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "limit:"+limit+";readPos:"+readPos +";writePos:"+writePos;
    }
}
