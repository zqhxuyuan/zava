package org.zbus.common.logging.impl;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.logging.Level; 
import java.util.logging.Logger;

public class JDKLogger implements org.zbus.common.logging.Logger {
    protected final Logger logger;

    public JDKLogger(String category) {
        logger = Logger.getLogger(category);
    }

    public JDKLogger(Class<?> category) {
        logger = Logger.getLogger(category.getName()); // fix for https://jira.jboss.org/browse/JGRP-1224
    }

    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINER);
    }

    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    public boolean isFatalEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    public void trace(String msg) {
        logger.log(Level.FINER, msg);
    }

    public void trace(String msg, Object... args) {
        if(isTraceEnabled())
            logger.log(Level.FINER, format(msg, args));
    }

    public void trace(Object msg) {
        logger.log(Level.FINER, msg.toString());
    }

    public void trace(String msg, Throwable t) {
        logger.log(Level.FINER, msg, t);
    }

    public void debug(String msg) {
        logger.log(Level.FINE, msg);
    }

    public void debug(String msg, Object... args) {
        if(isDebugEnabled())
            logger.log(Level.FINE, format(msg, args));
    }

    public void debug(String msg, Throwable t) {
        logger.log(Level.FINE, msg, t);
    }

    public void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    public void info(String msg, Object... args) {
        if(isInfoEnabled())
            logger.log(Level.INFO, format(msg, args));
    }

    public void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    public void warn(String msg, Object... args) {
        if(isWarnEnabled())
            logger.log(Level.WARNING, format(msg, args));
    }

    public void warn(String msg, Throwable t) {
        logger.log(Level.WARNING, msg, t);
    }

    public void error(String msg) {
        logger.log(Level.SEVERE, msg);
    }

    public void error(String format, Object... args) {
        if(isErrorEnabled())
            logger.log(Level.SEVERE, format(format, args));
    }

    public void error(String msg, Throwable t) {
        logger.log(Level.SEVERE, msg, t);
    }

    public void fatal(String msg) {
        logger.log(Level.SEVERE, msg);
    }

    public void fatal(String msg, Object... args) {
        if(isFatalEnabled())
            logger.log(Level.SEVERE, format(msg, args));
    }

    public void fatal(String msg, Throwable t) {
        logger.log(Level.SEVERE, msg, t);
    } 

    protected String format(String format, Object... args) {
        try {
            return String.format(format, args);
        } catch(IllegalFormatException ex) {
            error("Illegal format string \"" + format + "\", args=" + Arrays.toString(args));
        } catch(Throwable t) {
            error("Failure formatting string: format string=" + format + ", args=" + Arrays.toString(args));
        }
        return format;
    } 
}
