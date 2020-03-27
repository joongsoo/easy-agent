package software.fitz.easyagent.core.interceptor.handler;

/**
 * If thrown exception in interceptor, call this publish method.
 */
public class ExceptionPublisher {
    public static final String INTERNAL_NAME = "software/fitz/easyagent/core/interceptor/handler/ExceptionPublisher";
    public static final String PUBLISH_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/Throwable;[Ljava/lang/Object;)V";
    public static final String PUBLISH_METHOD_NAME = "publish";

    public static void publish(Object occurredObject, Throwable t, Object[] methodArgs) {
        // TODO : Publish to user defined ExceptionHandler
    }
}
