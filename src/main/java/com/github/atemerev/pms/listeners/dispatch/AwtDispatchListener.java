package com.github.atemerev.pms.listeners.dispatch;

import com.github.atemerev.pms.Asynchronous;

import java.lang.reflect.Method;

/**
 * All listener methods in a class extending AwtDispatchListener are
 * executed in Swing/AWT dispatch thread regardless of their @Asynchronous
 * annotations. You can still specify another executor in the constructors; in this case,
 */
public class AwtDispatchListener extends DispatchListener {

    private AwtDispatchThreadExecutor edtExecutor = new AwtDispatchThreadExecutor();

    /**
     * Create new Swing dispatch listener.
     */
    public AwtDispatchListener() {
        super();
    }

    /**
     * Create new AWT dispatch listener with specified listener object.
     *
     * @param listener Object with @Listener-annotated methods to handle
     *                 incoming messages (in the EDT).
     */
    public AwtDispatchListener(Object listener) {
        super(listener);
    }

    /**
     * Set EDT executor to use "SwingUtils.invokeAndWait()" instead of
     * "SwingUtils.invokeLater()". Use only when you know what are you doing.
     *
     * @param value Is false (default) or true.
     */
    public void setInvokeAndWait(boolean value) {
        edtExecutor.setUseInvokeAndWait(value);
    }

    /**
     * Dispatch and handle message in the AWT dispatch thread.
     *
     * @param message Message to process.
     */
    public void processMessage(final Object message) {
        final Method method = findCorrespondingMethod(message);
        if (method != null) {
            if (executor == null
                    || (method.getAnnotation(Asynchronous.class) == null)) {
                edtExecutor.execute(new Runnable() {
                    public void run() {
                        invoke(listener, method, message);
                    }
                });
            } else {
                executor.execute(new Runnable() {
                    public void run() {
                        invoke(listener, method, message);
                    }
                });
            }
        }
    }
}
