package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class UseCaseDiagram {

    private final UUID useCaseDiagramId;
    private final List<UseCase> useCaseList = new ArrayList<>();
    private final List<Relation> relationList = new ArrayList<>();

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

    public UUID getUseCaseDiagramId() {
        return useCaseDiagramId;
    }

    public void addRelation(Relation relation) {
        relationList.add(relation);
    }

    public List<Relation> getRelations() {
        return relationList;
    }

    public String showRelations() {
        StringBuilder sb = new StringBuilder();
        if (this.relationList.isEmpty()) {
            sb.append("No relations!");
        } else {
            sb.append("Relations:\n");
            int idx = 1;
            for (Relation relation : this.relationList) {
                sb.append(idx);
                sb.append(". ");
                sb.append(getUseCaseNameFromId(relation.getFromId()));
                sb.append(" ");
                sb.append(relation.getPrettyType());
                sb.append(" ");
                sb.append(getUseCaseNameFromId(relation.getToId()));
                sb.append("\n");
                idx++;
            }
        }
        return sb.toString();
    }

    public String getUseCaseNameFromId(UUID useCaseId) {
        UseCase useCase = useCaseList.stream().filter(uc -> uc.getId().equals(useCaseId)).findFirst().orElseThrow();
        return useCase.getUseCaseName();
    }
}
