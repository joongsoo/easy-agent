package software.fitz.easyagent.core.asm.helper;

import software.fitz.easyagent.api.util.ClassUtils;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;

public class ByteCodeHelper {

    public static void boxingPrimitiveType(MethodVisitor mv, String descriptor) {
        switch (descriptor) {
            // int
            case "I" :
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                break;
            // boolean
            case "Z" :
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                break;
            // char
            case "C" :
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                break;
            // byte
            case "B" :
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                break;
            // short
            case "S" :
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                break;
            // float
            case "F" :
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                break;
            // long
            case "J" :
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                break;
            // double
            case "D" :
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                break;
        }
    }

    public static void unBoxingPrimitiveType(MethodVisitor mv, String descriptor) {
        switch (descriptor) {
            // int
            case "I" :
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
                break;
            // boolean
            case "Z" :
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
                break;
            // char
            case "C" :
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
                break;
            // byte
            case "B" :
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
                break;
            // short
            case "S" :
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
                break;
            // float
            case "F" :
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
                break;
            // long
            case "J" :
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
                break;
            // double
            case "D" :
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
                break;
        }
    }

    public static void loadLocalVariableToStack(MethodVisitor mv, int slotIdx, String descriptor) {
        switch (descriptor) {
            case "I" :
            case "Z" :
            case "C" :
            case "B" :
            case "S" :
                mv.visitVarInsn(ILOAD, slotIdx);
                return;
            case "F" :
                mv.visitVarInsn(FLOAD, slotIdx);
                return;
            case "J" :
                mv.visitVarInsn(LLOAD, slotIdx);
                return;
            // double
            case "D" :
                mv.visitVarInsn(DLOAD, slotIdx);
                return;
            default:
                mv.visitVarInsn(ALOAD, slotIdx);
        }
    }

    public static void saveStackToLocalVariable(MethodVisitor mv, int slotIdx, String descriptor) {
        switch (descriptor) {
            case "I" :
            case "Z" :
            case "C" :
            case "B" :
            case "S" :
                mv.visitVarInsn(ISTORE, slotIdx);
                return;
            case "F" :
                mv.visitVarInsn(FSTORE, slotIdx);
                return;
            case "J" :
                mv.visitVarInsn(LSTORE, slotIdx);
                return;
            // double
            case "D" :
                mv.visitVarInsn(DSTORE, slotIdx);
                return;
            default:
                mv.visitVarInsn(ASTORE, slotIdx);
        }
    }

    public static void checkCast(MethodVisitor mv, String descriptor) {
        if (descriptor.startsWith("[")) {
            mv.visitTypeInsn(CHECKCAST, descriptor);
            mv.visitTypeInsn(CHECKCAST, descriptor);
        } else if (ClassUtils.isPrimitiveType(descriptor)) {
            mv.visitTypeInsn(CHECKCAST, ClassUtils.getBoxingInternalName(descriptor));
        } else {
            mv.visitTypeInsn(CHECKCAST, ClassUtils.descriptorToInternalName(descriptor));
        }
    }

    public static void systemOutPrintln(MethodVisitor mv, String msg){
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
