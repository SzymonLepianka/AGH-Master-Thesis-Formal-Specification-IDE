package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class UseCaseDiagram {

    private UUID imageID;
    private final UUID useCaseDiagramId;
    private final List<UseCase> useCaseList = new ArrayList<>();

    @JsonIgnore
    private Map<String, Map<String, List<String>>> useCasesRaw;

    @JsonCreator
    public UseCaseDiagram(@JsonProperty("useCaseDiagramId") UUID useCaseDiagramId) {
        this.useCaseDiagramId = useCaseDiagramId;
    }

    public List<UseCase> getUseCaseList() {
        return useCaseList;
    }

    public void addUseCase(UseCase useCase) {
        useCaseList.add(useCase);
    }

    public void removeUseCase(UseCase useCase) {
        useCaseList.remove(useCase);
    }

    public UUID getImageID() {
        return imageID;
    }

    public void setImageID(UUID imageID) {
        if (!this.imageID.equals(imageID)) {
            this.imageID = imageID;
//            propertyChanged("imageId");
        }
    }

    public Map<String, Map<String, List<String>>> getUseCasesRaw() {
        return useCasesRaw;
    }

    public void setUseCasesRaw(Map<String, Map<String, List<String>>> useCasesRaw) {
        if (this.useCasesRaw == null || !this.useCasesRaw.toString().equals(useCasesRaw.toString())) {
            this.useCasesRaw = useCasesRaw;
//            propertyChanged("useCasesRaw");
        }
    }

    public UUID getUseCaseDiagramId() {
        return useCaseDiagramId;
    }
}
