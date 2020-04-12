package software.fitz.easyagent.api.interceptor;

import java.lang.reflect.Method;

public interface AroundInterceptor {
    String INTERNAL_NAME = "software/fitz/easyagent/api/interceptor/AroundInterceptor";
    String BEFORE_METHOD_NAME = "before";
    String BEFORE_METHOD_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)[Ljava/lang/Object;";
    String AFTER_METHOD_NAME = "after";
    String AFTER_METHOD_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
    String THROWN_METHOD_NAME = "thrown";
    String THROWN_METHOD_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/Throwable;[Ljava/lang/Object;)V";


    /**
     * This method executed before target method execute.
     * It can replace the method arguments.
     *
     * @param targetObject Instance of target class.
     * @param targetMethod Target method object.
     * @param args Target method arguments.
     * @return Replaced method arguments.
     */
    default Object[] before(Object targetObject, Method targetMethod, Object[] args) {
        return args;
    }

    /**
     * This method executed after target method execute.
     * It can replace the value returned by the target method.
     *
     * @param targetObject Instance of target class.
     * @param targetMethod Target method object.
     * @param returnedValue Value returned from target method.
     * @param args Target method arguments. If the before method changes the value, it is affected.
     * @return Replaced return value.
     */
    default Object after(Object targetObject, Method targetMethod, Object returnedValue, Object[] args) {
        return returnedValue;
    }

    /**
     * This method executed on thrown exception in target method.
     * If this method is executed, after is not executed.
     *
     * @param targetObject Instance of target class.
     * @param targetMethod Target method object.
     * @param t Thrown by target method
     * @param args Target method arguments. If the before method changes the value, it is affected.
     */
    default void thrown(Object targetObject, Method targetMethod, Throwable t, Object[] args) {
    }
}
