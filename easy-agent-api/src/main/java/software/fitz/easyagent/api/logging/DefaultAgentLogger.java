package software.fitz.easyagent.api.logging;

import software.fitz.easyagent.api.logging.appender.LogEvent;
import software.fitz.easyagent.api.logging.appender.SimpleStreamAppender;
import software.fitz.easyagent.api.prop.AgentProperties;
import software.fitz.easyagent.api.util.DateUtils;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DefaultAgentLogger implements AgentLogger {

    private static final ExecutorService WRITE_EXECUTOR = Executors.newSingleThreadExecutor();

    private Class<?> clazz;

    public DefaultAgentLogger() {
        this(null);
    }

    public DefaultAgentLogger(Class<?> clazz) {
        this.clazz = clazz;
        if (LogAppenderRegistry.getAppenderList() == null) {
            LogAppenderRegistry.setAppenderList(
                    Collections.unmodifiableList(Collections.singletonList(new SimpleStreamAppender())));
        }
    }

    @Override
    public void debug(String msg) {
        if (AgentProperties.DEBUG) {
            LogAppenderRegistry.getAppenderList().forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
                logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.DEBUG));
            }));
        }
    }

    @Override
    public void info(String msg) {
        LogAppenderRegistry.getAppenderList().forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.INFO));
        }));
    }

    @Override
    public void warn(String msg) {
        LogAppenderRegistry.getAppenderList().forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.WARN));
        }));
    }

    @Override
    public void warn(String msg, Throwable t) {
        LogAppenderRegistry.getAppenderList().forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.WARN, t));
        }));
    }

    @Override
    public void error(String msg) {
        LogAppenderRegistry.getAppenderList().forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.ERROR));
        }));
    }

    @Override
    public void error(String msg, Throwable t) {
        LogAppenderRegistry.getAppenderList().forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.ERROR, t));
        }));
    }
}
