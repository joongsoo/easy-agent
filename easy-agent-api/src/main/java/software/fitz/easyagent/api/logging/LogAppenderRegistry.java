package software.fitz.easyagent.api.logging;

import software.fitz.easyagent.api.logging.appender.LogAppender;
import software.fitz.easyagent.api.logging.appender.SimpleStreamAppender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogAppenderRegistry {

    private volatile static List<LogAppender> APPENDER_LIST;

    public synchronized static void init(List<LogAppender> appenderList) {

        if (APPENDER_LIST != null) {
            throw new IllegalStateException("Appender list already set.");
        }

        APPENDER_LIST = Collections.unmodifiableList(appenderList);
    }

    static List<LogAppender> getOriginalAppenderList() {

        if (APPENDER_LIST == null) {
            init(Arrays.asList(new SimpleStreamAppender()));
        }

        return APPENDER_LIST;
    }

    public static List<LogAppender> getAppenderList() {
        return new ArrayList<>(APPENDER_LIST);
    }
}
