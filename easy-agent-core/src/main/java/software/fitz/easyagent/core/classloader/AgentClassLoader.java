package software.fitz.easyagent.core.classloader;

import software.fitz.easyagent.api.util.ClassUtils;
import software.fitz.easyagent.core.util.IOUtils;

import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentClassLoader extends ClassLoader {

    private static final Map<String, Class> CLASS_CACHE = new ConcurrentHashMap<>();
    private static final Map<ClassLoader, AgentClassLoader> PARENT_CACHE = new ConcurrentHashMap<>();

    private AgentClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static AgentClassLoader of(ClassLoader parent) {
        return PARENT_CACHE.computeIfAbsent(parent, k -> new AgentClassLoader(parent));
    }

    public Class<?> define(String name, byte[] clazz, ProtectionDomain protectionDomain) {

        return CLASS_CACHE.computeIfAbsent(getParent().toString() + "#" + name, k -> {
                    try {
                        return getParent().loadClass(name);
                    } catch (ClassNotFoundException e) {
                        return defineClass(name, clazz, 0, clazz.length, protectionDomain);
                    }
                });
    }

    public Class<?> define(String name, ProtectionDomain protectionDomain) throws IOException {
        return define(
                name,
                IOUtils.toByteArray(getParent().getResourceAsStream(ClassUtils.toInternalName(name) + ".class")),
                protectionDomain);
    }
}
