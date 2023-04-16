package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Scenario {

    private final UUID id;
    private final List<AtomicActivity> atomicActivities = new ArrayList<>();
    private final List<Requirement> requirements = new ArrayList<>();
    private String content = "";
    private String patternExpressionBeforeProcessingNesting;
    private String patternExpressionAfterProcessingNesting;
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

    public String getPatternExpressionBeforeProcessingNesting() {
        return patternExpressionBeforeProcessingNesting;
    }

    public void setPatternExpressionBeforeProcessingNesting(String patternExpressionBeforeProcessingNesting) {
        this.patternExpressionBeforeProcessingNesting = patternExpressionBeforeProcessingNesting;
    }

    public String getPatternExpressionAfterProcessingNesting() {
        return patternExpressionAfterProcessingNesting;
    }

    public void setPatternExpressionAfterProcessingNesting(String patternExpressionAfterProcessingNesting) {
        this.patternExpressionAfterProcessingNesting = patternExpressionAfterProcessingNesting;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public void addAtomicActivity(AtomicActivity atomicActivity) {
        this.atomicActivities.add(atomicActivity);
    }

    public void removeAtomicActivity(String atomicActivityContent) {
        var atomicActivitiesToRemove =
                this.atomicActivities.stream().filter(aa -> aa.getContent().equals(atomicActivityContent)).toList();
        this.atomicActivities.removeAll(atomicActivitiesToRemove);
    }

    public void removeAllAtomicActivities() {
        this.atomicActivities.clear();
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
                sb.append(" - '").append(aa.getContent()).append("'\n");
            }
        }
        return sb.toString();
    }

    public UUID getId() {
        return id;
    }

    public boolean isMainScenario() {
        return isMainScenario;
    }

    public void setIsMainScenario(boolean isMainScenario) {
        this.isMainScenario = isMainScenario;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }

    public void removeRequirement(Requirement requirement) {
        this.requirements.remove(requirement);
    }
}
