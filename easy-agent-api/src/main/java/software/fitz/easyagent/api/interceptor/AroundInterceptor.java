package software.fitz.easyagent.api.interceptor;

public interface AroundInterceptor {
    String INTERNAL_NAME = "software/fitz/easyagent/api/interceptor/AroundInterceptor";
    String BEFORE_METHOD_NAME = "before";
    String BEFORE_METHOD_DESCRIPTOR = "(Ljava/lang/Object;[Ljava/lang/Object;)[Ljava/lang/Object;";
    String AFTER_METHOD_NAME = "after";
    String AFTER_METHOD_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
    String THROWN_METHOD_NAME = "thrown";
    String THROWN_METHOD_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/Throwable;[Ljava/lang/Object;)V";


    /**
     * This method executed before target method execute.
     * It can replace the method arguments.
     *
     * @param target Instance of target class.
     * @param args Target method arguments.
     * @return Replaced method arguments.
     */
    default Object[] before(Object target, Object[] args) {
        return args;
    }

    /**
     * This method executed after target method execute.
     * It can replace the value returned by the target method.
     *
     * @param target Instance of target class.
     * @param returnedValue Value returned from target method.
     * @param args Target method arguments. If the before method changes the value, it is affected.
     * @return Replaced return value.
     */
    default Object after(Object target, Object returnedValue, Object[] args) {
        return returnedValue;
    }

    /**
     * This method executed on thrown exception in target method.
     * If this method is executed, after is not executed.
     *
     * @param target Instance of target class.
     * @param t Thrown by target method
     * @param args Target method arguments. If the before method changes the value, it is affected.
     */
    default void thrown(Object target, Throwable t, Object[] args) {
    }
}
