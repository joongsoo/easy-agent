package software.fitz.easyagent.core.asm.helper;

import software.fitz.easyagent.api.MethodDefinition;
import software.fitz.easyagent.api.util.ClassUtils;

import java.util.List;

public class MethodFilter {

    public static boolean isMatchArgs(List<String> argDescriptors, MethodDefinition definition) {

        if (argDescriptors.size() != definition.getArgTypeList().size()) {
            return false;
        }

        List<Class<?>> userDefinedArgs = definition.getArgTypeList();

        for (int i=0; i<argDescriptors.size(); i++) {
            String methodArgInternalName = ClassUtils.descriptorToInternalName(argDescriptors.get(i));
            String userDefinedArgInternalName = ClassUtils.toInternalName(userDefinedArgs.get(i));

            if (!methodArgInternalName.equals(userDefinedArgInternalName)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isMatchReturnType(String methodDescriptor, MethodDefinition definition) {

        String returnTypeDescriptor = ClassUtils.getReturnTypeDescriptor(methodDescriptor);

        return (ClassUtils.isReturnVoid(methodDescriptor) && definition.getReturnType() == Void.class)
                || (ClassUtils.isReturnVoid(methodDescriptor) && definition.getReturnType() == Void.TYPE)
                || (ClassUtils.descriptorToInternalName(returnTypeDescriptor).equals(
                ClassUtils.toInternalName(definition.getReturnType())));
    }

    public static boolean isMatchArgsAndReturnType(String methodDescriptor,
                                                   List<String> argDescriptors,
                                                   MethodDefinition definition) {

        return isMatchArgs(argDescriptors, definition) && isMatchReturnType(methodDescriptor, definition);
    }
}
