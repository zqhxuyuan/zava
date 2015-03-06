package org.zbus.common.logging;

public interface Logger { 
    boolean isFatalEnabled();
    boolean isErrorEnabled();
    boolean isWarnEnabled();
    boolean isInfoEnabled();
    boolean isDebugEnabled();
    boolean isTraceEnabled(); 

    void fatal(String msg);
    void fatal(String msg, Object ... args);
    void fatal(String msg, Throwable throwable);

    void error(String msg);
    void error(String format, Object ... args);
    void error(String msg, Throwable throwable);

    void warn(String msg);
    void warn(String msg, Object ... args);
    void warn(String msg, Throwable throwable);

    void info(String msg);
    void info(String msg, Object ... args);

    void debug(String msg);
    void debug(String msg, Object ... args);
    void debug(String msg, Throwable throwable);

    void trace(Object msg);
    void trace(String msg);
    void trace(String msg, Object ... args);
    void trace(String msg, Throwable throwable);
}
