package com.l2jserver.common;

import org.slf4j.LoggerFactory;

public class Log {

    private static final String tagFormat = "{} ";

    private String tag;
    private org.slf4j.Logger logger;

    public Log(String tag, Class<?> clazz) {
        this.tag = tag;
        this.logger = LoggerFactory.getLogger(clazz.getSimpleName());
    }

    public void trace(String msg) {
        logger.trace(tagFormat + msg, tag);
    }

    public void trace(String format, Object arg) {
        logger.trace(tagFormat + format, tag, arg);
    }

    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(tagFormat + format, tag, arg1, arg2);
    }

    public void trace(String format, Object... arguments) {
        logger.trace(tagFormat + format, tag, arguments);
    }

    public void trace(String msg, Throwable t) {
        logger.trace(tagFormat + msg, tag, t);
    }

    public void debug(String msg) {
        logger.debug(tagFormat + msg, tag);
    }

    public void debug(String format, Object arg) {
        logger.debug(tagFormat + format, tag, arg);
    }

    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(tagFormat + format, tag, arg1, arg2);
    }

    public void debug(String format, Object... arguments) {
        logger.debug(tagFormat + format, tag, arguments);
    }

    public void debug(String msg, Throwable t) {
        logger.debug(tagFormat + msg, tag, t);
    }

    public void info(String msg) {
        logger.info(tagFormat + msg, tag);
    }

    public void info(String format, Object arg) {
        logger.info(tagFormat + format, tag, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
        logger.info(tagFormat + format, tag, arg1, arg2);
    }

    public void info(String format, Object... arguments) {
        logger.info(tagFormat + format, tag, arguments);
    }

    public void info(String msg, Throwable t) {
        logger.info(tagFormat + msg, tag, t);
    }

    public void warn(String msg) {
        logger.warn(tagFormat + msg, tag);
    }

    public void warn(String format, Object arg) {
        logger.warn(tagFormat + format, tag, arg);
    }

    public void warn(String format, Object... arguments) {
        logger.warn(tagFormat + format, tag, arguments);
    }

    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(tagFormat + format, tag, arg1, arg2);
    }

    public void warn(String msg, Throwable t) {
        logger.warn(tagFormat + msg, tag, t);
    }

    public void error(String msg) {
        logger.error(tagFormat + msg, tag);
    }

    public void error(String format, Object arg) {
        logger.error(tagFormat + format, tag, arg);
    }

    public void error(String format, Object arg1, Object arg2) {
        logger.error(tagFormat + format, tag, arg1, arg2);
    }

    public void error(String format, Object[] arguments) {
        logger.error(tagFormat + format, tag, arguments);
    }

    public void error(String msg, Throwable t) {
        logger.error(tagFormat + msg, tag, t);
    }

}
