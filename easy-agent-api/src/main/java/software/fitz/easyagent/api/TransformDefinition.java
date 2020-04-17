package software.fitz.easyagent.api;

import software.fitz.easyagent.api.interceptor.AroundInterceptor;
import software.fitz.easyagent.api.strategy.TransformStrategy;

import java.util.ArrayList;
import java.util.List;

public class TransformDefinition {
    private final TransformStrategy transformStrategy;
    private boolean applyAllMethodInClass;
    private List<MethodDefinition> targetMethodList;
    private final List<AroundInterceptor> interceptorList;

    private TransformDefinition(TransformStrategy transformStrategy,
                                boolean applyAllMethodInClass,
                                List<MethodDefinition> targetMethodList,
                                List<AroundInterceptor> interceptorList) {
        this.transformStrategy = transformStrategy;
        this.applyAllMethodInClass = applyAllMethodInClass;
        this.targetMethodList = targetMethodList;
        this.interceptorList = interceptorList;
    }

    public TransformStrategy getTransformStrategy() {
        return transformStrategy;
    }

    public boolean isApplyAllMethodInClass() {
        return applyAllMethodInClass;
    }

    public List<MethodDefinition> getTargetMethodList() {
        return targetMethodList;
    }

    public List<AroundInterceptor> getInterceptorList() {
        return interceptorList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TransformStrategy transformStrategy;
        private boolean applyAllMethodInClass = false;
        private List<MethodDefinition> targetMethodList = new ArrayList<>();
        private List<AroundInterceptor> interceptorList = new ArrayList<>();

        public Builder transformStrategy(TransformStrategy transformStrategy) {
            this.transformStrategy = transformStrategy;
            return this;
        }

        public Builder applyAllMethodInClass(boolean applyAllMethodInClass) {
            this.applyAllMethodInClass = applyAllMethodInClass;
            return this;
        }

        public Builder addTargetMethod(MethodDefinition methodDefinition) {
            this.targetMethodList.add(methodDefinition);
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

            if (interceptorList.isEmpty()) {
                throw new IllegalStateException("interceptor is must be at least 1");
            }

            if (!applyAllMethodInClass && targetMethodList.isEmpty()) {
                throw new IllegalStateException("targetMethodList is must be at least 1 (or applyAllMethodInClass is active)");
            }

            return new TransformDefinition(transformStrategy, applyAllMethodInClass, targetMethodList, interceptorList);
        }
    }
}
