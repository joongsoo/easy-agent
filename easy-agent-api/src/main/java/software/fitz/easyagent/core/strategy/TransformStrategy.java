package software.fitz.easyagent.core.strategy;

import software.fitz.easyagent.core.strategy.impl.NameBaseTransformStrategy;

import java.security.ProtectionDomain;

public interface TransformStrategy {

    static TransformStrategy className(String classNamePattern) {
        return new NameBaseTransformStrategy(classNamePattern);
    }

    boolean isTransformTarget(ClassLoader loader,
                     String className,
                     Class<?> classBeingRedefined,
                     ProtectionDomain protectionDomain,
                     byte[] classfileBuffer,
                     String superClassName,
                     String[] interfaces);
}
