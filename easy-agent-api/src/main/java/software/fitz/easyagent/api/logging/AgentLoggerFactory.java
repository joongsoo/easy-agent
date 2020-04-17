package software.fitz.easyagent.api.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentLoggerFactory {

    private static final Map<Class<?>, AgentLogger> LOGGER_CACHE = new ConcurrentHashMap<>();
    private static final AgentLogger DEFAULT_LOGGER = new DefaultAgentLogger();

    public static AgentLogger getLogger() {
        return DEFAULT_LOGGER;
    }

    public static AgentLogger getLogger(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("class must not be null");
        }
        return LOGGER_CACHE.computeIfAbsent(clazz, k -> new DefaultAgentLogger(k));
    }
}
