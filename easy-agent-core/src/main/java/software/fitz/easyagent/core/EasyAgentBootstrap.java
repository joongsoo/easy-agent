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

    public synchronized static EasyAgentBootstrapInitializer initialize(String agentArgs, Instrumentation instrumentation) {
        if (started) {
            throw new IllegalStateException("EasyAgentBootstrap already started!");
        }

        return new EasyAgentBootstrapInitializer(agentArgs, instrumentation);
    }

    private synchronized static void start(String agentArgs, Instrumentation inst, List<Plugin> pluginList, List<ExceptionHandler> exceptionHandlerList) {
        if (started) {
            throw new IllegalStateException("EasyAgentBootstrap already started!");
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

    public static class EasyAgentBootstrapInitializer {
        private final String agentArgs;
        private final Instrumentation inst;
        private final List<Plugin> pluginList = new ArrayList<>();
        private final List<ExceptionHandler> exceptionHandlerList = new ArrayList<>();

        private EasyAgentBootstrapInitializer(String agentArgs, Instrumentation inst) {
            this.agentArgs = agentArgs;
            this.inst = inst;
        }

        public EasyAgentBootstrapInitializer addPlugin(Plugin plugin) {
            if (plugin == null) {
                throw new NullPointerException("plugin must not be null!");
            }

            this.pluginList.add(plugin);

            return this;
        }

        public EasyAgentBootstrapInitializer addExceptionHandler(ExceptionHandler exceptionHandler) {
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

            if (this.pluginList.isEmpty()) {
                throw new IllegalStateException("Plugin is must be at least 1");
            }

            EasyAgentBootstrap.start(agentArgs, inst, pluginList, exceptionHandlerList);
        }
    }
}
