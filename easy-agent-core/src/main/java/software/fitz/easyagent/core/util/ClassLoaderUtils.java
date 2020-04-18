package software.fitz.easyagent.core.util;

import software.fitz.easyagent.core.classloader.AgentClassLoader;

import java.io.IOException;
import java.security.ProtectionDomain;

public class ClassLoaderUtils {

    public static Class<?> reloadClass(String classInternalName, ClassLoader classLoader) {
        return reloadClass(classInternalName, classLoader, null);
    }

    public static Class<?> reloadClass(String classInternalName, ClassLoader classLoader, ProtectionDomain protectionDomain) {
        try {
            return AgentClassLoader.of(classLoader).define(classInternalName, protectionDomain);
        } catch (IOException e) {
            throw new RuntimeException("Class not found.", e);
        }
    }
}
