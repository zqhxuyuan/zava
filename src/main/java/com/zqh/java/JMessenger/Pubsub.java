package com.zqh.java.JMessenger;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * https://github.com/techrobby/SimplePubSub
 *
 * SimplePubsub is pure java implementation of publisher subscriber mechanism without any other library dependency. The project can be used in any java project including ANDROID.

 IMPLEMENTATION :

 Pubsub.java is a threadsafe singleton implementation of publisher subscriber system. It uses a threadpool to deliver the events to the subscribers. If you want ordering of the events simply configure the threadpool to be single threaded.

 Currently NUMBER_OF_THREADS = 1, but you can change it to any number depending on your needs.

 USAGE :

 PUBLISHER : Publisher should have an instance to Pubsub object and simply calls the publish function on a particular topic. Example : pubsub.publish("topic_food",obj), where obj could be any object used to pass to the subscriber.

 SUBSCRIBER : Subscriber should implement the listener Pubsub.listener interface and should subscribe to a particular topic using pubsub.addListener(topic,listener) function. All the events related to that topic will now be received in the onEventReceived() function along with the object passed.

 A sample testcase and usage is also included in the project.
 */
public class Pubsub implements Runnable
{
    public class Operation
    {
        public Operation(String type, Object o)
        {
            this.type = type;
            this.payload = o;
        }

        public final String type;

        public final Object payload;
    }

    public interface Listener
    {
        public void onEventReceived(String type, Object object);
    }

    private int NUMBER_OF_THREADS = 1;

    ExecutorService ex;

    private final BlockingQueue<Operation> mQueue;

    private Map<String, Set<Listener>> listeners;

    private static Pubsub _instance;

    public static Pubsub getInstance()
    {
        if (_instance == null)
        {
            synchronized (Pubsub.class)
            {
                if (_instance == null)
                    _instance = new Pubsub();
            }
        }
        return _instance;
    }

    private Pubsub()
    {
        listeners = new ConcurrentHashMap<String, Set<Listener>>();
        mQueue = new LinkedBlockingQueue<Operation>();
        ex = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        ex.submit(this);
    }

    public void addListener(String type, Listener listener)
    {
        add(type, listener);
    }

    public void addListeners(Listener listener, String... types)
    {
        for (String type : types)
        {
            add(type, listener);
        }
    }

    private void add(String type, Listener listener)
    {
        Set<Listener> list;
        list = listeners.get(type);
        if (list == null)
        {
            synchronized (this) // take a smaller lock
            {
                if ((list = listeners.get(type)) == null)
                {
                    list = new CopyOnWriteArraySet<Listener>();
                    listeners.put(type, list);
                }
            }
        }
        list.add(listener);
    }

    public void removeListener(String type, Listener listener)
    {
        remove(type, listener);
    }

    public void removeListeners(Listener listener, String... types)
    {
        for (String type : types)
        {
            remove(type, listener);
        }
    }

    private void remove(String type, Listener listener)
    {
        Set<Listener> l = null;
        l = listeners.get(type);
        if (l != null)
        {
            l.remove(listener);
        }
    }

    public boolean publish(String type, Object o)
    {
        Set<Listener> l = listeners.get(type);
        if (l != null && l.size() >= 0)
        {
            mQueue.add(new Operation(type, o));
            return true;
        }
        return false;
    }

    @Override
    public void run()
    {
        Operation op;
        while (true)
        {
            try
            {
                op = mQueue.take();
            }
            catch (InterruptedException e)
            {
                continue;
            }

            String type = op.type;
            Object o = op.payload;

            Set<Listener> list = listeners.get(type);

            if (list == null || list.isEmpty())
            {
                continue;
            }

            for (Listener l : list)
            {
                l.onEventReceived(type, o);
            }
        }
    }

}