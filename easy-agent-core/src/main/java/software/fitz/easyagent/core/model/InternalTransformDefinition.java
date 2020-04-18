package software.fitz.easyagent.core.model;

import software.fitz.easyagent.api.MethodDefinition;
import software.fitz.easyagent.api.TransformDefinition;
import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.api.strategy.TransformStrategy;

import java.util.List;

public class InternalTransformDefinition {

    private final TransformStrategy transformStrategy;
    private final List<MethodDefinition> targetMethodList;
    private final List<AroundInterceptor> interceptorList;
    private final boolean applyAllMethodInClass;

    public InternalTransformDefinition(TransformStrategy transformStrategy,
                                       List<MethodDefinition> targetMethodList,
                                       List<AroundInterceptor> interceptorList,
                                       boolean applyAllMethodInClass) {
        this.transformStrategy = transformStrategy;
        this.targetMethodList = targetMethodList;
        this.interceptorList = interceptorList;
        this.applyAllMethodInClass = applyAllMethodInClass;
    }

    public TransformStrategy getTransformStrategy() {
        return transformStrategy;
    }

    public List<MethodDefinition> getTargetMethodList() {
        return targetMethodList;
    }

    public boolean isApplyAllMethodInClass() {
        return applyAllMethodInClass;
    }

    public List<AroundInterceptor> getInterceptorList() {
        return interceptorList;
    }

    public static InternalTransformDefinition from(TransformDefinition transformDefinition) {

        return new InternalTransformDefinition(
                transformDefinition.getTransformStrategy(),
                transformDefinition.getTargetMethodList(),
                transformDefinition.getInterceptorList(),
                transformDefinition.isApplyAllMethodInClass()
        );
    }
}
