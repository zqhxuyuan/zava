package com.github.sefler1987.javaworker.worker;

import java.util.concurrent.Future;

/**
 * 工人的一个任务. 只要为PageURLMiningTask指定一个URL, 剩余的挖掘工作都交给工人去完成. 不管他挖了多深
 * @param <T>
 */
public abstract class WorkerTask<T> implements Future<T> {

    protected String taskID;
    protected boolean done = false;
    protected int priority;

    public WorkerTask(int priority) {
        taskID = SimpleTaskIDGenerator.genTaskID();
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
