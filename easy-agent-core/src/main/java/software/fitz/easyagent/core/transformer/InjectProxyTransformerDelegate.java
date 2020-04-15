package software.fitz.easyagent.core.transformer;

import software.fitz.easyagent.core.asm.ASMContext;
import software.fitz.easyagent.core.classloader.AgentClassLoader;
import software.fitz.easyagent.core.asm.AddProxyClassVisitor;
import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.core.interceptor.InterceptorRegistry;
import software.fitz.easyagent.api.logging.AgentLogger;
import software.fitz.easyagent.api.logging.AgentLoggerFactory;
import software.fitz.easyagent.core.model.InstrumentClass;
import software.fitz.easyagent.core.model.InterceptorDefinition;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import software.fitz.easyagent.core.model.InternalTransformDefinition;
import software.fitz.easyagent.core.util.ReflectionUtils;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;

public class InjectProxyTransformerDelegate implements TransformerDelegate {

    private static final AgentLogger LOGGER = AgentLoggerFactory.getDefaultLogger();

    private InterceptorRegistry interceptorRegistry;

    public InjectProxyTransformerDelegate(InterceptorRegistry interceptorRegistry) {
        this.interceptorRegistry = interceptorRegistry;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String internalClassName,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer,
                            ClassReader cr,
                            InternalTransformDefinition transformDefinition) {

        LOGGER.info("\"" + internalClassName + "\" matched class. start transforming : " + loader);

        try {
            InstrumentClass instrumentClass = InstrumentClass.fromInternalName(internalClassName);
            List<InterceptorDefinition> interceptorDefinitionList = new ArrayList<>();

            for (AroundInterceptor interceptor : transformDefinition.getInterceptorList()) {
                InstrumentClass interceptorInstClass = InstrumentClass.fromClassName(interceptor.getClass().getName());

                // Reload interceptor class by same classloader as target class.
                // If target class is loaded by bootstrap classloader, it is replaced application classloader.
                ClassLoader parent = loader == null ? Thread.currentThread().getContextClassLoader() : loader;
                Class reloadedClass = ReflectionUtils.reloadClass(interceptorInstClass.getName(), parent, protectionDomain);
                AroundInterceptor reloadedInterceptor = (AroundInterceptor) ReflectionUtils.newInstance(reloadedClass);
                ReflectionUtils.copyAllField(interceptor, reloadedInterceptor);

                int id = interceptorRegistry.getNextId();

                InterceptorDefinition interceptorDefinition = new InterceptorDefinition(id, interceptor,
                        reloadedInterceptor, interceptorInstClass);
                interceptorRegistry.register(id, interceptorDefinition);

                interceptorDefinitionList.add(interceptorDefinition);
            }

            ClassWriter cw = new ClassWriter(0);
            ClassVisitor cv = new AddProxyClassVisitor(ASMContext.ASM_VERSION, cw, instrumentClass,
                    transformDefinition.getTargetMethodName(), transformDefinition.getMethodArgTypes(),
                    transformDefinition.getMethodReturnType(), interceptorDefinitionList);

            cr.accept(cv, EXPAND_FRAMES);

            return cw.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }

        throw new Error("No executed it.");
    }
}
