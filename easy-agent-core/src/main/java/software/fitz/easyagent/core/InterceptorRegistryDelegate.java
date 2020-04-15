package software.fitz.easyagent.core;

import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.core.interceptor.InterceptorRegistry;

/**
 * Delegate class for encapsulation
 */
public class InterceptorRegistryDelegate {

    public static final String INTERNAL_NAME = "software/fitz/easyagent/core/InterceptorRegistryDelegate";

    static final InterceptorRegistry INTERCEPTOR_REGISTRY = new InterceptorRegistry();

    public static AroundInterceptor findInterceptor(int id) {
        return INTERCEPTOR_REGISTRY.findInterceptor(id);
    }

    public static Class<? extends AroundInterceptor> findOriginalClass(Class<? extends AroundInterceptor> reloadedClass) {
        return INTERCEPTOR_REGISTRY.findOriginalClass(reloadedClass);
    }
}
