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

    @AllowedStates(Standby.class)
    public abstract void respond(Call call, String greeting, MessageListener listener);

    //挂断
    @AllowedStates(Talking.class)
    public abstract void hangUp();

    //正在通话中
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
                        listener.processMessage(call.getContent());
                    } catch (InterruptedException e) {
                    }
                }
            }.start();
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
