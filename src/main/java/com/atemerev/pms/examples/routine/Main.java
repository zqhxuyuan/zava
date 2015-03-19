package com.atemerev.pms.examples.routine;

import com.atemerev.pms.examples.helloworld.Evening;
import com.atemerev.pms.examples.helloworld.HelloWorldHandler;
import com.atemerev.pms.examples.helloworld.Morning;
import com.atemerev.pms.listeners.dispatch.DispatchListener;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class Main {
    public static void main(String[] args) {
        Life life = new Life();
        DispatchListener helloWorld
                = new DispatchListener(new HelloWorldHandler());
        DailyRoutineHandler dailyRoutine
                = new DailyRoutineHandler();
        life.listeners().add(helloWorld);
        life.listeners().add(dailyRoutine);
        life.processMessage(new Morning());
        life.processMessage(new Evening());
    }
}
