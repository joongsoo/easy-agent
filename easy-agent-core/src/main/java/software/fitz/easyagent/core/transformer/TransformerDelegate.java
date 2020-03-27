package software.fitz.easyagent.core.transformer;

import org.objectweb.asm.ClassReader;
import software.fitz.easyagent.core.model.InternalTransformDefinition;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public interface TransformerDelegate {

    byte[] transform(ClassLoader loader,
                     String className,
                     Class<?> classBeingRedefined,
                     ProtectionDomain protectionDomain,
                     byte[] classfileBuffer,
                     ClassReader classReader,
                     InternalTransformDefinition transformDefinition) throws IllegalClassFormatException;
}
