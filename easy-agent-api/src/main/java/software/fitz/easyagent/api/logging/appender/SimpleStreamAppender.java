package software.fitz.easyagent.api.logging.appender;

import software.fitz.easyagent.api.util.DateUtils;

import java.io.PrintStream;

public class SimpleStreamAppender implements LogAppender {

    private static final String LOG_FORMAT = "[EASY-AGENT][%s][%s] - %s";
    private static final String LOG_FORMAT_WITH_CLASS = "[EASY-AGENT][%s][%s][%s] - %s";

    private final PrintStream outputStream;
    private final PrintStream errStream;

    public SimpleStreamAppender() {
        this(System.out, System.err);
    }

    public SimpleStreamAppender(PrintStream outputStream, PrintStream errStream) {
        this.outputStream = outputStream;
        this.errStream = errStream;
    }

    @Override
    public void append(LogEvent logEvent) {
        switch (logEvent.getLogLevel()) {
            case DEBUG:
            case INFO:
                print(logEvent);
                break;
            case WARN:
            case ERROR:
                printError(logEvent);
                break;
        }
    }

    private void print(LogEvent logEvent) {
        outputStream.println(format(logEvent, logEvent.getMsg()));
    }

    private void printError(LogEvent logEvent) {
        synchronized (errStream) {
            errStream.println(format(logEvent, logEvent.getMsg()));

            if (logEvent.getThrowable() != null) {
                logEvent.getThrowable().printStackTrace(errStream);
            }
        }
    }

    private static String format(LogEvent logEvent, String msg) {
        if (logEvent.getClazz() == null) {
            return String.format(LOG_FORMAT,
                    DateUtils.formatYearToMillis(logEvent.getDateTime()), logEvent.getLogLevel(), msg);
        } else {
            return String.format(LOG_FORMAT_WITH_CLASS, logEvent.getClazz().getName(),
                    DateUtils.formatYearToMillis(logEvent.getDateTime()), logEvent.getLogLevel(), msg);
        }
    }
}
