package com.github.techrobby.SimplePubSub;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Pubsub implements Runnable {
    //操作对象: 包括消息的主题类型和消息的具体内容
    public class Operation {
        public Operation(String type, Object o) {
            this.type = type;
            this.payload = o;
        }

        public final String type;
        public final Object payload;
    }

    //监听器,具体监听器要实现这个接口
    public interface Listener {
        //并在自己的实现方法里,对接收到某种类型的主题做出具体的实现
        public void onEventReceived(String type, Object object);
    }

    private int NUMBER_OF_THREADS = 1;

    //并发线程池
    ExecutorService ex;

    //消息的发布和订阅通过队列存取
    private final BlockingQueue<Operation> mQueue;

    //topicType, 实现Listener的实现类. 同一个topic可以有多个订阅者
    private Map<String, Set<Listener>> listeners;

    //单例
    private static Pubsub _instance;

    //双重检查
    public static Pubsub getInstance() {
        if (_instance == null) {
            synchronized (Pubsub.class) {
                if (_instance == null)
                    _instance = new Pubsub();
            }
        }
        return _instance;
    }

    private Pubsub() {
        listeners = new ConcurrentHashMap<String, Set<Listener>>();
        mQueue = new LinkedBlockingQueue<Operation>();
        ex = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        //自己也是一个线程
        ex.submit(this);
    }

    public void addListener(String type, Listener listener) {
        add(type, listener);
    }

    public void addListeners(Listener listener, String... types) {
        for (String type : types) {
            add(type, listener);
        }
    }

    //往指定的topic中添加一个监听器.
    private void add(String type, Listener listener) {
        //topic的订阅者是一个无序集合
        Set<Listener> list = listeners.get(type);
        //集合为空:比如第一个订阅者,先创建一个集合,准备用来将当前订阅者加入集合中
        if (list == null) {
            // take a smaller lock 和单例的获取一样,双重检查锁, 所以是线程安全的
            synchronized (this) {
                if ((list = listeners.get(type)) == null) {
                    list = new CopyOnWriteArraySet<Listener>();
                    //listeners首先是一个Map.先往Map的topicType中放入一个空的List
                    //因为listeners要保存所有topic的所有订阅者
                    listeners.put(type, list);
                }
            }
        }
        //再往List中添加当前订阅者
        list.add(listener);
    }

    public void removeListener(String type, Listener listener) {
        remove(type, listener);
    }

    public void removeListeners(Listener listener, String... types) {
        for (String type : types) {
            remove(type, listener);
        }
    }

    private void remove(String type, Listener listener) {
        Set<Listener> l = listeners.get(type);
        //如果主题的订阅者本来就没有,就不需要移除, 存在才需要移除
        if (l != null) {
            l.remove(listener);
        }
    }

    public boolean publish(String type, Object o) {
        //这个主题有订阅者才能发送成功,否则没有订阅者,发送了也没用
        Set<Listener> l = listeners.get(type);
        if (l != null && l.size() >= 0) {
            //队列只保存了要发送到的主题类型和消息内容. 不保存这个主题的订阅者有哪些人
            mQueue.add(new Operation(type, o));
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        Operation op;
        while (true) {
            try {
                //从队列中获取出一个元素
                op = mQueue.take();
            } catch (InterruptedException e) {
                continue;
            }

            String type = op.type;
            Object o = op.payload;

            //这个主题的订阅者列表
            Set<Listener> list = listeners.get(type);
            if (list == null || list.isEmpty()) continue;
            //触发监听事件, 调用订阅者自己的接收方法
            for (Listener l : list) {
                l.onEventReceived(type, o);
            }
        }
    }

}