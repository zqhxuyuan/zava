package com.miriamlaurel.hollywood.jekyll;

import com.miriamlaurel.hollywood.Actor;
import com.miriamlaurel.hollywood.Hollywood;
import com.miriamlaurel.hollywood.annotations.AllowedStates;
import com.miriamlaurel.hollywood.annotations.Initial;
import com.miriamlaurel.hollywood.annotations.State;

/**
 * @author Alexander Temerev
 */
public abstract class JekyllHyde extends Actor {

    public static JekyllHyde create() {
        return Hollywood.createActor(JekyllHyde.class);
    }

    // public interface

    public abstract String say();
    public abstract void transform();
    public abstract String say(String arg);

    @AllowedStates(Hyde.class)
    public abstract void killAllHumans();


    // states

    @Initial @State
    public static abstract class Jekyll extends JekyllHyde {

        public String say() {
            return "My name is Jekyll, I am a Ph.D. in chemistry.";
        }

        public String say(String arg) {
            return arg.toLowerCase();
        }

        public synchronized void transform() {
            setState(Hyde.class);
        }
    }

    @State
    public static abstract class Hyde extends JekyllHyde {
        public String say() {
            return "AAAAAAARRRRRRRGGMMMHHH!!!!11";
        }

        public synchronized void transform() {
            setState(Jekyll.class);
        }

        public void killAllHumans() {
            System.out.println("Silence! I'll kill you!");
        }

        public String say(String arg) {
            return arg.toUpperCase();
        }
    }

}
