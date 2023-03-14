package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Scenario {

    private final ArrayList<Action> actions;
    private final UUID id;
    private final List<ActivityDiagram> activityDiagramList = new ArrayList<>();
    private Set<UUID> atomicActivities;
    private boolean isMainScenario;

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

    public void addAction(Action action) {
        this.actions.add(action);
//        propertyChanged("actions");
    }

    public void removeActions(String action) {
        if (this.actions.remove(action)) {
//            propertyChanged("actions");
        }
    }

    public UUID getId() {
        return id;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public boolean isMainScenario() {
        return isMainScenario;
    }

    public void setIsMainScenario(boolean isMainScenario) {
        this.isMainScenario = isMainScenario;
    }

    public Set<UUID> getAtomicActivities() {
        return atomicActivities;
    }
}
