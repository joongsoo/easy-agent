package software.fitz.easyagent.core.strategy.impl;

import software.fitz.easyagent.core.strategy.TransformStrategy;
import software.fitz.easyagent.core.util.ClassUtils;

import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NameBaseTransformStrategy implements TransformStrategy {
    private final Map<String, String> superClassMap = new ConcurrentHashMap<>();
    private static final String END = "";

    private String internalClassName;
    private boolean applyChild;

    public NameBaseTransformStrategy(String classNamePattern) {
        String internalClassNamePattern = ClassUtils.toInternalName(classNamePattern);

        this.applyChild = internalClassNamePattern.endsWith("+");

        if (applyChild) {
            this.internalClassName = internalClassNamePattern.substring(0, internalClassNamePattern.length() - 1);
        } else {
            this.internalClassName = internalClassNamePattern;
        }

        superClassMap.putIfAbsent(this.internalClassName, END);
    }

    @Override
    public boolean isTransformTarget(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                     ProtectionDomain protectionDomain, byte[] classfileBuffer, String superClassName, String[] interfaces) {

        if (className != null && className.contains("fitz")) {
            System.err.println(className + " / " + internalClassName);
        }

        return this.internalClassName.equals(className)
                || (this.applyChild && registerIfChild(internalClassName, superClassName, interfaces));
    }

    private boolean registerIfChild(String internalClassName, String superClassName, String[] interfaces) {

        if (superClassMap.get(superClassName) != null) {
            superClassMap.put(internalClassName, superClassName);
            return true;
        }

        for (String i : interfaces) {
            if (superClassMap.get(i) != null) {
                superClassMap.put(internalClassName, i);
                return true;
            }
        }

        return false;
    }
}
