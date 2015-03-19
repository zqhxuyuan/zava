package com.github.atemerev.pms.listeners.dispatch;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

/**
 * Executor for running commands at AWT dispatch thread.
 * Useful for GUI synchronization.
 *
 * @author Alexander Temerev
 * @version $Id:$
 */
public class AwtDispatchThreadExecutor implements Executor {

    private boolean useInvokeAndWait = false;

    /**
     * Execute command within AWT dispatch thread.
     *
     * @param command Command to execute.
     */
    public void execute(Runnable command) {
        try {
            if (useInvokeAndWait) {
                SwingUtilities.invokeAndWait(command);
            } else {
                SwingUtilities.invokeLater(command);
            }
        } catch (InterruptedException e) {
            // Ignore.
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Use invokeAndWait() instead of invokeLater() execution policy for
     * EDT—which leads to synchronous execution. May cause GUI freezing.
     * Default is false. Candle with hare.
     *
     * @param useInvokeAndWait True to execute command in EDT synchronously,
     * false otherwise.
     */
    public void setUseInvokeAndWait(boolean useInvokeAndWait) {
        this.useInvokeAndWait = useInvokeAndWait;
    }
}
