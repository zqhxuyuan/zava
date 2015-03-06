package org.zbus.common.logging.impl;

import java.util.Arrays;
import java.util.IllegalFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JLogger implements org.zbus.common.logging.Logger {
    protected final Logger logger;

    public Slf4JLogger(String category) {
        logger = LoggerFactory.getLogger(category);
    }

    public Slf4JLogger(Class<?> category) {
        logger = LoggerFactory.getLogger(category.getName()); // fix for https://jira.jboss.org/browse/JGRP-1224
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return logger.isErrorEnabled();
    }

    public void trace(String msg) {
        logger.trace(msg);
    }

    public void trace(String msg, Object... args) {
        if(isTraceEnabled())
            logger.trace(format(msg, args));
    }

    public void trace(Object msg) {
    	logger.trace(msg.toString());
    }

    public void trace(String msg, Throwable t) {
    	logger.trace(msg, t);
    }

    public void debug(String msg) {
        logger.debug(msg);;
    }

    public void debug(String msg, Object... args) {
        if(isDebugEnabled())
            logger.debug(format(msg, args));
    }

    public void debug(String msg, Throwable t) {
    	logger.debug(msg, t);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String msg, Object... args) {
        if(isInfoEnabled())
        	logger.info(format(msg, args));
    }

    public void warn(String msg) {
    	logger.warn(msg);
    }

    public void warn(String msg, Object... args) {
        if(isWarnEnabled())
        	logger.warn(format(msg, args));
    }

    public void warn(String msg, Throwable t) {
    	logger.warn(msg, t);
    }

    public void error(String msg) {
    	logger.warn(msg);
    }

    public void error(String format, Object... args) {
        if(isErrorEnabled())
        	logger.error(format(format, args));
    }

    public void error(String msg, Throwable t) {
    	logger.error(msg, t);
    }

    public void fatal(String msg) {
    	logger.error(msg);
    }

    public void fatal(String msg, Object... args) {
        if(isFatalEnabled())
        	logger.error(format(msg, args));
    }

    public void fatal(String msg, Throwable t) {
    	logger.error(msg, t);
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
