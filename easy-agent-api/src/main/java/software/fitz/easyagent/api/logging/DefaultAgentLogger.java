package software.fitz.easyagent.api.logging;

import software.fitz.easyagent.api.prop.AgentProperties;
import software.fitz.easyagent.api.util.DateUtils;

import java.io.PrintStream;

public class DefaultAgentLogger implements AgentLogger {

    private static final String LOG_FORMAT = "[EASY-AGENT][%s][%s] - %s";

    private final PrintStream outputStream;
    private final PrintStream errStream;

    public DefaultAgentLogger() {
        this(System.out, System.err);
    }

    public DefaultAgentLogger(PrintStream outputStream, PrintStream errStream) {
        this.outputStream = outputStream;
        this.errStream = errStream;
    }

    @Override
    public void debug(String msg) {
        if (AgentProperties.DEBUG) {
            outputStream.println(format(LogLevel.DEBUG, msg));
        }
    }

    @Override
    public void info(String msg) {
        outputStream.println(format(LogLevel.INFO, msg));
    }

    @Override
    public void warn(String msg) {
        errStream.println(format(LogLevel.WARN, msg));
    }

    @Override
    public void warn(String msg, Throwable t) {
        synchronized (errStream) {
            errStream.println(format(LogLevel.WARN, msg));
            t.printStackTrace(errStream);
        }
    }

    @Override
    public void error(String msg) {
        errStream.println(format(LogLevel.ERROR, msg));
    }

    @Override
    public void error(String msg, Throwable t) {
        synchronized (errStream) {
            errStream.println(format(LogLevel.ERROR, msg));
            t.printStackTrace(errStream);
        }
    }

    private String format(LogLevel logLevel, String msg) {
        return String.format(LOG_FORMAT, DateUtils.currentDateTime(), logLevel, msg);
    }
}
