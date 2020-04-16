package software.fitz.easyagent.api.logging.appender;

import software.fitz.easyagent.api.logging.LogLevel;

import java.time.ZonedDateTime;

public class LogEvent {

    private Class<?> clazz;
    private String msg;
    private ZonedDateTime dateTime;
    private LogLevel logLevel;
    private Throwable throwable;

    public LogEvent(String msg, ZonedDateTime dateTime, LogLevel logLevel) {
        this(null, msg, dateTime, logLevel, null);
    }

    public LogEvent(Class<?> clazz, String msg, ZonedDateTime dateTime, LogLevel logLevel) {
        this(clazz, msg, dateTime, logLevel, null);
    }

    public LogEvent(Class<?> clazz, String msg, ZonedDateTime dateTime, LogLevel logLevel, Throwable throwable) {
        this.clazz = clazz;
        this.msg = msg;
        this.dateTime = dateTime;
        this.logLevel = logLevel;
        this.throwable = throwable;
    }

    public String getMsg() {
        return msg;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
