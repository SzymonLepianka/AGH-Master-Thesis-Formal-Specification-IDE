package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Project extends ModelRootAggregate {

    private UUID atomicActivityCollectionId;

    @JsonCreator
    public Project(@JsonProperty("id") UUID id) {
        super(id);
    }

    public List<UseCaseDiagram> getUseCaseDiagramList() {
        return getChildrenOfType(UseCaseDiagram.class);
    }

    public void addUseCaseDiagram(UseCaseDiagram useCaseDiagram){
        addChild(useCaseDiagram);
    }

    public void removeUseCaseDiagram(UseCaseDiagram useCaseDiagram){
        removeChild(useCaseDiagram);
    }

    public UUID getAtomicActivityCollectionId() {
        return atomicActivityCollectionId;
    }

    public void setAtomicActivityCollectionId(UUID atomicActivityCollectionId) {
        this.atomicActivityCollectionId = atomicActivityCollectionId;
    }
}
