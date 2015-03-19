package com.github.atemerev.pms.examples.routine;

import com.github.atemerev.pms.Listener;
import com.github.atemerev.pms.examples.helloworld.Evening;
import com.github.atemerev.pms.examples.helloworld.Morning;
import com.github.atemerev.pms.listeners.dispatch.DispatchListener;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class DailyRoutineHandler extends DispatchListener {
    @Listener void $(Morning morning) {
        System.out.println("Making bed...");
        System.out.println("Brushing teeth...");
        System.out.println("Shaving...");
    }

    @Listener void $(Evening evening) {
        System.out.println("Taking shower...");
        System.out.println("Going to sleep...");
    }
}
