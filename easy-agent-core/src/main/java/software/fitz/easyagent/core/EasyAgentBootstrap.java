package software.fitz.easyagent.core;

import software.fitz.easyagent.api.Plugin;
import software.fitz.easyagent.api.interceptor.exception.ExceptionHandler;
import software.fitz.easyagent.api.logging.AgentLogger;
import software.fitz.easyagent.api.logging.AgentLoggerFactory;
import software.fitz.easyagent.core.transformer.InjectProxyTransformerDelegate;
import software.fitz.easyagent.core.transformer.TransformerDelegate;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class EasyAgentBootstrap {

    private static final AgentLogger LOGGER = AgentLoggerFactory.getLogger();
    private static boolean started = false;

    private final String agentArgs;
    private final Instrumentation inst;
    private final List<Plugin> pluginList = new ArrayList<>();
    private final List<ExceptionHandler> exceptionHandlerList = new ArrayList<>();

    public EasyAgentBootstrap(String agentArgs, Instrumentation inst) {
        this.agentArgs = agentArgs;
        this.inst = inst;
    }

    public EasyAgentBootstrap addPlugin(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("plugin must not be null!");
        }

        this.pluginList.add(plugin);

        return this;
    }

    public EasyAgentBootstrap addExceptionHandler(ExceptionHandler exceptionHandler) {
        if (exceptionHandler == null) {
            throw new NullPointerException("exceptionHandler must not be null!");
        }

        this.exceptionHandlerList.add(exceptionHandler);

        return this;
    }

    public void start() {

        if (inst == null) {
            throw new IllegalStateException("Instrumentation must not be null!");
        }

        start(agentArgs, inst, pluginList, exceptionHandlerList);
    }

    private synchronized static void start(String agentArgs, Instrumentation inst, List<Plugin> pluginList, List<ExceptionHandler> exceptionHandlerList) {
        if (started) {
            return;
        }

        if (Object.class.getClassLoader() != EasyAgentBootstrap.class.getClassLoader()) {
            LOGGER.error("Invalid jar : Bootstrap class must loaded by bootstrap classloader");
            System.exit(1);
            return;
        }

        TransformerDelegate transformerDelegate = new InjectProxyTransformerDelegate(InterceptorRegistryDelegate.INTERCEPTOR_REGISTRY);
        TransformerManager transformerManager = TransformerManager.INSTANCE;

        for (Plugin plugin : pluginList) {
            plugin.setup(transformerManager);
        }

        for (ExceptionHandler exceptionHandler : exceptionHandlerList) {
            ExceptionPublisherDelegate.register(exceptionHandler);
        }

        inst.addTransformer(new AgentClassFileTransformer(transformerManager, transformerDelegate), true);

        started = true;
    }
}
