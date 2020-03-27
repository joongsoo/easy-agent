package software.fitz.easyagent.core.model;

import software.fitz.easyagent.core.util.ClassUtils;

public class InstrumentClass {
    private String name;
    private String internalName;

    public InstrumentClass(String name, String internalName) {
        this.name = name;
        this.internalName = internalName;
    }

    public InstrumentClass(Class<?> clazz) {
        this.name = clazz.getName();
        this.internalName = ClassUtils.toInternalName(name);
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public static InstrumentClass fromInternalName(String internalName) {
        return new InstrumentClass(internalName.replace("/", "."), internalName);
    }

    public static InstrumentClass fromClassName(String className) {
        return new InstrumentClass(className, className.replace(".", "/"));
    }
}
