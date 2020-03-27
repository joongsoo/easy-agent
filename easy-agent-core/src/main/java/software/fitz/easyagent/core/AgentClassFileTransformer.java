package software.fitz.easyagent.core;

import software.fitz.easyagent.core.model.InternalTransformDefinition;
import software.fitz.easyagent.core.strategy.TransformStrategy;
import software.fitz.easyagent.core.transformer.TransformerDelegate;
import org.objectweb.asm.ClassReader;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

class AgentClassFileTransformer implements ClassFileTransformer {

    private final TransformerManager transformerManager;
    private final TransformerDelegate transformerDelegate;


    public AgentClassFileTransformer(TransformerManager transformerManager, TransformerDelegate transformerDelegate) {
        this.transformerManager = transformerManager;
        this.transformerDelegate = transformerDelegate;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] classBuffer = classfileBuffer;
        ClassReader cr = new ClassReader(classBuffer);

        for (InternalTransformDefinition definition : transformerManager.getTransformDefinitionList()) {
            TransformStrategy transformStrategy = definition.getTransformStrategy();

            if (transformStrategy.isTransformTarget(loader, className, classBeingRedefined,
                    protectionDomain, classBuffer, cr.getSuperName(), cr.getInterfaces())){

                byte[] transformedClassBuffer = transformerDelegate.transform(loader, className, classBeingRedefined,
                        protectionDomain, classBuffer, cr, definition);

                // 만약 변환되지 않았다면 다음 인터셉터에서는 ClassReader를 사용해 굳이 클래스를 재파싱 할 필요가 없다.
                if (classBuffer != transformedClassBuffer && definition != transformerManager.getLast()) {
                    cr = new ClassReader(transformedClassBuffer);
                }

                classBuffer = transformedClassBuffer;
            }
        }

        return classBuffer;
    }
}
