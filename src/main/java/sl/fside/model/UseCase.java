package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class UseCase {

    private final boolean isImported;
    private final HashMap<UUID, ArrayList<RelationEnum>> relations;
    private final List<Scenario> scenarioList = new ArrayList<>();
    private final UUID id;
    private String useCaseName; // np.: create_order
    private String useCasePrettyName; // np.: Create Order

    @JsonCreator
    public UseCase(@JsonProperty("id") UUID id, @JsonProperty("useCaseName") String useCaseName,
                   @JsonProperty("isImported") boolean isImported) {
        this.id = id;
        this.useCaseName = useCaseName;
        this.isImported = isImported;
        this.relations = new HashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public void setName(String name) {
        if (!useCaseName.equals(name)) {
            this.useCaseName = name;
//            propertyChanged("useCaseName");
        }
    }

    public void setPrettyName(String name) {
        if (useCasePrettyName == null || !useCasePrettyName.equals(name)) {
            this.useCasePrettyName = name;
//            propertyChanged("useCasePrettyName");
        }
    }

    //    @Override
    public void removeScenario(Scenario scenario) {
        if (scenario.isMainScenario()) {
            throw new IllegalArgumentException("Can't remove the main scenario");
        }
        scenarioList.remove(scenario);
//        super.removeChild(item);
    }

//    public void addScenario(Scenario scenario) {
//        addChild(scenario);
//    }
//
//    public void removeScenario(Scenario scenario) {
//        removeChild(scenario);
//    }

    //    @Override
    public void addScenario(Scenario scenario) {
        if (scenario.isMainScenario() && getScenarioList().stream().anyMatch(Scenario::isMainScenario)) {
            throw new IllegalArgumentException("Can't add more than one main scenario");
        }
        scenarioList.add(scenario);
    }

    public List<Scenario> getScenarioList() {
        return scenarioList;
    }

    public void addRelations(UUID otherUseCaseId, RelationEnum relation) {
        if (relations.containsKey(otherUseCaseId)) {
            relations.get(otherUseCaseId).add(relation);
        } else {
            ArrayList<RelationEnum> newRelations = new ArrayList<>();
            newRelations.add(relation);
            relations.put(otherUseCaseId, newRelations);
        }
//        propertyChanged("relations");
    }

    public void removeRelations(UUID otherUseCaseId, RelationEnum relation) {
        boolean removed = relations.get(otherUseCaseId).remove(relation);
        if (relations.get(otherUseCaseId).isEmpty()) {
            relations.remove(otherUseCaseId);
        }
//        if(removed) {
//            propertyChanged("relations");
//        }
    }

    public String getUseCaseName() {
        return useCaseName;
    }

    public String getUseCasePrettyName() {
        return useCasePrettyName;
    }

    public boolean isImported() {
        return isImported;
    }

    public HashMap<UUID, ArrayList<RelationEnum>> getRelations() {
        return new HashMap<>(relations);
    }


    public enum RelationEnum {
        EXTEND, INCLUDE,
    }


}
