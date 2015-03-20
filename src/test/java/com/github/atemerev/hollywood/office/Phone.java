package com.github.atemerev.hollywood.office;

import com.github.atemerev.hollywood.Actor;
import com.github.atemerev.hollywood.Hollywood;
import com.github.atemerev.hollywood.annotations.AllowedStates;
import com.github.atemerev.hollywood.annotations.Initial;
import com.github.atemerev.hollywood.annotations.State;
import com.github.atemerev.pms.listeners.MessageListener;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public abstract class Phone extends Actor {

    private static Phone instance = null;

    protected int callCount = 0;

    public static Phone instance() {
        if (instance == null) {
            instance = Hollywood.createActor(Phone.class);
        }
        return instance;
    }

    public int getCallCount() {
        return callCount;
    }

    // Public interface

    //备份: 因为接听电话是个很耗时的过程,所以启动一个新的线程来处理接听电话整个过程
    //Standby-->Talking
    @AllowedStates(Standby.class)
    public abstract void respond(Call call, String greeting, MessageListener listener);

    //挂断
    //Talking-->Standby
    @AllowedStates(Talking.class)
    public abstract void hangUp();

    //正在通话中
    //Talking...
    @AllowedStates(Talking.class)
    public abstract void say(String phrase);

    // States

    @Initial
    @State
    public static abstract class Standby extends Phone {

        public synchronized void respond(final Call call, String greeting, final MessageListener listener) {
            System.out.println(greeting);
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(5);
                        //带有@Listener的方法会被调用. 因为content是String类型,
                        //所以会触发Secretary.OnCall的$(String phrase)执行
                        //因为OnCall接收String类型的事件,而content就是String类型的.
                        listener.processMessage(call.getContent());
                    } catch (InterruptedException e) {
                    }
                }
            }.start();
            //由Standby转为正在通话中
            setState(Talking.class);
        }
    }

    @State
    public static abstract class Talking extends Phone {
        public void say(String phrase) {
            System.out.println(phrase);
        }

        public synchronized void hangUp() {
            setState(Standby.class);
        }

        public void onExit() {            
            callCount++;
        }
    }
}
