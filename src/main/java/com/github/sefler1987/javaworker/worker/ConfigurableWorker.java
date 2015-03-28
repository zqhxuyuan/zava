package com.github.sefler1987.javaworker.worker;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 工人
 */
public class ConfigurableWorker implements Runnable, LifeCycle {

    //任务队列
    private BlockingQueue<WorkerTask<?>> taskQueue = new ArrayBlockingQueue<WorkerTask<?>>(5);

    //有一个后台线程,在一直跑
    private Thread thread;

    //工人的<事件->监听器列表>的映射.
    private HashMap<WorkerEvent, CopyOnWriteArrayList<WorkerListener>> listenerMap;

    //要怎么处理任务. 线性,还是MapReduce? 一个工人有一个任务处理器
    private TaskProcessor taskProcessor;

    //只有初始化完毕,才可以工作
    private volatile boolean initiated = false;

    //工人id
    private String workerID;

    public ConfigurableWorker(String workerID) {
        this.workerID = workerID;
    }

    @Override
    public void start() {
        if (!initiated) {
            init();
        }
        thread.start();
    }

    @Override
    public void init() {
        if (initiated) return;

        if (taskProcessor == null)
            throw new IllegalStateException("Task Processor must be set first");

        thread = new Thread(this);
        thread.setDaemon(true);

        //初始化时,工人会对一个事件会有多个监听事件
        listenerMap = new HashMap<WorkerEvent, CopyOnWriteArrayList<WorkerListener>>();

        initiated = true;
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    // 给工人添加任务. 任务用队列来保存. 任何需要执行一定时间的任务最好放在队列里(生产). 并用一个后台线程来消费队列里的任务
    public String addTask(WorkerTask<?> task) {
        if (!initiated) {
            init();
        }

        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            thread.interrupt();
        }

        //放入队列后, 立即返回. 不需要等待任务执行.
        return task.getTaskID();
    }

    // 给工人添加任务, 由于任务可能长时间运行, 所以不能等待工人完成一个任务,再执行接下来的任务.
    // 所以在任务完成的时候, 需要能够通知给调用者, 说工人完成任务了. 任务有两种执行结果: 执行成功,执行失败
    // 这都属于任务执行的事件状态. 当任务到达这两种状态的一种, 都应该能够触发相应的事件.
    // 使用监听器来完成事件的通知.

    // 给工人添加监听器. 在开始工作前,要指定对某一类的事件注册监听器
    public synchronized void addListener(WorkerListener listener) {
        if (!initiated) {
            init();
        }
        List<WorkerEvent> intrestEvents = listener.intrests();
        for (WorkerEvent event : intrestEvents) {
            CopyOnWriteArrayList<WorkerListener> listeners = listenerMap.get(event);
            //第一个监听器,创建一个列表,并将其加入列表中
            if (listeners == null) {
                listeners = new CopyOnWriteArrayList<WorkerListener>();
            }

            //将当前要注册的监听器,加入到这个时间的监听器列表中.
            listeners.add(listener);
            listenerMap.put(event, listeners);
        }
    }

    // 工人的后台线程, 工作啦, 啦啦啦....啦啦啦... 啦啦啦
    @Override
    public void run() {
        try {
            for (;;) {
                //从队列中获取任务
                WorkerTask<?> task = taskQueue.take();

                //使用任务处理器执行任务, 任务的执行是顺序的!
                taskProcessor.process(task);

                //任务完成后,触发"任务执行完成"的事件
                if (task.isDone()) {
                    fireEvent(WorkerEvent.TASK_COMPLETE, task);
                    //完成当前任务后,继续for循环. 看看队列中还有没有要执行的任务
                    continue;
                }

                //任务没有完成,触发"任务执行失败"的事件
                fireEvent(WorkerEvent.TASK_FAILED, task);
            }
        } catch (InterruptedException e) {
            System.out.println("Worker mission canceled, remaining task size: " + taskQueue.size());
            return;
        }
    }

    // Got Event! DO DO DO THE FUCKING DEAD WORK!
    // 当给工人注册的事件发生时, 触发监听器的调用.
    public void fireEvent(WorkerEvent event, Object... args) {
        CopyOnWriteArrayList<WorkerListener> listeners = listenerMap.get(event);
        //这个事件没有监听器, 不做任何事
        if (listeners == null) return;

        //对在这个事件上的每个监听器,进行回调
        for (WorkerListener listener : listeners) {
            //回调. 自定义的监听器实现类需要对不同的事件做不同的响应.
            listener.onEvent(event, args);
        }
    }

    public TaskProcessor getTaskProcessor() {
        return taskProcessor;
    }

    public void setTaskProcessor(TaskProcessor taskProcessor) {
        this.taskProcessor = taskProcessor;
    }

    public String getWorkerID() {
        return workerID;
    }
}
