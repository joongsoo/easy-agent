package software.fitz.easyagent.core.model;

import software.fitz.easyagent.api.interceptor.AroundInterceptor;

public class InterceptorDefinition {

    private Integer interceptorId;
    private AroundInterceptor originalInterceptor;
    private AroundInterceptor reloadedInterceptor;
    private InstrumentClass instrumentClass;

    public InterceptorDefinition(Integer interceptorId, AroundInterceptor originalInterceptor, AroundInterceptor reloadedInterceptor, InstrumentClass instrumentClass) {
        this.interceptorId = interceptorId;
        this.originalInterceptor = originalInterceptor;
        this.reloadedInterceptor = reloadedInterceptor;
        this.instrumentClass = instrumentClass;
    }

    public Integer getInterceptorId() {
        return interceptorId;
    }

    public AroundInterceptor getOriginalInterceptor() {
        return originalInterceptor;
    }

    public AroundInterceptor getReloadedInterceptor() {
        return reloadedInterceptor;
    }

    public InstrumentClass getInstrumentClass() {
        return instrumentClass;
    }
}
