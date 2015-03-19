package com.github.sefler1987.javaworker.worker;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleTaskIDGenerator {
    private static AtomicInteger lastID = new AtomicInteger(0);

    public static String genTaskID() {
        return "T" + lastID.incrementAndGet();
    }
}
