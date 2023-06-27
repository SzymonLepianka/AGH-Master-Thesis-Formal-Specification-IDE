package sl.fside.services.code_generator2.model;

public class WorkflowNode extends Node {
    public String name;

    public WorkflowNode(String name, DebugInfo debugInfo) {
        super(debugInfo);
        this.name = name;
    }
}
