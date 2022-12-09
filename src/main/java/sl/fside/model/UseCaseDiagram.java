package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class UseCaseDiagram extends ModelAggregate {

    private UUID imageID;
    
    @JsonIgnore
    private Map<String, Map<String, List<String>>> useCasesRaw;

    @JsonCreator
    public UseCaseDiagram(@JsonProperty("id")UUID id, @JsonProperty("imageID") UUID imageID) {
        super(id);
        this.imageID = imageID;
    }

    public List<UseCase> getUseCaseList() {
        return getChildrenOfType(UseCase.class);
    }

    public void addUseCase(UseCase useCase){
        addChild(useCase);
    }

    public void removeUseCase(UseCase useCase){
        removeChild(useCase);
    }

    public UUID getImageID() {
        return imageID;
    }

    public void setImageID(UUID imageID) {
        if (!this.imageID.equals(imageID)) {
            this.imageID = imageID;
            propertyChanged("imageId");
        }
    }

    public Map<String, Map<String, List<String>>> getUseCasesRaw() {
        return useCasesRaw;
    }

    public void setUseCasesRaw(Map<String, Map<String, List<String>>> useCasesRaw) {
        if (this.useCasesRaw == null || !this.useCasesRaw.toString().equals(useCasesRaw.toString())) {
            this.useCasesRaw = useCasesRaw;
            propertyChanged("useCasesRaw");
        }
    }
}
