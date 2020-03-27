package software.fitz.easyagent.core.model;

import software.fitz.easyagent.core.interceptor.AroundInterceptor;

public class InterceptorDefinition {
    private Integer interceptorId;
    private AroundInterceptor interceptor;
    private InstrumentClass instrumentClass;

    public InterceptorDefinition(Integer interceptorId, AroundInterceptor interceptor, InstrumentClass instrumentClass) {
        this.interceptorId = interceptorId;
        this.interceptor = interceptor;
        this.instrumentClass = instrumentClass;
    }

    public Integer getInterceptorId() {
        return interceptorId;
    }

    public AroundInterceptor getInterceptor() {
        return interceptor;
    }

    public InstrumentClass getInstrumentClass() {
        return instrumentClass;
    }
}
