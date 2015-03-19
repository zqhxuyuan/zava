package com.github.sefler1987.javaworker.worker;

public interface TaskProcessor {
    void process(WorkerTask<?> task);
}
