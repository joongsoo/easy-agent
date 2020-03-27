package software.fitz.easyagent.core.interceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InterceptorRegistry {
    public static final String INTERNAL_NAME = "software/fitz/easyagent/core/interceptor/InterceptorRegistry";

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private static final ConcurrentMap<AroundInterceptor, Integer> INTERCEPTOR_ID_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, AroundInterceptor> ID_INTERCEPTOR_MAP = new ConcurrentHashMap<>();

    private InterceptorRegistry() {
    }

    public static int register(AroundInterceptor interceptor) {

        return INTERCEPTOR_ID_MAP.computeIfAbsent(interceptor, k -> {
            int id = ID_GENERATOR.getAndIncrement();
            ID_INTERCEPTOR_MAP.put(id, k);
            return id;
        });
    }

    public static AroundInterceptor findInterceptor(int id) {
        return ID_INTERCEPTOR_MAP.get(id);
    }
}
