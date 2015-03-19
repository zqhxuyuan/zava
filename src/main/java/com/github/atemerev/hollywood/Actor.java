package com.github.atemerev.hollywood;

import com.github.atemerev.pms.listeners.MessageListener;

import java.util.concurrent.Executor;

/**
 * Actor is the base class for all your own actors. You have to extend this class and provide <em>common interface</em>
 * and <em>states</em> (with implementation of common interface methods).
 * <p/>
 * You can't instantiate an Actor on your own since it's <b>always</b> an abstract class and doesn't provide
 * constructors. Instead, you should use following initialization call:
 * <p/>
 * <code>SomeActor actor = Hollywood.createActor(SomeActor.class)</code>
 * <p/>
 * Actor's base interface methods have the following signature:
 * <p/>
 * <code>public abstract Type method(args) [throws SomeException...]</code>
 * <p/>
 * An actor normally has multiple states (well, you could get with a single state, but then you don't really need
 * a full-scaled actor model, right?)
 * <p/>
 * States are implemented via internal classes <b>extending the current Actor subclass</b>. Thus if you have a
 * SomeActor implementation, your internal classes should have the following signature:
 * <p/>
 * <code>@State public static abstract class SomeInternalState extends SomeActor</code>
 * <p/>
 * (It's long and strange, but it has to be this way; it's Java, after all).
 * <p/>
 * One of the states should be marked with <code>@Initial</code> annotation -- it will be set right after the
 * initalization is complete. It's there you should set actor's dependencies and properties -- and you don't want
 * to define any additional behavior (as in implementing common interface methods -- except, maybe, the one method
 * that changes the state (see <code>setState(SomeState.class)</code> method).
 * <p/>
 * Other states can implement other methods of common interface. If some methods are not implemented (which is
 * possible since state subclasses are defined as abstract) -- they will throw UnsupportedOperationException on
 * attempt to call it from this specific state.
 * <p/>
 * For convenience, common interface methods can be marked with <code>@AllowedStates</code> annotation with a list
 * of state classes where this method is actually implemented. Currently, this annotation is ignored by Hollywood
 * framework and serves for documentation purposes only.
 * <p/>
 * Within the state methods (and only there), state can be changed with setState() method. If additional dependencies
 * should be set for specific state, it has to be done after <code>prepareState()</code> method call. Also, it's
 * <em>highly</em> recommended to change state in the last statement of the method (except for return). Otherwise you
 * have to be a certified magician.
 * <p/>
 * Commands which may change state <em>must</em> have the <code>synchrinized</code> modifier (to ensure that the order
 * of state transitions corresponds to the order the commands were called). Basic actor thread-safety (i.e. state
 * transition would wait for all running methods of the state to complete) is guaranteed by internal mechanisms, but
 * you still have to do your actor-specific synchronization manually if necessary. Synchronization bugs in Actors can
 * be really weird, so you are warned.
 *
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public abstract class Actor extends RootState implements MessageListener {

    /**
     * TODO
     *
     * @return Actor object instance.
     */
    protected abstract Actor me(); // Method will be hot-swapped in run-time.

    /**
     * Get a dummy object with current state class so it can be checked with <code>instanceof</code> if needed.
     * Do not attempt to call business methods from this object -- it won't work!
     *
     * @return Dummy object with current state class.
     */
    public abstract RootState state(); // Method will be hot-swapped in run-time.

    /**
     * Get dummy object with <em>previous</em> state class to be checked with <code>instanceof</code>.
     *
     * @return Dummy object with previous state class.
     */
    public abstract RootState prevState(); // Method will be hot-swapped in run-time.

    /**
     * Initialize state from specified state class.
     * This method is necessary if you need to set state dependencies before switching to it.
     *
     * @param stateClass State class.
     * @return State object.
     */
    protected abstract <T extends RootState> T prepareState(Class<T> stateClass);
    // Method will be hot-swapped in run-time.

    /**
     * Sets new state. This method can be called only within the state itself.
     * <p/>
     * The state object should first be retrieved with <code>prepareState</code> method
     * and all dependencies has to be set beforehand:
     * <pre>
     *   newState = prepareState(SomeState.class);
     *   newState.field = value;
     *   newState.otherField = othervalue;
     *   setState(newState);
     *   return;
     * </pre>
     * <p/>
     * Note that fields which are common in present state and target state by some common ancestor
     * will be transported automatically.
     * <p/>
     * After the new state is set, you should immediately return, because the running context of the method
     * is invalidated. You may return some constant, some variable, or the result of some method in the new
     * state. If you want to conclude state change by some other actions, write a member in the new state
     * and call it by
     * <pre>
     *   return setState(SomeState.class).methodConclusion();
     * </pre>
     * If you don't want strange heisenbugs, please obey this rule.
     *
     * @param state Initialized state object.
     * @return State set. The same as argument in most cases, null if the state was changed while onExit()s and onEnter()s were called.
     */
    protected abstract <T extends RootState> T setState(T state); // Method will be hot-swapped in run-time.

    /**
     * Sets new state. Used when no fields of the new state must be initialized, i.e. just shorthand for
     * <code>setState(prepareState(state))</code>
     *
     * @param state State class to set.
     * @return State set.
     */
    protected final <T extends RootState> T setState(Class<T> state) {
        return setState(prepareState(state));
    }

    /**
     * States can react to PMS messages. Actor is a DispatchListener itself, and can act as listener delegate
     * in a common way. See PMS documentation for details.
     *
     * @param o Message object.
     */
    public abstract void processMessage(Object o); // Method will be hot-swapped in run-time.

    /**
     * You can provide your own executor for PMS message handling by overriding this method.
     *
     * @return An Executor instance.
     */
    protected Executor asyncListenerExecutor() {
        return null;
    }    
}