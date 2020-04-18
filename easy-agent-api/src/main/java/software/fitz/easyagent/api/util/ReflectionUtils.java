package software.fitz.easyagent.api.util;

import software.fitz.easyagent.api.logging.AgentLogger;
import software.fitz.easyagent.api.logging.AgentLoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public static Field getField(Object instance, String fieldName) {
        try {
            return instance.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T inAccesible(Field field, Function<Field, T> f) {
        if (!field.isAccessible()) {
            try {
                field.setAccessible(true);
                return f.apply(field);
            } finally {
                field.setAccessible(false);
            }
        } else {
            return f.apply(field);
        }
    }

    public static void inAccesible(Field field, Consumer<Field> f) {
        if (!field.isAccessible()) {
            try {
                field.setAccessible(true);
                f.accept(field);
            } finally {
                field.setAccessible(false);
            }
        } else {
            f.accept(field);
        }
    }

    public static Object getFieldValue(Object instance, Field field) {

        return inAccesible(field, f -> {
            try {
                return field.get(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean getFieldBooleanValue(Object instance, Field field) {

        return inAccesible(field, f -> {
            try {
                return field.getBoolean(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static int getFieldIntValue(Object instance, Field field) {

        return inAccesible(field, f -> {
            try {
                return field.getInt(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static double getFieldDoubleValue(Object instance, Field field) {

        return inAccesible(field, f -> {
            try {
                return field.getDouble(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setFieldValue(Object instance, Field field, Object value) {
        inAccesible(field, f -> {
            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T> T newInstance(Class<? extends T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Class must have public default constructor.", e);
        }
    }
}
