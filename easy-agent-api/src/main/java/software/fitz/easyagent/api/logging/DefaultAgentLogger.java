package software.fitz.easyagent.api.logging;

import software.fitz.easyagent.api.logging.appender.LogAppender;
import software.fitz.easyagent.api.logging.appender.LogEvent;
import software.fitz.easyagent.api.prop.AgentProperties;
import software.fitz.easyagent.api.util.DateUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DefaultAgentLogger implements AgentLogger {

    private static final ExecutorService WRITE_EXECUTOR = Executors.newSingleThreadExecutor();

    private final Class<?> clazz;
    private final List<LogAppender> appenderList;

    public DefaultAgentLogger() {
        this(null);
    }

    public DefaultAgentLogger(Class<?> clazz) {
        this.clazz = clazz;
        this.appenderList = LogAppenderRegistry.getOriginalAppenderList();
    }

    @Override
    public void debug(String msg) {
        if (AgentProperties.DEBUG) {
            appenderList.forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
                logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.DEBUG));
            }));
        }
    }

    @Override
    public void info(String msg) {
        appenderList.forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.INFO));
        }));
    }

    @Override
    public void warn(String msg) {
        appenderList.forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.WARN));
        }));
    }

    @Override
    public void warn(String msg, Throwable t) {
        appenderList.forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.WARN, t));
        }));
    }

    @Override
    public void error(String msg) {
        appenderList.forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.ERROR));
        }));
    }

    @Override
    public void error(String msg, Throwable t) {
        appenderList.forEach(logAppender -> WRITE_EXECUTOR.execute(() -> {
            logAppender.append(new LogEvent(clazz, msg, DateUtils.currentDateTime(), LogLevel.ERROR, t));
        }));
    }
}
