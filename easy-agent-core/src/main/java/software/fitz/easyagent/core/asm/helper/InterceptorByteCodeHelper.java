package software.fitz.easyagent.core.asm.helper;

import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.core.interceptor.handler.ExceptionPublisher;
import software.fitz.easyagent.core.model.InstrumentClass;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.AFTER_METHOD_DESCRIPTOR;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.AFTER_METHOD_NAME;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.BEFORE_METHOD_DESCRIPTOR;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.BEFORE_METHOD_NAME;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.THROWN_METHOD_DESCRIPTOR;
import static software.fitz.easyagent.api.interceptor.AroundInterceptor.THROWN_METHOD_NAME;

public class InterceptorByteCodeHelper {


    public static void generateBeforeDelegateMethod(InstrumentClass classInfo, ClassVisitor cv, String interceptorFieldName, String delegateMethodName) {

        System.err.println("[EASY_AGENT][" + classInfo.getName() + "] Generate method : " + delegateMethodName);

        MethodVisitor methodVisitor = cv.visitMethod(ACC_PRIVATE | ACC_FINAL | ACC_STATIC | ACC_VARARGS, delegateMethodName, BEFORE_METHOD_DESCRIPTOR, null, null);
        methodVisitor.visitCode();

        TryCatchHelper tryCatchHelper = new TryCatchHelper();
        methodVisitor.visitTryCatchBlock(tryCatchHelper.getStart(), tryCatchHelper.getEnd(), tryCatchHelper.getHandler(), "java/lang/Throwable");

        Label label3 = new Label();
        methodVisitor.visitLabel(label3);
        methodVisitor.visitLineNumber(47, label3);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitVarInsn(ASTORE, 2);

        Label label4 = new Label();
        methodVisitor.visitLabel(label4);
        methodVisitor.visitLineNumber(48, label4);
        methodVisitor.visitFieldInsn(GETSTATIC, classInfo.getInternalName(), interceptorFieldName, "Ljava/util/ArrayList;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "iterator", "()Ljava/util/Iterator;", false);
        methodVisitor.visitVarInsn(ASTORE, 3);

        Label label5 = new Label();
        methodVisitor.visitLabel(label5);
        methodVisitor.visitFrame(Opcodes.F_APPEND, 2, new Object[]{"[Ljava/lang/Object;", "java/util/Iterator"}, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);

        Label label6 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label6);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        methodVisitor.visitTypeInsn(CHECKCAST, AroundInterceptor.INTERNAL_NAME);
        methodVisitor.visitVarInsn(ASTORE, 4);


        methodVisitor.visitLabel(tryCatchHelper.getStart());
        methodVisitor.visitLineNumber(50, tryCatchHelper.getStart());
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, AroundInterceptor.INTERNAL_NAME, BEFORE_METHOD_NAME, BEFORE_METHOD_DESCRIPTOR, true);
        methodVisitor.visitVarInsn(ASTORE, 2);


        methodVisitor.visitLabel(tryCatchHelper.getEnd());
        methodVisitor.visitLineNumber(53, tryCatchHelper.getEnd());
        Label label7 = new Label();
        methodVisitor.visitJumpInsn(GOTO, label7);

        methodVisitor.visitLabel(tryCatchHelper.getHandler());
        methodVisitor.visitLineNumber(51, tryCatchHelper.getHandler());
        methodVisitor.visitFrame(Opcodes.F_FULL, 5, new Object[]{classInfo.getInternalName(), "[Ljava/lang/Object;", "[Ljava/lang/Object;", "java/util/Iterator", AroundInterceptor.INTERNAL_NAME}, 1, new Object[]{"java/lang/Throwable"});
        methodVisitor.visitVarInsn(ASTORE, 5);
        Label label8 = new Label();
        methodVisitor.visitLabel(label8);
        methodVisitor.visitLineNumber(52, label8);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 5);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(INVOKESTATIC, ExceptionPublisher.INTERNAL_NAME, ExceptionPublisher.PUBLISH_METHOD_NAME, ExceptionPublisher.PUBLISH_DESCRIPTOR, false);

        methodVisitor.visitLabel(label7);
        methodVisitor.visitLineNumber(54, label7);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        methodVisitor.visitJumpInsn(GOTO, label5);

        methodVisitor.visitLabel(label6);
        methodVisitor.visitLineNumber(56, label6);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(ARETURN);

        Label label9 = new Label();
        methodVisitor.visitLabel(label9);
        methodVisitor.visitLocalVariable("t", "Ljava/lang/Throwable;", null, label8, label7, 5);
        methodVisitor.visitLocalVariable("ai", "L"+AroundInterceptor.INTERNAL_NAME+";", null, tryCatchHelper.getStart(), label7, 4);
        methodVisitor.visitLocalVariable("target", "L"+classInfo.getInternalName()+";", null, label3, label9, 0);
        methodVisitor.visitLocalVariable("args", "[Ljava/lang/Object;", null, label3, label9, 1);
        methodVisitor.visitLocalVariable("changed", "[Ljava/lang/Object;", null, label4, label9, 2);

        methodVisitor.visitMaxs(3, 6);
        methodVisitor.visitEnd();
    }

    public static void generateAfterDelegateMethod(InstrumentClass classInfo, ClassVisitor cv, String interceptorFieldName, String delegateMethodName) {

        System.err.println("[EASY_AGENT][" + classInfo.getName() + "] Generate method : " + delegateMethodName);

        MethodVisitor methodVisitor = cv.visitMethod(ACC_PRIVATE | ACC_FINAL | ACC_STATIC | ACC_VARARGS, delegateMethodName, AFTER_METHOD_DESCRIPTOR, null, null);
        methodVisitor.visitCode();

        TryCatchHelper tryCatchHelper = new TryCatchHelper();
        methodVisitor.visitTryCatchBlock(tryCatchHelper.getStart(), tryCatchHelper.getEnd(), tryCatchHelper.getHandler(), "java/lang/Throwable");

        Label label3 = new Label();
        methodVisitor.visitLabel(label3);
        methodVisitor.visitFieldInsn(GETSTATIC, classInfo.getInternalName(), interceptorFieldName, "Ljava/util/ArrayList;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "iterator", "()Ljava/util/Iterator;", false);
        methodVisitor.visitVarInsn(ASTORE, 3);


        Label label4 = new Label();
        methodVisitor.visitLabel(label4);
        methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/util/Iterator"}, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);

        Label label5 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label5);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        methodVisitor.visitTypeInsn(CHECKCAST, AroundInterceptor.INTERNAL_NAME);
        methodVisitor.visitVarInsn(ASTORE, 4);

        // try
        methodVisitor.visitLabel(tryCatchHelper.getStart());
        methodVisitor.visitVarInsn(ALOAD, 4); // interceptor (invoke target)
        methodVisitor.visitVarInsn(ALOAD, 0); // target object
        methodVisitor.visitVarInsn(ALOAD, 1); // target method returned value
        methodVisitor.visitVarInsn(ALOAD, 2); // method arguments
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, AroundInterceptor.INTERNAL_NAME, AFTER_METHOD_NAME, AFTER_METHOD_DESCRIPTOR, true);
        methodVisitor.visitVarInsn(ASTORE, 1);
        methodVisitor.visitLabel(tryCatchHelper.getEnd());

        // end (jump to catch)
        Label label6 = new Label();
        methodVisitor.visitJumpInsn(GOTO, label6);

        // catch
        methodVisitor.visitLabel(tryCatchHelper.getHandler());
        methodVisitor.visitFrame(Opcodes.F_FULL, 4, new Object[]{classInfo.getInternalName(), "[Ljava/lang/Object;", "java/util/Iterator", AroundInterceptor.INTERNAL_NAME}, 1, new Object[]{"java/lang/Throwable"});
        methodVisitor.visitVarInsn(ASTORE, 5);

        // Publish Exception
        Label label7 = new Label();
        methodVisitor.visitLabel(label7);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 5);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(INVOKESTATIC, ExceptionPublisher.INTERNAL_NAME, ExceptionPublisher.PUBLISH_METHOD_NAME, ExceptionPublisher.PUBLISH_DESCRIPTOR, false);

        methodVisitor.visitLabel(label6);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        methodVisitor.visitJumpInsn(GOTO, label4);

        // method return
        methodVisitor.visitLabel(label5);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitInsn(ARETURN);


        Label label8 = new Label();
        methodVisitor.visitLabel(label8);
        methodVisitor.visitLocalVariable("t", "Ljava/lang/Throwable;", null, label7, label6, 5);
        methodVisitor.visitLocalVariable("ai", "L" + AroundInterceptor.INTERNAL_NAME + ";", null, tryCatchHelper.getStart(), label6, 4);
        methodVisitor.visitLocalVariable("target", "L" + classInfo.getInternalName() + ";", null, label3, label8, 0);
        methodVisitor.visitLocalVariable("returnedValue", "Ljava/lang/Object;", null, label3, label8, 1);
        methodVisitor.visitLocalVariable("args", "[Ljava/lang/Object;", null, label3, label8, 2);

        methodVisitor.visitMaxs(4, 6);
        methodVisitor.visitEnd();
    }

    public static void generateThrownDelegateMethod(InstrumentClass classInfo, ClassVisitor cv, String interceptorFieldName, String delegateMethodName) {

        System.err.println("[EASY_AGENT][" + classInfo.getName() + "] Generate method : " + delegateMethodName);

        MethodVisitor methodVisitor = cv.visitMethod(ACC_PRIVATE | ACC_FINAL | ACC_STATIC | ACC_VARARGS, delegateMethodName, THROWN_METHOD_DESCRIPTOR, null, null);
        methodVisitor.visitCode();

        TryCatchHelper tryCatchHelper = new TryCatchHelper();

        methodVisitor.visitTryCatchBlock(tryCatchHelper.getStart(), tryCatchHelper.getEnd(), tryCatchHelper.getHandler(), "java/lang/Throwable");
        Label label3 = new Label();
        methodVisitor.visitLabel(label3);
        methodVisitor.visitFieldInsn(GETSTATIC, classInfo.getInternalName(), interceptorFieldName, "Ljava/util/ArrayList;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "iterator", "()Ljava/util/Iterator;", false);
        methodVisitor.visitVarInsn(ASTORE, 3);

        Label label4 = new Label();
        methodVisitor.visitLabel(label4);
        methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/util/Iterator"}, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);

        Label label5 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label5);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        methodVisitor.visitTypeInsn(CHECKCAST, AroundInterceptor.INTERNAL_NAME);
        methodVisitor.visitVarInsn(ASTORE, 4);

        methodVisitor.visitLabel(tryCatchHelper.getStart());
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, AroundInterceptor.INTERNAL_NAME, THROWN_METHOD_NAME, THROWN_METHOD_DESCRIPTOR, true);

        methodVisitor.visitLabel(tryCatchHelper.getEnd());
        Label label6 = new Label();
        methodVisitor.visitJumpInsn(GOTO, label6);

        methodVisitor.visitLabel(tryCatchHelper.getHandler());
        methodVisitor.visitFrame(Opcodes.F_FULL, 5, new Object[]{"java/lang/Object", "java/lang/Throwable", "[Ljava/lang/Object;", "java/util/Iterator", AroundInterceptor.INTERNAL_NAME}, 1, new Object[]{"java/lang/Throwable"});
        methodVisitor.visitVarInsn(ASTORE, 5);
        Label label7 = new Label();
        methodVisitor.visitLabel(label7);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 5);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(INVOKESTATIC, ExceptionPublisher.INTERNAL_NAME, ExceptionPublisher.PUBLISH_METHOD_NAME, ExceptionPublisher.PUBLISH_DESCRIPTOR, false);

        methodVisitor.visitLabel(label6);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        methodVisitor.visitJumpInsn(GOTO, label4);

        methodVisitor.visitLabel(label5);
        methodVisitor.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        methodVisitor.visitInsn(RETURN);

        Label label8 = new Label();
        methodVisitor.visitLabel(label8);
        methodVisitor.visitLocalVariable("t2", "Ljava/lang/Throwable;", null, label7, label6, 5);
        methodVisitor.visitLocalVariable("ai", "L" + AroundInterceptor.INTERNAL_NAME + ";", null, tryCatchHelper.getStart(), label6, 4);
        methodVisitor.visitLocalVariable("target", "Ljava/lang/Object;", null, label3, label8, 0);
        methodVisitor.visitLocalVariable("t", "Ljava/lang/Throwable;", null, label3, label8, 1);
        methodVisitor.visitLocalVariable("args", "[Ljava/lang/Object;", null, label3, label8, 2);
        methodVisitor.visitMaxs(4, 6);
        methodVisitor.visitEnd();
    }
}
