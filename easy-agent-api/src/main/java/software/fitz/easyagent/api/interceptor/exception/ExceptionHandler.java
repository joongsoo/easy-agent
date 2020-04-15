package software.fitz.easyagent.api.interceptor.exception;

import software.fitz.easyagent.api.interceptor.AroundInterceptor;

import java.lang.reflect.Method;

public interface ExceptionHandler<I extends AroundInterceptor> {

    void handle(Object targetObject, Method targetMethod, I interceptor, Throwable t, Object[] methodArgs);
}
