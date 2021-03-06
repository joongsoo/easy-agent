package software.fitz.easyagent.core.asm;

import org.objectweb.asm.Type;
import software.fitz.easyagent.api.MethodDefinition;
import software.fitz.easyagent.api.logging.AgentLogger;
import software.fitz.easyagent.api.logging.AgentLoggerFactory;
import software.fitz.easyagent.core.InterceptorRegistryDelegate;
import software.fitz.easyagent.core.asm.helper.InterceptorByteCodeHelper;
import software.fitz.easyagent.core.asm.helper.ByteCodeHelper;
import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.core.asm.helper.MethodFilter;
import software.fitz.easyagent.core.model.InstrumentMethod;
import software.fitz.easyagent.api.util.ClassUtils;
import software.fitz.easyagent.core.model.InstrumentClass;
import software.fitz.easyagent.core.model.InterceptorDefinition;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.AFTER_METHOD_DESCRIPTOR;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.BEFORE_METHOD_DESCRIPTOR;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.THROWN_METHOD_DESCRIPTOR;

public class AddProxyClassVisitor extends ClassVisitor {

    private static final AgentLogger LOGGER = AgentLoggerFactory.getLogger();

    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    private static final String INTERCEPTOR_FIELD_NAME_FORMAT = "$$_easy_agent_interceptor_$$_%d";
    private static final String DELEGATE_BEFORE_FORMAT = "$$_easy_agent_before_$$_%d";
    private static final String DELEGATE_AFTER_FORMAT = "$$_easy_agent_after_$$_%d";
    private static final String DELEGATE_THROWN_FORMAT = "$$_easy_agent_thrown_$$_%d";

    private final long id;
    private final String interceptorFieldName;
    private final String delegateBefore;
    private final String delegateAfter;
    private final String delegateThrown;

    private ClassVisitor cv;
    private InstrumentClass classInfo;
    private boolean isInterface;
    private List<MethodDefinition> targetMethodList;
    private boolean applyAllMethodInClass;
    private List<InterceptorDefinition> interceptorList;
    private boolean visitedStaticBlock = false;

    public AddProxyClassVisitor(int api,
                                ClassVisitor cv,
                                InstrumentClass classInfo,
                                List<MethodDefinition> targetMethodList,
                                boolean applyAllMethodInClass,
                                List<InterceptorDefinition> interceptorList) {
        super(api, cv);
        this.cv = cv;
        this.classInfo = classInfo;
        this.targetMethodList = targetMethodList;
        this.applyAllMethodInClass = applyAllMethodInClass;
        this.interceptorList = interceptorList;

        this.id = ID_GENERATOR.getAndIncrement();
        this.interceptorFieldName = String.format(INTERCEPTOR_FIELD_NAME_FORMAT, id);
        this.delegateBefore = String.format(DELEGATE_BEFORE_FORMAT, id);
        this.delegateAfter = String.format(DELEGATE_AFTER_FORMAT, id);
        this.delegateThrown = String.format(DELEGATE_THROWN_FORMAT, id);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        // static field init
        if (!isInterface && "<clinit>".equals(name) &&  !visitedStaticBlock) {
            LOGGER.debug("\"" + classInfo.getName() + "\" Generate static field");

            visitedStaticBlock = true;
            MethodVisitor visitor = cv.visitMethod(access, name, descriptor, signature, exceptions);

            return new StaticInitAdviceAdapter(ASMContext.ASM_VERSION, visitor, access, name, descriptor);
        }

        // inject interceptor
        if (!isInterface && isMatchedMethod(access, name, descriptor)) {
            LOGGER.debug("Find matched method : " + name + descriptor);

            boolean isStatic = (access & ACC_STATIC) != 0;
            MethodVisitor visitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
            return new InjectProxyCodeMethodAdapter(ASMContext.ASM_VERSION, visitor, access, name, descriptor, isStatic);
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    private boolean isMatchedMethod(int access, String methodName, String methodDescriptor) {

        // Not apply constructor.
        if ("<clinit>".equals(methodName) || "<init>".equals(methodName)) {
            return false;
        }

        if (applyAllMethodInClass) {
            return true;
        }

        String[] argDescriptors = ClassUtils.getMethodArgDescriptors(methodDescriptor);

        for (MethodDefinition definition : this.targetMethodList) {

            if (methodName.equals(definition.getMethodName())) {

                switch (definition.getType()) {
                    case ALL:
                        return true;
                    case ARG:
                        if (argDescriptors.length == 0 && definition.getArgTypeList().size() == 0) {
                            return true;
                        }

                        return MethodFilter.isMatchArgs(Arrays.asList(argDescriptors), definition);
                    case RETURN_TYPE:
                        return MethodFilter.isMatchReturnType(methodDescriptor, definition);
                    case ARG_AND_RETURN_TYPE:
                        return MethodFilter.isMatchArgsAndReturnType(methodDescriptor, Arrays.asList(argDescriptors), definition);
                }
            }
        }

        return false;
    }

    @Override
    public void visitEnd() {
        if (!visitedStaticBlock) {
            MethodVisitor mv = super.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv = new StaticInitAdviceAdapter(ASMContext.ASM_VERSION, mv, ACC_STATIC, "<clinit>", "()V");
            mv.visitCode();
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        if (!isInterface) {
            cv.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, interceptorFieldName, "Ljava/util/ArrayList;", "Ljava/util/ArrayList<L" + AroundInterceptor.INTERNAL_NAME + ";>;", null).visitEnd();
            InterceptorByteCodeHelper.generateBeforeDelegateMethod(classInfo, cv, interceptorFieldName, delegateBefore);
            InterceptorByteCodeHelper.generateAfterDelegateMethod(classInfo, cv, interceptorFieldName, delegateAfter);
            InterceptorByteCodeHelper.generateThrownDelegateMethod(classInfo, cv, interceptorFieldName, delegateThrown);
        }

        super.visitEnd();
    }

    private class StaticInitAdviceAdapter extends AdviceAdapter {

        protected StaticInitAdviceAdapter(int i, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(i, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodExit(int opcode) {
            mv.visitTypeInsn(NEW, "java/util/ArrayList");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
            mv.visitFieldInsn(PUTSTATIC, classInfo.getInternalName(), interceptorFieldName, "Ljava/util/ArrayList;");

            for (InterceptorDefinition interceptor : interceptorList) {
                LOGGER.debug("\"" + classInfo.getName() + "\" Init interceptor : " + interceptor.getInstrumentClass().getInternalName());

                mv.visitFieldInsn(GETSTATIC, classInfo.getInternalName(), interceptorFieldName, "Ljava/util/ArrayList;");
                mv.visitIntInsn(BIPUSH, interceptor.getInterceptorId());
                mv.visitMethodInsn(INVOKESTATIC, InterceptorRegistryDelegate.INTERNAL_NAME, "findInterceptor", "(I)" + AroundInterceptor.DESCRIPTOR, false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false);
                mv.visitInsn(POP);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 3, maxLocals);
        }
    }

    private class InjectProxyCodeMethodAdapter extends AdviceAdapter {
        private final InstrumentMethod instrumentMethod;
        private final int argCount;
        private final String methodName;
        private final boolean isStatic;

        protected InjectProxyCodeMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, boolean isStatic) {
            super(api, methodVisitor, access, name, descriptor);
            this.instrumentMethod = new InstrumentMethod(access, name, descriptor, null, null);
            this.methodName = name;
            this.argCount = instrumentMethod.getArgCount();
            this.isStatic = isStatic;
        }

        @Override
        protected void onMethodEnter() {

            if (isStatic) {
                mv.visitInsn(ACONST_NULL);
            } else {
                mv.visitVarInsn(ALOAD, 0);
            }

            // Load current method
            loadCurrentMethodObject();

            // Store method arguments to array.
            generateArgumentsArray();

            // Call interceptor before method
            mv.visitMethodInsn(INVOKESTATIC, classInfo.getInternalName(), delegateBefore, BEFORE_METHOD_DESCRIPTOR, false);

            // Replace method arguments to before method return values.
            // This bytecode does not affect the code of the target method.
            for (int i=0; i<argCount; i++) {
                String argTypeDesc = instrumentMethod.getArgTypeDescriptors()[i];

                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitInsn(AALOAD);
                ByteCodeHelper.checkCast(mv, argTypeDesc);
                if (ClassUtils.isPrimitiveType(argTypeDesc)) {
                    ByteCodeHelper.unBoxingPrimitiveType(mv, argTypeDesc);
                }
                ByteCodeHelper.saveStackToLocalVariable(mv, i+1, argTypeDesc);
            }
            mv.visitInsn(POP);
        }

        @Override
        protected void onMethodExit(int opcode) {
            // Run only return.
            // Stack snapshot : [returnedValue]
            if (opcode != ATHROW) {

                // Load "this" to stack for call "after" method. (if method is static, load null instead of "this")
                // Stack snapshot : [this, returnedValue]
                if (isStatic) {
                    mv.visitInsn(ACONST_NULL);
                } else {
                    mv.visitVarInsn(ALOAD, 0);
                }

                if (opcode == RETURN) {
                    // Stack snapshot : [null, this]
                    mv.visitInsn(ACONST_NULL);
                } else {
                    // Swap between "this" and "returnedValue"
                    // Stack snapshot : [returnedValue, this]
                    mv.visitInsn(SWAP);
                }

                // Load current method
                // Stack snapshot : [method, returnedValue, this]
                loadCurrentMethodObject();

                // Stack snapshot : [returnedValue, method, this]
                mv.visitInsn(SWAP);

                // Define array for store method arguments.
                // Stack snapshot : [args, returnedValue, method, this]
                generateArgumentsArray();

                // Call interceptor before method
                // Stack snapshot : [returnedValue] -> The final value returned.
                mv.visitMethodInsn(INVOKESTATIC, classInfo.getInternalName(), delegateAfter, AFTER_METHOD_DESCRIPTOR, false);

                // If return type is void, remove it.
                if (opcode == RETURN) {
                    mv.visitInsn(POP);
                }
            } else {
                // Method threw an exception

                // Copy throwable for call thrown method.
                // Stack snapshot : [throwable, throwable]
                mv.visitInsn(DUP);

                // Load "this" to stack for call "after" method. (if method is static, load null instead of "this")
                // Stack snapshot : [this, throwable, throwable]
                if (isStatic) {
                    mv.visitInsn(ACONST_NULL);
                } else {
                    mv.visitVarInsn(ALOAD, 0);
                }

                // Swap top two value between "this" and "throwable"
                // Stack snapshot : [throwable, this, throwable]
                mv.visitInsn(SWAP);

                // Load current method
                // Stack snapshot : [method, throwable, this, throwable]
                loadCurrentMethodObject();

                // Stack snapshot : [throwable, method, this, throwable]
                mv.visitInsn(SWAP);

                // Define array for store method arguments.
                // Stack snapshot : [args, throwable, method, this, throwable] => top three values are used to "thrown" method.
                generateArgumentsArray();

                // Stack snapshot : [throwable] -> The final value throwable. this value is used to throw.
                mv.visitMethodInsn(INVOKESTATIC, classInfo.getInternalName(), delegateThrown, THROWN_METHOD_DESCRIPTOR, false);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 7, maxLocals);
        }

        private void loadCurrentMethodObject() {
            mv.visitLdcInsn(Type.getType(classInfo.getDescriptor()));
            mv.visitLdcInsn(methodName);
            mv.visitIntInsn(BIPUSH, argCount);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");

            for (int i=0; i<argCount; i++) {
                String argDesc = instrumentMethod.getArgTypeDescriptors()[i];

                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);

                if (ClassUtils.isPrimitiveType(argDesc)) {
                    mv.visitFieldInsn(GETSTATIC, ClassUtils.getBoxingInternalName(argDesc), "TYPE", "Ljava/lang/Class;");
                } else {
                    mv.visitLdcInsn(Type.getType(argDesc));
                }

                mv.visitInsn(AASTORE);
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        }

        private void generateArgumentsArray() {
            mv.visitIntInsn(BIPUSH, argCount);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

            // Store method arguments to array.
            for (int i=0; i<argCount; i++) {
                String argDesc = instrumentMethod.getArgTypeDescriptors()[i];

                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                ByteCodeHelper.loadLocalVariableToStack(mv, i+1, argDesc);
                if (ClassUtils.isPrimitiveType(argDesc)) {
                    ByteCodeHelper.boxingPrimitiveType(mv, argDesc);
                }
                mv.visitInsn(AASTORE);
            }
        }
    }
}
