package sl.fside.services.code_generator2.model;

public class MethodNode extends Node {
    public Method method;
    public boolean statement = true;

    public MethodNode(Method method, DebugInfo debugInfo) {
        super(debugInfo);
        this.method = method;
    }
}
