package software.fitz.easyagent.core;

import software.fitz.easyagent.api.TransformDefinition;
import software.fitz.easyagent.api.TransformerRegistry;
import software.fitz.easyagent.core.model.InternalTransformDefinition;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TransformerManager implements TransformerRegistry {

    static final TransformerManager INSTANCE = new TransformerManager();

    private List<InternalTransformDefinition> transformDefinitionList;

    private TransformerManager() {
        this.transformDefinitionList = new CopyOnWriteArrayList<>();
    }

    @Override
    public void register(TransformDefinition transformDefinition) {
        this.transformDefinitionList.add(InternalTransformDefinition.from(transformDefinition));
    }

    List<InternalTransformDefinition> getTransformDefinitionList() {
        return transformDefinitionList;
    }

    InternalTransformDefinition getLast() {
        if (transformDefinitionList.isEmpty()) {
            return null;
        }

        return transformDefinitionList.get(transformDefinitionList.size() - 1);
    }
}
