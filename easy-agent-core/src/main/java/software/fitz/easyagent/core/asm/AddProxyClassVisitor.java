package software.fitz.easyagent.core.asm;

import software.fitz.easyagent.core.asm.helper.InterceptorByteCodeHelper;
import software.fitz.easyagent.core.asm.helper.ByteCodeHelper;
import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.core.interceptor.InterceptorRegistry;
import software.fitz.easyagent.core.model.InstrumentMethod;
import software.fitz.easyagent.api.util.ClassUtils;
import software.fitz.easyagent.core.model.InstrumentClass;
import software.fitz.easyagent.core.model.InterceptorDefinition;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

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
    private String methodName;
    private List<String> methodArgTypes;
    private String methodReturnType;
    private List<InterceptorDefinition> interceptorList;
    private boolean visitedStaticBlock = false;

    public AddProxyClassVisitor(int api,
                                ClassVisitor cv,
                                InstrumentClass classInfo,
                                String methodName,
                                List<String> methodArgTypes,
                                String methodReturnType,
                                List<InterceptorDefinition> interceptorList) {
        super(api, cv);
        this.cv = cv;
        this.classInfo = classInfo;
        this.methodName = methodName;
        this.methodArgTypes = methodArgTypes;
        this.methodReturnType = methodReturnType;
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

        if (!isInterface) {
            cv.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, interceptorFieldName, "Ljava/util/ArrayList;", "Ljava/util/ArrayList<L" + AroundInterceptor.INTERNAL_NAME + ";>;", null).visitEnd();
            InterceptorByteCodeHelper.generateBeforeDelegateMethod(classInfo, cv, interceptorFieldName, delegateBefore);
            InterceptorByteCodeHelper.generateAfterDelegateMethod(classInfo, cv, interceptorFieldName, delegateAfter);
            InterceptorByteCodeHelper.generateThrownDelegateMethod(classInfo, cv, interceptorFieldName, delegateThrown);
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        // static field init
        if (!isInterface && "<clinit>".equals(name) &&  !visitedStaticBlock) {
            System.err.println("[EASY_AGENT][" + classInfo.getName() + "] Generate static field");

            visitedStaticBlock = true;
            MethodVisitor visitor = cv.visitMethod(access, name, descriptor, signature, exceptions);

            return new StaticInitAdviceAdapter(ASMContext.ASM_VERSION, visitor, access, name, descriptor);
        }

        // inject interceptor
        if (!isInterface && isMatchedMethod(name, descriptor)) {
            MethodVisitor visitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
            return new InjectProxyCodeMethodAdapter(ASMContext.ASM_VERSION, visitor, access, name, descriptor);
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    private boolean isMatchedMethod(String methodName, String methodDescriptor) {
        boolean matched = this.methodName.equals(methodName);

        /*
        TODO : [#1] Support method signature
        if (this.methodArgTypes != null) {
            String[] methodParams = ClassUtils.getMethodArgDescriptors(methodDescriptor);

            matched &= methodParams.length == this.methodArgTypes.size();

            for(String paramType : methodParams) {
                if (ClassUtils.isPrimitiveType(paramType)) {

                } else {

                }
            }
        }
         */

        return matched;
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
                System.err.println("[EASY_AGENT][" + classInfo.getName() + "] Init interceptor : " + interceptor.getInstrumentClass().getInternalName());

                mv.visitFieldInsn(GETSTATIC, classInfo.getInternalName(), interceptorFieldName, "Ljava/util/ArrayList;");
                mv.visitIntInsn(BIPUSH, interceptor.getInterceptorId());
                mv.visitMethodInsn(INVOKESTATIC, InterceptorRegistry.INTERNAL_NAME, "findInterceptor", "(I)L" + AroundInterceptor.INTERNAL_NAME + ";", false);
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

        protected InjectProxyCodeMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
            instrumentMethod = new InstrumentMethod(access, name, descriptor, null, null);
            argCount = instrumentMethod.getArgCount();
        }

        @Override
        protected void onMethodEnter() {
            // Define array for store method arguments.
            mv.visitVarInsn(ALOAD, 0);
            mv.visitIntInsn(BIPUSH, argCount);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

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

                // Load "this" to stack for call "after" method.
                // Stack snapshot : [this, returnedValue]
                mv.visitVarInsn(ALOAD, 0);

                if (opcode == RETURN) {
                    // Stack snapshot : [null, this]
                    mv.visitInsn(ACONST_NULL);
                } else {
                    // Swap between "this" and "returnedValue"
                    // Stack snapshot : [returnedValue, this]
                    mv.visitInsn(SWAP);
                }

                // Define array for store method arguments.
                // Stack snapshot : [args, returnedValue, this]
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

                // Load "this" to stack for call "after" method.
                // Stack snapshot : [this, throwable, throwable]
                mv.visitVarInsn(ALOAD, 0);

                // Swap top two value between "this" and "throwable"
                // Stack snapshot : [throwable, this, throwable]
                mv.visitInsn(SWAP);

                // Define array for store method arguments.
                // Stack snapshot : [args, throwable, this, throwable] => top three values are used to "thrown" method.
                generateArgumentsArray();

                // Stack snapshot : [throwable] -> The final value throwable. this value is used to throw.
                mv.visitMethodInsn(INVOKESTATIC, classInfo.getInternalName(), delegateThrown, THROWN_METHOD_DESCRIPTOR, false);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 5, maxLocals);
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
