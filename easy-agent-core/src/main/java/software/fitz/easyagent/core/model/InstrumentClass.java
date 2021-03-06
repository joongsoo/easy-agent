package software.fitz.easyagent.core.model;

import software.fitz.easyagent.api.util.ClassUtils;

public class InstrumentClass {
    private String name;
    private String internalName;
    private String descriptor;

    public InstrumentClass(String name, String internalName) {
        this.name = name;
        this.internalName = internalName;
        this.descriptor = "L" + internalName + ";";
    }

    public InstrumentClass(Class<?> clazz) {
        this.name = clazz.getTypeName();
        this.internalName = ClassUtils.toInternalName(name);
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public static InstrumentClass fromInternalName(String internalName) {
        return new InstrumentClass(internalName.replace("/", "."), internalName);
    }

    public static InstrumentClass fromClassName(String className) {
        return new InstrumentClass(className, className.replace(".", "/"));
    }
}
