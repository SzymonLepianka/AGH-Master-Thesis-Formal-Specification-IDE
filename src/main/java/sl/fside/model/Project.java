package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Project {

    private final UUID projectId;
    private final String projectName;
    private UseCaseDiagram useCaseDiagram;
    private String base64Image;
    private boolean isCodeGenerationPanelVisible = true;

    @JsonCreator
    public Project(@JsonProperty("projectId") UUID projectId, @JsonProperty("projectName") String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
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

    public void addUseCaseDiagram(UseCaseDiagram useCaseDiagram) {
        this.useCaseDiagram = useCaseDiagram;
    }

    public String getImage() {
        return base64Image;
    }

    public void addImage(String base64Image) {
        this.base64Image = base64Image;
    }

    public void removeImage() {
        this.base64Image = null;
    }

    public boolean isCodeGenerationPanelVisible() {
        return isCodeGenerationPanelVisible;
    }

    public void setCodeGenerationPanelVisible(boolean codeGenerationPanelVisible) {
        isCodeGenerationPanelVisible = codeGenerationPanelVisible;
    }
}
