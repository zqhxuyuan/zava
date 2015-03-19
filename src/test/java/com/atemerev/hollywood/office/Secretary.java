package com.miriamlaurel.hollywood.office;

import com.miriamlaurel.hollywood.Actor;
import com.miriamlaurel.hollywood.StateChangedEvent;
import com.miriamlaurel.hollywood.annotations.AllowedStates;
import com.miriamlaurel.hollywood.annotations.Initial;
import com.miriamlaurel.hollywood.annotations.State;
import com.miriamlaurel.hollywood.future.CompletedEvent;
import com.miriamlaurel.hollywood.future.Promise;
import com.miriamlaurel.pms.Listener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public abstract class Secretary extends Actor {

    // Public interface

    @AllowedStates(AtHome.class)
    public abstract void goToWork();

    @AllowedStates(Working.class)
    public abstract void acceptLetter(Letter letter);

    // States

    @Initial
    @State
    public static abstract class AtHome extends Secretary {

        @AllowedStates(AtHome.class)
        public synchronized void goToWork() {
            setState(Working.class);
        }
    }

    @State
    public static abstract class Working extends Secretary {
        protected Phone phone;
        protected Fax fax;

        protected Queue<Call> callsOnHold = new LinkedList<Call>();
        protected Set<Letter> letters = new HashSet<Letter>();

        // Commands

        public void acceptLetter(Letter letter) {
            letters.add(letter);
        }

        // Behavior

        public void onEnter() {
            phone = Phone.instance();
            fax = Fax.instance();
        }

        public void onExit() {
            processOnHoldCalls();
            PostOffice.instance().send(letters);
        }

        @Listener
        public synchronized void $(EndDayEvent e) {
            setState(AtHome.class);
        }

        @Listener
        public synchronized void $(Call call) {
            OnCall newState = prepareState(OnCall.class);
            newState.call = call;
            setState(newState);
        }

        public synchronized void processOnHoldCalls() {
            if (callsOnHold.size() != 0) {
                System.out.println("Processing calls on hold...");
                me().processMessage(callsOnHold.poll());
            }
        }
    }

    @State
    public static abstract class OnCall extends Working {

        protected Call call;

        public void onEnter() {
            phone.respond(call, "- Corporate accounts payable, Nina speaking. Just a moment...", this);
        }

        @Listener
        public synchronized void $(String phrase) {
            System.out.println("- " + phrase);
            phone.say("- We aware of your problem and will resolve it ASAP. Goodbye.");
            if (phrase.contains("fax")) {
                FaxMessage message = new FaxMessage("our price list");
                SendingFax newState = prepareState(SendingFax.class);
                newState.faxToSend = message;
                setState(newState);
            } else if (phrase.contains("go home")) {
                setState(AtHome.class);
            } else {
                ((Working) setState(Working.class)).processOnHoldCalls();
            }
        }

        @Listener
        public synchronized void $(Call call) {
            callsOnHold.add(call);
            System.out.println("Incoming call taken on hold...");
        }

        public void onExit() {
            phone.hangUp();
        }
    }

    @State
    public static abstract class SendingFax extends Working {

        FaxMessage faxToSend;

        public void onEnter() {
            Promise<Void> sendPromise = fax.send(faxToSend);
            sendPromise.listeners().add(me());
        }

        @Listener
        public synchronized void $(Call call) {
            callsOnHold.add(call);
            System.out.println("Incoming call taken on hold...");
        }

        @Listener
        public synchronized void $(CompletedEvent e) {
            ((Working) setState(Working.class)).processOnHoldCalls();
        }
    }

    @Listener
    public void $(StateChangedEvent e) {
        System.out.println("\t\t\t\t\t\t\t\t[" + prevState() + "] -> [" + state() + "]");
    }
}
