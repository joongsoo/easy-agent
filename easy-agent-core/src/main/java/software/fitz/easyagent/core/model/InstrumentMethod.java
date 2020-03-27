package software.fitz.easyagent.core.model;

import software.fitz.easyagent.core.util.ClassUtils;

public class InstrumentMethod {
    private int access;
    private String name;
    private String descriptor;
    private String signature;
    private String[] exceptions;
    private boolean returnVoid;
    private int argCount;
    private String[] argTypeDescriptors;

    public InstrumentMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
        this.returnVoid = ClassUtils.isReturnVoid(descriptor);
        this.argCount = ClassUtils.getArgCount(descriptor);
        this.argTypeDescriptors = ClassUtils.getMethodArgDescriptors(descriptor);
    }

    public int getAccess() {
        return access;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public boolean isReturnVoid() {
        return returnVoid;
    }

    public int getArgCount() {
        return argCount;
    }

    public String[] getArgTypeDescriptors() {
        return argTypeDescriptors;
    }
}
