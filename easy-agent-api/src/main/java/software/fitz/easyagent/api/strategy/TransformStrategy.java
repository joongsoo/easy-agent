package software.fitz.easyagent.api.strategy;

import software.fitz.easyagent.api.strategy.impl.NameBaseTransformStrategy;

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
