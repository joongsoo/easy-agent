package software.fitz.easyagent.api.logging;

public class AgentLoggerFactory {

    private static final AgentLogger DEFAULT_LOGGER = new DefaultAgentLogger();

    public static AgentLogger getDefaultLogger() {
        return DEFAULT_LOGGER;
    }
}