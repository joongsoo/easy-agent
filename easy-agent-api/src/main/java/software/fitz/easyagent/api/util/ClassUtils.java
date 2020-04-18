package software.fitz.easyagent.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassUtils {

    public static String toInternalName(String className) {
        return className.replace(".", "/");
    }

    public static String toInternalName(Class<?> clazz) {
        return toInternalName(clazz.getTypeName());
    }

    public static String toClassName(String internalName) {
        return internalName.replace("/", ".");
    }

    public static int getArgCount(String descriptor) {
        return getMethodArgDescriptors(descriptor).length;
    }

    public static boolean isReturnVoid(String methodDescriptor) {
        String returnType = getReturnTypeDescriptor(methodDescriptor);
        return "V".equals(returnType);
    }

    public static String getReturnTypeDescriptor(String methodDescriptor) {
        return methodDescriptor.split("\\)")[1];
    }

    public static String[] getMethodArgDescriptors(String methodDescriptor) {
        if (methodDescriptor.startsWith("()")) {
            return new String[]{};
        }

        String[] descriptors = methodDescriptor.split("\\)")[0].split("\\(")[1].split(";");

        String[] rawDescriptors = Arrays.stream(descriptors)
                .map(x -> {
                    if (x.startsWith("L") || x.startsWith("[L")) {
                        return x + ";";
                    } else {
                        return x;
                    }
                })
                .toArray(String[]::new);

        List<String> dList = new ArrayList<>();
        for (String d : rawDescriptors) {
            if (d.startsWith("L")) { // Reference type
                dList.add(d);
            } else if (d.startsWith("[") && d.charAt(1) == 'L') { // Array of reference type
                dList.add(d);
            } else {
                for (int i=0; i<d.length(); i++) {
                    if (d.charAt(i) == '[' && d.charAt(i+1) != 'L') { // Array of primitive type
                        dList.add(String.valueOf(new char[] { d.charAt(i), d.charAt(i+1) }));
                        ++i;
                    } else if (d.charAt(i) == 'L' || (d.charAt(i) == '[' && d.charAt(i+1) == 'L')) {
                        dList.add(d.substring(i));
                        break;
                    } else {
                        dList.add(String.valueOf(d.charAt(i)));
                    }
                }
            }
        }

        return dList.stream().toArray(String[]::new);
    }

    public static boolean isPrimitiveType(String descriptor) {
        return !descriptor.startsWith("L") && !descriptor.startsWith("[");
    }

    public static String getBoxingInternalName(String descriptor) {

        switch (descriptor) {
            // int
            case "I" :
                return "java/lang/Integer";
            // boolean
            case "Z" :
                return "java/lang/Boolean";
            // char
            case "C" :
                return "java/lang/Character";
            // byte
            case "B" :
                return "java/lang/Byte";
            // short
            case "S" :
                return "java/lang/Short";
            // float
            case "F" :
                return "java/lang/Float";
            // long
            case "J" :
                return "java/lang/Long";
            // double
            case "D" :
                return "java/lang/Double";
        }

        throw new IllegalArgumentException("not primitive descriptor : " + descriptor);
    }

    public static String unboxing(String internalName) {

        switch (internalName) {
            case "java/lang/Integer":
                return "int";
            case "java/lang/Boolean":
                return "boolean";
            case "java/lang/Character" :
                return "char";
            case "java/lang/Byte" :
                return "byte";
            case "java/lang/Short" :
                return "short";
            case "java/lang/Float" :
                return "float";
            case "java/lang/Long" :
                return "long";
            case "java/lang/Double" :
                return "double";
        }

        throw new IllegalArgumentException("not primitive internal name : " + internalName);
    }

    public static String descriptorToInternalName(String descriptor) {

        if (descriptor.startsWith("L")) { // reference type
            return descriptor.substring(1, descriptor.length() -1);
        } else if (descriptor.startsWith("[L")) { // reference type array
            return descriptor.substring(2, descriptor.length() -1) + "[]";
        } else if (isPrimitiveType(descriptor)) { // primitive type
            return unboxing(getBoxingInternalName(descriptor));
        } else { // primitive type array
            return unboxing(getBoxingInternalName(String.valueOf(descriptor.charAt(1)))) + "[]";
        }
    }
}
