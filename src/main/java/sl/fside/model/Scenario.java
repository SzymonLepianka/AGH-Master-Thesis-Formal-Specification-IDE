package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Scenario extends ModelAggregate{

    private final ArrayList<String> actions;
    private final boolean isMainScenario;
    Set<UUID> atomicActivities;

    @JsonCreator
    public Scenario(@JsonProperty("id")UUID id, @JsonProperty("isMainScenario") boolean isMainScenario){
        super(id);
        this.isMainScenario = isMainScenario;
        this.actions = new ArrayList<>();
    }

    public List<ActivityDiagram> getActivityDiagram() {
        return getChildrenOfType(ActivityDiagram.class);
    }

    public void setActivityDiagram(ActivityDiagram activityDiagram) {
        addChild(activityDiagram);
    }

    public void removeActivityDiagram(ActivityDiagram activityDiagram){
        removeChild(activityDiagram);
    }

    public void addAtomicActivity(UUID atomicId){
        this.atomicActivities.add(atomicId);
        propertyChanged("atomicActivities");
    }

    public void removeAtomicActivity(UUID atomicId){
        if(this.atomicActivities.remove(atomicId)) {
            propertyChanged("atomicActivities");
        }
    }

    public void addActions(String action){
        this.actions.add(action);
        propertyChanged("actions");
    }

    public void removeActions(String action){
        if(this.actions.remove(action)) {
            propertyChanged("actions");
        }
    }

    public ArrayList<String> getActions() {
        return actions;
    }

    public boolean isMainScenario() {
        return isMainScenario;
    }

    public Set<UUID> getAtomicActivities() {
        return atomicActivities;
    }


}
