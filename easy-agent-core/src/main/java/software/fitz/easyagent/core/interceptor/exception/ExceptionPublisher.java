package software.fitz.easyagent.core.interceptor.exception;

import software.fitz.easyagent.api.interceptor.exception.ExceptionHandler;
import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.core.InterceptorRegistryDelegate;
import software.fitz.easyagent.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * If thrown exception in interceptor, call this publish method.
 */
public class ExceptionPublisher {

    private final List<ExceptionHandler> handlerList = new CopyOnWriteArrayList<>();

    public void register(ExceptionHandler handler) {
        handlerList.add(handler);
    }

    public void publish(Object targetObject, Method targetMethod, AroundInterceptor interceptor, Throwable t, Object[] methodArgs) {

        AroundInterceptor originalInstance;

        // The argument "interceptor" is an instance reloaded from another classloader.
        // So pass copied object of the original class to ExceptionHandler.
        try {
            originalInstance = InterceptorRegistryDelegate.findOriginalClass(interceptor.getClass()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Target class must have public default constructor.", e);
        }

        ReflectionUtils.copyAllField(interceptor, originalInstance);

        for (ExceptionHandler handler : handlerList) {

            // Execute only matched interceptor type of handler.
            Class<?> exceptionHandlerGenericType = findExceptionHandlerGenericType(handler.getClass());
            if ((exceptionHandlerGenericType != null && exceptionHandlerGenericType.isAssignableFrom(originalInstance.getClass()))
                    || exceptionHandlerGenericType == null) {
                handler.handle(targetObject, targetMethod, originalInstance, t, methodArgs);
            }
        }
    }

    private static Class<?> findExceptionHandlerGenericType(Class<? extends ExceptionHandler> clazz) {
        Class<? extends ExceptionHandler> c = clazz;

        do {
            for (Type type : c.getGenericInterfaces()) {
                if (type.getTypeName().startsWith(ExceptionHandler.class.getTypeName()) && type instanceof ParameterizedType) {
                    return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                }
            }
        } while((c = getParentIfChildOfExceptionHandler(c)) != null);

        return null;
    }

    private static Class<? extends ExceptionHandler> getParentIfChildOfExceptionHandler(Class<?> clazz) {
        if (clazz.getSuperclass() != null && ExceptionHandler.class.isAssignableFrom(clazz.getSuperclass())) {
            return (Class<? extends ExceptionHandler>) clazz.getSuperclass();
        }
        return null;
    }
}
