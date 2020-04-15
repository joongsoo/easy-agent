package software.fitz.easyagent.core.model;

import software.fitz.easyagent.api.TransformDefinition;
import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.api.strategy.TransformStrategy;

import java.util.List;

public class InternalTransformDefinition {

    private final TransformStrategy transformStrategy;
    private final String targetMethodName;
    private final List<String> methodArgTypes;
    private final String methodReturnType;
    private final List<AroundInterceptor> interceptorList;

    public InternalTransformDefinition(TransformStrategy transformStrategy, String targetMethodName, List<String> methodArgTypes, String methodReturnType, List<AroundInterceptor> interceptorList) {
        this.transformStrategy = transformStrategy;
        this.targetMethodName = targetMethodName;
        this.methodArgTypes = methodArgTypes;
        this.methodReturnType = methodReturnType;
        this.interceptorList = interceptorList;
    }

    public TransformStrategy getTransformStrategy() {
        return transformStrategy;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public List<String> getMethodArgTypes() {
        return methodArgTypes;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }

    public List<AroundInterceptor> getInterceptorList() {
        return interceptorList;
    }

    public static InternalTransformDefinition from(TransformDefinition transformDefinition) {

        return new InternalTransformDefinition(
                transformDefinition.getTransformStrategy(),
                transformDefinition.getTargetMethodName(),
                transformDefinition.getMethodArgTypes(),
                transformDefinition.getMethodReturnType(),
                transformDefinition.getInterceptorList()
        );
    }
}
