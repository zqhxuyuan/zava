package com.github.shansun.guava.concurrency;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;

import java.util.concurrent.Executor;

/**
 * <p></p> 
 * @author:     lanbo <br>
 * @version:    1.0  <br>
 * @date:   	2012-7-4
 */
public class ServiceUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    static class SimpleService implements Service {

        boolean running = false;

        @Override
        public ListenableFuture<State> start() {
            return null;
        }

        @Override
        public State startAndWait() {
            return null;
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public State state() {
            return null;
        }

        @Override
        public ListenableFuture<State> stop() {
            return null;
        }

        @Override
        public State stopAndWait() {
            return null;
        }

        @Override
        public Throwable failureCause() {
            return null;
        }

        @Override
        public void addListener(Listener listener, Executor executor) {

        }

    }
}