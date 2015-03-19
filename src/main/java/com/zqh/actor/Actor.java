package com.zqh.actor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://github.com/cloudjun/SimpleActorJava
 */
class ActorStatus {
    public static final int AVAILABLE = 0;
    public static final int EXECUTING = 1;
    public static final int ENDED = 2;

    public ActorStatus() {
        status = new AtomicInteger(AVAILABLE);
    }

    private AtomicInteger status;
    public AtomicInteger getStatus() {
        return status;
    }
}

interface IActor {
    // queue size
    long messageCount();

    // if the message queue is empty
//    boolean isEmpty();

    // if someone has stopped the actor
    boolean isEnded();

    // return the current status (defined in ActorStatus) of the actor
    AtomicInteger getStatus();

    // the work that the actor needs to do
    void execute();
}

public abstract class Actor<E> implements IActor{
    // Actor can have its own way to queue messages. We choose ConcurrentLinkedQueue, it could also be a BlockingQueue.
    private ConcurrentLinkedQueue<E> queue;
    private ActorStatus status;
    private boolean ended;

    @Override
    public long messageCount() {
        return queue.size();
    }

    public Actor() {
        queue = new ConcurrentLinkedQueue<>();
        status = new ActorStatus();
        ended = false;
    }

//    @Override
//    public boolean isEmpty() {
//        return queue.isEmpty();
//    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    public void stop() {
        ended = true;
    }

    @Override
    public AtomicInteger getStatus() {
        return status.getStatus();
    }

    public abstract void doWork(E message);

    @Override
    public void execute() {
        E message = queue.poll();

        if (message != null) {
            doWork(message);
        }
    }

    public void addMessage(E message) {
        queue.offer(message);
        GateKeeper.readyToExecute(this);
    }
}

class GateKeeper {
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * make sure the actor status is correct, and run the actor in a thread pool thread
     * @param actor
     */
    public static void readyToExecute(final IActor actor) {
        AtomicInteger atom = actor.getStatus();
        if (actor.isEnded()) {
            atom.set(ActorStatus.ENDED);
            return;
        }

        // only available actor can do work
        if (atom.compareAndSet(ActorStatus.AVAILABLE, ActorStatus.EXECUTING)) {
            executorService.execute(() -> Execute(actor));
        }
    }

    /**
     * make sure the actor status is correct after the execution, and check if it is available to run again.
     * @param actor
     */
    static void Execute(IActor actor) {
        actor.execute();

        AtomicInteger atom = actor.getStatus();
        if (actor.isEnded()) {
            atom.set(ActorStatus.ENDED);
            return;
        }

        atom.set(ActorStatus.AVAILABLE);

        // Call readyToExecute() again is to
        // 1) ensure the fairness of all the actors for the thread pool
        // 2) make sure this model works for tons of actors sharing a small thread pool
        // We can make some changes here so it can reduce scheduling cost by going directly into the message queue
        // to pull the next message.
        if (actor.messageCount() > 0) {
            readyToExecute(actor);
        }
    }
}