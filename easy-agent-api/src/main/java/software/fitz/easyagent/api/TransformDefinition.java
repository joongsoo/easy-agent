package software.fitz.easyagent.api;

import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.api.strategy.TransformStrategy;

import java.util.ArrayList;
import java.util.List;

public class TransformDefinition {
    private final TransformStrategy transformStrategy;
    private final String targetMethodName;
    private final List<String> methodArgTypes;
    private final String methodReturnType;
    private final List<AroundInterceptor> interceptorList;

    private TransformDefinition(TransformStrategy transformStrategy,
                                String targetMethodName,
                                List<String> methodArgTypes,
                                String methodReturnType,
                                List<AroundInterceptor> interceptorList) {
        this.transformStrategy = transformStrategy;
        this.targetMethodName = targetMethodName;
        this.methodArgTypes = methodArgTypes;
        this.methodReturnType = methodReturnType;
        this.interceptorList = interceptorList;
    }

    public TransformStrategy getTransformStrategy() {
        return transformStrategy;
    }

    public List<AroundInterceptor> getInterceptorList() {
        return interceptorList;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TransformStrategy transformStrategy;
        private String targetMethodName;
        private List<String> methodArgTypes;
        private String methodReturnType;
        private List<AroundInterceptor> interceptorList = new ArrayList<>();

        public Builder transformStrategy(TransformStrategy transformStrategy) {
            this.transformStrategy = transformStrategy;
            return this;
        }

        public Builder targetMethodName(String targetMethodName) {
            this.targetMethodName = targetMethodName;
            return this;
        }

        public Builder methodArgTypes(List<String> methodArgTypes) {
            this.methodArgTypes = methodArgTypes;
            return this;
        }

        public Builder methodReturnType(String methodReturnType) {
            this.methodReturnType = methodReturnType;
            return this;
        }

        public Builder addInterceptor(AroundInterceptor aroundInterceptor) {
            this.interceptorList.add(aroundInterceptor);
            return this;
        }

        public TransformDefinition build() {
            if (transformStrategy == null) {
                throw new IllegalStateException("transformStrategy must be not null");
            }

            if (targetMethodName == null) {
                throw new IllegalStateException("targetMethodName must be not null");
            }

            if (interceptorList.isEmpty()) {
                throw new IllegalStateException("interceptor is must be at least 1");
            }

            return new TransformDefinition(transformStrategy, targetMethodName,
                    methodArgTypes, methodReturnType, interceptorList);
        }
    }
}
