package software.fitz.easyagent.api.logging;

import software.fitz.easyagent.api.logging.appender.LogAppender;

import java.util.Collections;
import java.util.List;

public class LogAppenderRegistry {

    private static List<LogAppender> APPENDER_LIST;

    public synchronized static void setAppenderList(List<LogAppender> appenderList) {
        if (APPENDER_LIST != null) {
            throw new IllegalStateException("Appender list already set.");
        }

        APPENDER_LIST = Collections.unmodifiableList(appenderList);
    }

    public static List<LogAppender> getAppenderList() {
        return APPENDER_LIST;
    }
}
