package software.fitz.easyagent.api.interceptor;

public interface AroundInterceptor {
    String INTERNAL_NAME = "software/fitz/easyagent/api/interceptor/AroundInterceptor";
    String BEFORE_METHOD_NAME = "before";
    String BEFORE_METHOD_DESCRIPTOR = "(Ljava/lang/Object;[Ljava/lang/Object;)[Ljava/lang/Object;";
    String AFTER_METHOD_NAME = "after";
    String AFTER_METHOD_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";

    /**
     *
     * @param target 타겟 메소드를 가진 객체
     * @param args 타겟 메소드의 인자들
     * @return 타겟 메소드에 전달할 인자. 이 메소드에서 타겟 메소드에 전달할 값을 조작할 수 있다.
     */
    default Object[] before(Object target, Object[] args) {
        return args;
    }

    default Object after(Object target, Object returnedValue, Object[] args) {
        return returnedValue;
    }
}
