package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class AtomicActivityCollection {

    private final UUID projectId;
    private final UUID atomicActivityCollectionId;
    private final List<AtomicActivity> atomicActivities = new ArrayList<>();

    @JsonCreator
    public AtomicActivityCollection(@JsonProperty("atomicActivityCollectionId") UUID atomicActivityCollectionId,
                                    @JsonProperty("projectId") UUID projectId) {
        this.atomicActivityCollectionId = atomicActivityCollectionId;
        this.projectId = projectId;
    }

    public List<AtomicActivity> getAtomicActivities() {
        return atomicActivities;
    }

    // TODO adding atomic activity

    public Optional<AtomicActivity> getAtomicActivityById(UUID id) {
        return atomicActivities.stream().filter(x -> x.getId().equals(id)).findFirst();
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getAtomicActivityCollectionId() {
        return atomicActivityCollectionId;
    }
}
