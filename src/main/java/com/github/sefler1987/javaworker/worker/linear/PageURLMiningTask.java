package com.github.sefler1987.javaworker.worker.linear;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.sefler1987.javaworker.worker.WorkerTask;

/**
 * 给定一个PageURL, 挖掘这个目标URL上的所有URL, 以及更进一层的挖掘...
 */
public class PageURLMiningTask extends WorkerTask<HashSet<String>> {
    private static final int NO_PRIORITY = 0;

    //一个URL会挖掘出好多相关的任务.比如提供一个网页,则这个网页会有其他的连接地址
    //这些地址都是需要挖掘的. 因此由一个targetURL构成的任务, 它有一堆等待挖掘的URLs
    private HashSet<String> minedURLs = new HashSet<String>();

    //挖掘目标URL
    private String targetURL;

    public PageURLMiningTask(String targetURL) {
        super(NO_PRIORITY);

        this.targetURL = targetURL;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public synchronized HashSet<String> get() throws InterruptedException, ExecutionException {
        if (!isDone()) {
            wait();
        }

        return minedURLs;
    }

    @Override
    public synchronized HashSet<String> get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        if (!isDone()) {
            wait(unit.toMillis(timeout));
        }

        return minedURLs;
    }

    public HashSet<String> getMinedURLs() {
        return minedURLs;
    }

    //当找到一个新的URL时, 要将其加入到待挖掘的URLs中
    public void addMinedURL(String url) {
        minedURLs.add(url);
    }

    public String getTargetURL() {
        return targetURL;
    }

    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }
}
