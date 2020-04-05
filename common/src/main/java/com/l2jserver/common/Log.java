package com.l2jserver.common;

import org.slf4j.LoggerFactory;

public class Log {

    private String tag;
    private org.slf4j.Logger logger;

    public Log(String tag, Class<?> clazz) {
        this.tag = tag + " ";
        this.logger = LoggerFactory.getLogger(clazz.getSimpleName());
    }

    public void trace(String msg) {
        logger.trace(tag + msg, tag);
    }

    public void trace(String format, Object arg) {
        logger.trace(tag + format, arg);
    }

    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(tag + format, arg1, arg2);
    }

    public void trace(String format, Object... arguments) {
        logger.trace(tag + format, arguments);
    }

    public void trace(String msg, Throwable t) {
        logger.trace(tag + msg, t);
    }

    public void debug(String msg) {
        logger.debug(tag + msg, tag);
    }

    public void debug(String format, Object arg) {
        logger.debug(tag + format, arg);
    }

    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(tag + format, arg1, arg2);
    }

    public void debug(String format, Object... arguments) {
        logger.debug(tag + format, arguments);
    }

    public void debug(String msg, Throwable t) {
        logger.debug(tag + msg, t);
    }

    public void info(String msg) {
        logger.info(tag + msg, tag);
    }

    public void info(String format, Object arg) {
        logger.info(tag + format, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
        logger.info(tag + format, arg1, arg2);
    }

    public void info(String format, Object... arguments) {
        logger.info(tag + format, arguments);
    }

    public void info(String msg, Throwable t) {
        logger.info(tag + msg, t);
    }

    public void warn(String msg) {
        logger.warn(tag + msg, tag);
    }

    public void warn(String format, Object arg) {
        logger.warn(tag + format, arg);
    }

    public void warn(String format, Object... arguments) {
        logger.warn(tag + format, arguments);
    }

    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(tag + format, arg1, arg2);
    }

    public void warn(String msg, Throwable t) {
        logger.warn(tag + msg, t);
    }

    public void error(String msg) {
        logger.error(tag + msg, tag);
    }

    public void error(String format, Object arg) {
        logger.error(tag + format, arg);
    }

    public void error(String format, Object arg1, Object arg2) {
        logger.error(tag + format, arg1, arg2);
    }

    public void error(String format, Object... arguments) {
        logger.error(tag + format, arguments);
    }

    public void error(String msg, Throwable t) {
        logger.error(tag + msg, t);
    }

    public static Log of(String tag, Class<?> clazz) {
        return new Log(tag, clazz);
    }

}
