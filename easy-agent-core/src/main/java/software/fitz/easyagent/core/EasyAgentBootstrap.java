package software.fitz.easyagent.core;

import software.fitz.easyagent.core.transformer.InjectProxyTransformerDelegate;
import software.fitz.easyagent.core.transformer.TransformerDelegate;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class EasyAgentBootstrap {

    private static boolean started = false;

    private final String agentArgs;
    private final Instrumentation inst;
    private final List<Plugin> pluginList = new ArrayList<>();

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

    public void start() {

        if (inst == null) {
            throw new IllegalStateException("Instrumentation must not be null!");
        }

        start(agentArgs, inst, pluginList);
    }

    private static void start(String agentArgs, Instrumentation inst, List<Plugin> pluginList) {
        if (started) {
            return;
        }

        // 에이전트는 시스템 클래스도 변환할 수 있어야하기에 Bootstrap 클래스로더로 로딩해야한다.
        if (Object.class.getClassLoader() != EasyAgentBootstrap.class.getClassLoader()) {
            System.err.println("[EASY_AGENT] Invalid jar : Bootstrap class must loaded by bootstrap classloader");
            System.exit(1);
            return;
        }

        TransformerDelegate transformerDelegate = InjectProxyTransformerDelegate.INSTANCE;
        TransformerManager transformerManager = TransformerManager.INSTANCE;

        for (Plugin plugin : pluginList) {
            plugin.setup(transformerManager);
        }

        inst.addTransformer(new AgentClassFileTransformer(transformerManager, transformerDelegate), true);

        started = true;
    }
}
