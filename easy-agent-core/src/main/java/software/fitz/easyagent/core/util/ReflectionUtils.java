package software.fitz.easyagent.core.util;

import software.fitz.easyagent.api.util.ClassUtils;
import software.fitz.easyagent.core.classloader.AgentClassLoader;
import software.fitz.easyagent.core.logging.AgentLogger;
import software.fitz.easyagent.core.logging.AgentLoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

    private static final AgentLogger LOGGER = AgentLoggerFactory.getDefaultLogger();

    public static <T> T copyInstanceBySameClassLoader(Object original, Class<T> target) {
        return copyInstanceBySameClassLoader(original, target, true);
    }

    public static <T> T copyInstanceBySameClassLoader(Object original, Class<T> target, boolean ignoreUnknownField) {
        Field[] fields = original.getClass().getFields();
        T instance;

        try {
            try {
                instance = (T) AgentClassLoader.of(original.getClass().getClassLoader()).
                        loadClass(ClassUtils.toInternalName(target)).newInstance();
            } catch (ClassNotFoundException cnfe) {
                instance = (T) AgentClassLoader.of(original.getClass().getClassLoader())
                        .define(ClassUtils.toInternalName(target), null).newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Target class must have public default constructor.", e);
        } catch (IOException ioe) {
            throw new RuntimeException("Class not found.", ioe);
        }

        copyAllField(original, instance, ignoreUnknownField);

        return instance;
    }

    public static void copyAllField(Object src, Object target) {
        copyAllField(src, target, true);
    }

    public static void copyAllField(Object src, Object target, boolean ignoreUnknownField) {
        Field[] fields = src.getClass().getFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Field targetField;

            try {
                targetField = target.getClass().getField(field.getName());
            } catch (NoSuchFieldException e) {
                LOGGER.debug("Exception on copyAllField : " + e.getMessage());

                if (!ignoreUnknownField) {
                    throw new RuntimeException(e);
                } else {
                    continue;
                }
            }

            setFieldValue(target, targetField, getFieldValue(src, field));
        }
    }

    private static Object getFieldValue(Object instance, Field field) {
        try {
            if (!field.isAccessible()) {
                try {
                    return field.get(instance);
                } finally {
                    field.setAccessible(false);
                }
            } else {
                return field.get(instance);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setFieldValue(Object instance, Field field, Object value) {
        try {
            if (!field.isAccessible()) {
                try {
                    field.set(instance, value);
                } finally {
                    field.setAccessible(false);
                }
            } else {
                field.set(instance, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
