package software.fitz.easyagent.core;

import software.fitz.easyagent.api.interceptor.exception.ExceptionHandler;
import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.core.interceptor.exception.ExceptionPublisher;

import java.lang.reflect.Method;

/**
 * Delegate class for encapsulation
 */
public class ExceptionPublisherDelegate {

    public static final String INTERNAL_NAME = "software/fitz/easyagent/core/ExceptionPublisherDelegate";
    public static final String PUBLISH_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/reflect/Method;" +
            AroundInterceptor.DESCRIPTOR + "Ljava/lang/Throwable;[Ljava/lang/Object;)V";
    public static final String PUBLISH_METHOD_NAME = "publish";

    private static final ExceptionPublisher EXCEPTION_PUBLISHER = new ExceptionPublisher();

    public static void publish(Object targetObject, Method targetMethod, AroundInterceptor interceptor, Throwable t, Object[] methodArgs) {
        EXCEPTION_PUBLISHER.publish(targetObject, targetMethod, interceptor, t, methodArgs);
    }

    static void register(ExceptionHandler handler) {
        EXCEPTION_PUBLISHER.register(handler);
    }
}
