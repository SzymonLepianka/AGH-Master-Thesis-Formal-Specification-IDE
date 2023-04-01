package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Scenario {

    private final List<Action> actions = new ArrayList<>();
    private final UUID id;
    private final List<ActivityDiagram> activityDiagramList = new ArrayList<>();
    private final List<AtomicActivity> atomicActivities = new ArrayList<>();
    private String patternExpression;
    private String folLogicalSpecification;
    private String ltlLogicalSpecification;
    private boolean isMainScenario;
    private String scenarioName;

    @JsonCreator
    public Scenario(@JsonProperty("id") UUID id, @JsonProperty("isMainScenario") boolean isMainScenario,
                    @JsonProperty("scenarioName") String scenarioName) {
        this.id = id;
        this.scenarioName = scenarioName;
        this.isMainScenario = isMainScenario;
    }

    public String getPatternExpression() {
        return patternExpression;
    }

    public void setPatternExpression(String patternExpression) {
        this.patternExpression = patternExpression;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getFolLogicalSpecification() {
        return folLogicalSpecification;
    }

    public void setFolLogicalSpecification(String folLogicalSpecification) {
        this.folLogicalSpecification = folLogicalSpecification;
    }

    public String getLtlLogicalSpecification() {
        return ltlLogicalSpecification;
    }

    public void setLtlLogicalSpecification(String ltlLogicalSpecification) {
        this.ltlLogicalSpecification = ltlLogicalSpecification;
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
        if (this.atomicActivities.isEmpty()) {
            sb.append("No atomic activities!");
        } else {
            sb.append("Atomic activities:\n");
            for (AtomicActivity aa : this.atomicActivities) {
                sb.append(" - ").append(aa.getContent()).append("\n");
            }
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
