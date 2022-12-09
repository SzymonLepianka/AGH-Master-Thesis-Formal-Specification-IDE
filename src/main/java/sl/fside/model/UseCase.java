package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class UseCase extends ModelAggregate{

    private String useCaseName; // np.: create_order
    private String useCasePrettyName; // np.: Create Order
    private final boolean isImported;
    private final HashMap<UUID, ArrayList<RelationEnum>> relations;
    
    public enum RelationEnum {
        EXTEND,
        INCLUDE,
    }

    @JsonCreator
    public UseCase(@JsonProperty("id")UUID id, @JsonProperty("useCaseName") String useCaseName, @JsonProperty("isImported") boolean isImported){
        super(id);
        this.useCaseName = useCaseName;
        this.isImported = isImported;
        this.relations = new HashMap<>();
    }

    public void setName(String name) {
        if (!useCaseName.equals(name)) {
            this.useCaseName = name;
            propertyChanged("useCaseName");
        }
    }

    public void setPrettyName(String name) {
        if (useCasePrettyName == null || !useCasePrettyName.equals(name)) {
            this.useCasePrettyName = name;
            propertyChanged("useCasePrettyName");
        }
    }

    public void addScenario(Scenario scenario) {
        addChild(scenario);
    }

    public void removeScenario(Scenario scenario) {
        removeChild(scenario);
    }

    @Override
    public void removeChild(ModelBase item) {
        if (item instanceof Scenario scenario && scenario.isMainScenario())
            throw new IllegalArgumentException("Can't remove the main scenario");
        super.removeChild(item);
    }

    @Override
    public void addChild(ModelBase item) {
        if (item instanceof Scenario scenario && scenario.isMainScenario() && getScenarioList().stream().anyMatch(Scenario::isMainScenario))
            throw new IllegalArgumentException("Can't add more than one main scenario");
        super.addChild(item);
    }

    public List<Scenario> getScenarioList() {
        return getChildrenOfType(Scenario.class);
    }

    public void addRelations(UUID otherUseCaseId, RelationEnum relation){
        if(relations.containsKey(otherUseCaseId)){
            relations.get(otherUseCaseId).add(relation);
        }
        else{
            ArrayList<RelationEnum> newRelations = new ArrayList<>();
            newRelations.add(relation);
            relations.put(otherUseCaseId, newRelations);
        }
        propertyChanged("relations");
    }

    public void removeRelations(UUID otherUseCaseId, RelationEnum relation){
        boolean removed = relations.get(otherUseCaseId).remove(relation);
        if(relations.get(otherUseCaseId).isEmpty()){
            relations.remove(otherUseCaseId);
        }
        if(removed) {
            propertyChanged("relations");
        }
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


}
