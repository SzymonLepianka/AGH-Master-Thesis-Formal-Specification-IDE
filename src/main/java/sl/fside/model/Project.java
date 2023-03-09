package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Project {

    private final UUID projectId;
    private final String projectName;
    private UUID atomicActivityCollectionId;
    private UseCaseDiagram useCaseDiagram;

    private String base64Image;
    private String imageFileExtension; // TODO możliwe, że do usunięcia

    @JsonCreator
    public Project(@JsonProperty("projectId") UUID projectId, @JsonProperty("projectName") String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public void addUseCaseDiagram(UseCaseDiagram useCaseDiagram) {
        this.useCaseDiagram = useCaseDiagram;
    }

    public void removeUseCaseDiagram() {
        this.useCaseDiagram = null;
    }

    public void addImage(String base64Image) {
        this.base64Image = base64Image;
    }

    public void removeImage() {
        this.base64Image = null;
    }

    public UUID getAtomicActivityCollectionId() {
        return atomicActivityCollectionId;
    }

    public void setAtomicActivityCollectionId(UUID atomicActivityCollectionId) {
        this.atomicActivityCollectionId = atomicActivityCollectionId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public UseCaseDiagram getUseCaseDiagram() {
        return useCaseDiagram;
    }

    public String getImage() {
        return base64Image;
    }

    public String getImageFileExtension() {
        return imageFileExtension;
    }

    public void setImageFileExtension(String imageFileExtension) {
        this.imageFileExtension = imageFileExtension;
    }
}
