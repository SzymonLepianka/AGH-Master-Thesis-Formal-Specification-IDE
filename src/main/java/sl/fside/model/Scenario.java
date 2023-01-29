package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Scenario {

    private final ArrayList<String> actions;
    private final boolean isMainScenario;
    Set<UUID> atomicActivities;
    private final UUID id;
    private final List<ActivityDiagram> activityDiagramList = new ArrayList<>();

    @JsonCreator
    public Scenario(@JsonProperty("id") UUID id, @JsonProperty("isMainScenario") boolean isMainScenario) {
        this.id = id;
        this.isMainScenario = isMainScenario;
        this.actions = new ArrayList<>();
    }

    public List<ActivityDiagram> getActivityDiagram() {
        return activityDiagramList;
    }

    public void addActivityDiagram(ActivityDiagram activityDiagram) {
        activityDiagramList.add(activityDiagram);
    }

    public void removeActivityDiagram(ActivityDiagram activityDiagram) {
        activityDiagramList.remove(activityDiagram);
    }

    public void addAtomicActivity(UUID atomicId) {
        this.atomicActivities.add(atomicId);
//        propertyChanged("atomicActivities");
    }

    public void removeAtomicActivity(UUID atomicId) {
        if (this.atomicActivities.remove(atomicId)) {
//            propertyChanged("atomicActivities");
        }
    }

    public void addActions(String action) {
        this.actions.add(action);
//        propertyChanged("actions");
    }

    public void removeActions(String action) {
        if (this.actions.remove(action)) {
//            propertyChanged("actions");
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
