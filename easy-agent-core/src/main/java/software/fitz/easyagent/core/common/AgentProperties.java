package software.fitz.easyagent.core.common;

public class AgentProperties {

    public static final boolean DEBUG;

    static {
        String debug = System.getProperty("easyagent.debug");

        if (debug != null) {
            DEBUG = Boolean.parseBoolean(debug);
        } else {
            DEBUG = false;
        }
    }
}
