package software.fitz.easyagent.core.asm.helper;

import org.objectweb.asm.Label;

/**
 * ASM's label used by tryCatch is implicit. so made for explicit use.
 */
public class TryCatchHelper {
    private Label start; // try block
    private Label handler; // catch block
    private Label end; // end

    public TryCatchHelper() {
        start = new Label();
        handler = new Label();
        end = new Label();
    }

    public Label getStart() {
        return start;
    }

    public Label getHandler() {
        return handler;
    }

    public Label getEnd() {
        return end;
    }
}
