package software.fitz.easyagent.api.logging;

public interface AgentLogger {

    void debug(String msg);

    void info(String msg);

    void warn(String msg);

    void warn(String msg, Throwable t);

    void error(String msg);

    void error(String msg, Throwable t);
}
