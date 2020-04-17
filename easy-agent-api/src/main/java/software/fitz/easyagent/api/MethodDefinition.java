package software.fitz.easyagent.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodDefinition {

    private String methodName;
    private Type type;
    private List<Class<?>> argTypeList;
    private Class<?> returnType;

    private MethodDefinition(String methodName, Type type, List<Class<?>> argTypeList, Class<?> returnType) {
        this.methodName = methodName;
        this.type = type;
        this.argTypeList = argTypeList;
        this.returnType = returnType;
    }

    public static MethodDefinition all(String methodName) {
        if (methodName == null) {
            throw new IllegalArgumentException("methodName must not be null");
        }

        return new MethodDefinition(methodName, Type.ALL, null, null);
    }

    public static MethodDefinition matchArgs(String methodName, Class<?>... argType) {
        if (methodName == null) {
            throw new IllegalArgumentException("methodName must not be null");
        }

        List<Class<?>> args = argType == null ? Collections.emptyList() : Arrays.asList(argType);

        return new MethodDefinition(methodName, Type.ARG, args, null);
    }

    public static MethodDefinition matchReturnType(String methodName, Class<?> returnType) {
        if (methodName == null) {
            throw new IllegalArgumentException("methodName must not be null");
        }

        if (returnType == null) {
            throw new IllegalArgumentException("returnType must not be null");
        }

        return new MethodDefinition(methodName, Type.RETURN_TYPE, null, returnType);
    }

    public static MethodDefinition matchArgsAndReturnType(String methodName, Class<?> returnType, Class<?>... argType) {
        if (methodName == null) {
            throw new IllegalArgumentException("methodName must not be null");
        }

        if (returnType == null) {
            throw new IllegalArgumentException("returnType must not be null");
        }

        List<Class<?>> args = argType == null ? Collections.emptyList() : Arrays.asList(argType);

        return new MethodDefinition(methodName, Type.ARG_AND_RETURN_TYPE, args, returnType);
    }

    public String getMethodName() {
        return methodName;
    }

    public Type getType() {
        return type;
    }

    public List<Class<?>> getArgTypeList() {
        return argTypeList;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public enum Type {
        ALL, ARG, RETURN_TYPE, ARG_AND_RETURN_TYPE
    }
}
