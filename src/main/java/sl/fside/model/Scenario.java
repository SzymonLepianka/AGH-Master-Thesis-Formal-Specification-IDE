package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Scenario {

    private final List<Action> actions = new ArrayList<>();
    private final UUID id;
    private final List<ActivityDiagram> activityDiagramList = new ArrayList<>();
    private final List<AtomicActivity> atomicActivities = new ArrayList<>();

    private boolean isMainScenario;

    @JsonCreator
    public Scenario(@JsonProperty("id") UUID id, @JsonProperty("isMainScenario") boolean isMainScenario) {
        this.id = id;
        this.isMainScenario = isMainScenario;
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


    public void addAtomicActivity(AtomicActivity atomicActivity) {
        this.atomicActivities.add(atomicActivity);
    }

    public void removeAtomicActivity(String atomicActivityContent) {
        var atomicActivitiesToRemove =
                this.atomicActivities.stream().filter(aa -> aa.getContent().equals(atomicActivityContent)).toList();
        this.atomicActivities.removeAll(atomicActivitiesToRemove);
    }

    public void removeAtomicActivities(List<AtomicActivity> atomicActivitiesToRemove) {
        this.atomicActivities.removeAll(atomicActivitiesToRemove);
    }

    public List<AtomicActivity> getAtomicActivities() {
        return atomicActivities;
    }

    public String showAtomicActivities() {
        StringBuilder sb = new StringBuilder();
        sb.append("Atomic activities:\n");
        for (AtomicActivity aa : this.atomicActivities) {
            sb.append(" - ").append(aa.getContent()).append("\n");
        }
        return sb.toString();
    }


    public void clearAtomicActivitiesList() {
        this.atomicActivities.clear();
    }

    public void addAction(Action action) {
        this.actions.add(action);
//        propertyChanged("actions");
    }

    public void removeAction(Action action) {
        this.actions.remove(action);
    }

    public UUID getId() {
        return id;
    }

    public List<Action> getActions() {
        return actions;
    }

    public boolean isMainScenario() {
        return isMainScenario;
    }

    public void setIsMainScenario(boolean isMainScenario) {
        this.isMainScenario = isMainScenario;
    }

}
