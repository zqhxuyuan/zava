package com.github.coderplay.test;

import com.github.coderplay.util.concurrent.queue.Sequence;

/**
 * Created by zqhxuyuan on 15-3-27.
 */
public class TestSequence {

    public static void main(String[] args) {
        Sequence sequence = new Sequence();
        System.out.println(sequence.addAndGet(1));;
        System.out.println(sequence.addAndGet(1));;
        System.out.println(sequence.addAndGet(100));;
    }
}
