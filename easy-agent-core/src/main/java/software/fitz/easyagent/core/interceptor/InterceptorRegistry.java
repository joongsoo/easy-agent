package software.fitz.easyagent.core.interceptor;

import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.core.model.InterceptorDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InterceptorRegistry {

    private static final AtomicInteger ID = new AtomicInteger(0);
    private final Map<Integer, InterceptorDefinition> interceptorMap;
    private final Map<Class<? extends AroundInterceptor>, Class<? extends AroundInterceptor>> interceptorClassMap;

    public InterceptorRegistry() {
        interceptorMap = new HashMap<>();
        interceptorClassMap = new HashMap<>();
    }

    public int getNextId() {
        return ID.getAndIncrement();
    }

    public int register(InterceptorDefinition interceptor) {
        int id = ID.getAndIncrement();
        return register(id, interceptor);
    }

    public int register(int id, InterceptorDefinition interceptor) {
        if (interceptorMap.containsKey(id)) {
            throw new IllegalArgumentException("Key already exists : " + id);
        }

        interceptorMap.put(id, interceptor);
        interceptorClassMap.put(interceptor.getReloadedInterceptor().getClass(),
                interceptor.getOriginalInterceptor().getClass());

        return id;
    }

    public AroundInterceptor findInterceptor(int id) {
        InterceptorDefinition interceptorDefinition = interceptorMap.get(id);

        if (interceptorDefinition == null) {
            throw new IllegalArgumentException("Key " + id + " is not exists.");
        }

        return interceptorDefinition.getReloadedInterceptor();
    }

    public Class<? extends AroundInterceptor> findOriginalClass(Class<? extends AroundInterceptor> reloadedClass) {
        Class<? extends AroundInterceptor> originalClass = interceptorClassMap.get(reloadedClass);

        if (originalClass == null) {
            throw new IllegalArgumentException("Original class not found.");
        }

        return originalClass;
    }
}
