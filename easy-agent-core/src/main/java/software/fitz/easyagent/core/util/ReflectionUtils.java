package software.fitz.easyagent.core.util;

import software.fitz.easyagent.api.logging.AgentLogger;
import software.fitz.easyagent.api.logging.AgentLoggerFactory;
import software.fitz.easyagent.core.classloader.AgentClassLoader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

public class ReflectionUtils {

    private static final AgentLogger LOGGER = AgentLoggerFactory.getLogger();

    public static void copyAllField(Object src, Object target) {
        copyAllField(src, target, true);
    }

    public static void copyAllField(Object src, Object target, boolean ignoreUnknownField) {
        Field[] fields = src.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Field targetField;

            try {
                targetField = target.getClass().getDeclaredField(field.getName());
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
                    field.setAccessible(true);
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
                    field.setAccessible(true);
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

    public static <T> T newInstance(Class<? extends T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Class must have public default constructor.", e);
        }
    }

    public static Class<?> reloadClass(String classInternalName, ClassLoader classLoader) {
        return reloadClass(classInternalName, classLoader, null);
    }

    public static Class<?> reloadClass(String classInternalName, ClassLoader classLoader, ProtectionDomain protectionDomain) {
        try {
            return AgentClassLoader.of(classLoader).define(classInternalName, protectionDomain);
        } catch (IOException e) {
            throw new RuntimeException("Class not found.", e);
        }
    }
}
