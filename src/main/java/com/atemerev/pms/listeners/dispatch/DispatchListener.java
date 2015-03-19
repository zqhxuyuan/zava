package com.atemerev.pms.listeners.dispatch;

import com.atemerev.pms.Asynchronous;
import com.atemerev.pms.Listener;
import com.atemerev.pms.listeners.MessageListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Dispatch messages to their appropriate handler messages and (stay with us,
 * because here things are coming little crazy) execute it within
 * gracefully provided executor (default is current thread, but who stays with
 * defaults these times?) Armed with this highly magical class, you can easily
 * write asynchronous events processing with arbitrary complexity. Isn't it
 * a little exciting?
 * <p/>
 *
 * @author Alexander Kuklev
 * @author Alexander Temerev
 * @version $Id:$
 */
public class DispatchListener implements MessageListener {

    private static final String LOCAL = "local"; // Magic workaround
    private boolean async = false;

    protected Executor executor;
    protected Object listener;

    // Yay, a meta-Java!
    protected Map<Class, Method> dispatchTable = new HashMap<Class, Method>();

    /**
     * Create new dispatch listener and route message handling to its own
     * methods. Obviously, you will not want to use this constructor directly.
     * Instead, extend DispatchListener with your own class and write handler
     * methods right there.
     */
    public DispatchListener() {
        this(LOCAL);
    }

    /**
     * Create new dispatch listener with default (this-thread synchronous)
     * execution. Populate the method dispatch table.
     *
     * @param listener The actual listener instance, the object of a class
     *                 with @Listener-annotated methods.
     */
    public DispatchListener(Object listener) {
        if (listener == LOCAL) {
            listener = this;
        }
        this.listener = listener;
        for (Class aClass = listener.getClass();
             aClass != null;
             aClass = aClass.getSuperclass()) {
            for (Method method : aClass.getDeclaredMethods()) {
                Listener listenerAnnotation
                        = method.getAnnotation(Listener.class);
                Class[] parameterTypes = method.getParameterTypes();
                if (listenerAnnotation != null
                        && parameterTypes.length >= 1) {
                    Class messageType = parameterTypes[0];
                    Method handler = dispatchTable.get(messageType);
                    if (handler == null) {
                        dispatchTable.put(messageType, method);
                    }
                }
            }
        }
    }

    /**
     * Create new async dispatcher and initialize it with specified Executor.
     *
     * @param executor java.util.concurrent.Executor instance.
     * @param async    If true, all listeners will be executed through the supplied executor, regardless of
     *                 their @Asynchronous annotation.
     */
    public DispatchListener(Executor executor, boolean async) {
        this(executor);
        this.async = async;
    }

    /**
     * Create new async dispatcher and initialize it with specified Executor.
     *
     * @param executor java.util.concurrent.Executor instance.
     */
    public DispatchListener(Executor executor) {
        this();
        setExecutor(executor);
    }

    /**
     * Create new async dispatcher with specified actual listener and
     * executor for message handling.
     *
     * @param listener Object with @Listener-annotated methods to handle
     *                 incoming messages.
     * @param executor Executor to run these methods in.
     */
    public DispatchListener(Object listener, Executor executor) {
        this(listener);
        setExecutor(executor);
    }

    /**
     * Dispatch message to appropriate message handler and execute it with your
     * very own highly kosher executor.
     *
     * @param message Message to process.
     */
    public void processMessage(final Object message) {
        final Method method = findCorrespondingMethod(message);
        if (method != null) {
            if (executor == null
                    || (method.getAnnotation(Asynchronous.class) == null && !async)) {
                invoke(listener, method, message);
            } else {
                executor.execute(new Runnable() {
                    public void run() {
                        invoke(listener, method, message);
                    }
                });
            }
        }
    }

    /**
     * Set your own executor for async message handling, so you won't have to
     * wait until processing unblocks.
     *
     * @param executor Executor to handle messages.
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * Process message and dispatch it to one of your custom-written
     * tailor-made Swiss-precise message handlers. And
     * yes, some magic happens here, with reflections and stuff.
     *
     * @param message Message to dispatch to appropriate listener.
     * @return Target method, or null if none found.
     */
    protected Method findCorrespondingMethod(Object message) {
        for (Class aClass = message.getClass();
             aClass != null; aClass = aClass.getSuperclass()) {
            Method method = dispatchTable.get(aClass);
            if (method != null) {
                return method;
            }
            for (Class anInterface : aClass.getInterfaces()) {
                method = dispatchTable.get(anInterface);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Invoke the found method and rethrow the exceptions as runtime.
     *
     * @param listener Listener with the method.
     * @param method   Method to invoke.
     * @param message  Message to pass to this method.
     */
    protected final void invoke(Object listener,
                                Method method, Object message) {
        if (listener == null || method == null) {
            return;
        }
        try {
            method.setAccessible(true);
            method.invoke(listener, message);
        } catch (IllegalAccessException e1) {
            // can never occur due to setAccessible(true);
        } catch (InvocationTargetException e1) {
            throw new RuntimeException(e1);
        }
    }
}