package sl.fside.model;

import com.fasterxml.jackson.annotation.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;

import java.util.*;

public class Scenario {

    private final UUID id;
    private final List<AtomicActivity> atomicActivities = new ArrayList<>();
    private final List<Code> codes = new ArrayList<>();
    private final List<Requirement> requirements = new ArrayList<>();
    private final List<Verification> verifications = new ArrayList<>();
    private int maxRequirementSerialNumber = 0;
    private String content = "";
    private PatternExpression patternExpression;
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

    public PatternExpression getPatternExpression() {
        return patternExpression;
    }

    public void setPatternExpression(PatternExpression patternExpression) {
        this.patternExpression = patternExpression;
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
        NodesManager.getInstance()
                .setCurrentAtomicActivities(atomicActivities.stream().map(AtomicActivity::getContent).toList());
    }

    public void removeAtomicActivity(String atomicActivityContent) {
        var atomicActivitiesToRemove =
                this.atomicActivities.stream().filter(aa -> aa.getContent().equals(atomicActivityContent)).toList();
        this.atomicActivities.removeAll(atomicActivitiesToRemove);
        NodesManager.getInstance()
                .setCurrentAtomicActivities(atomicActivities.stream().map(AtomicActivity::getContent).toList());
    }

    public void removeAllAtomicActivities() {
        this.atomicActivities.clear();
        NodesManager.getInstance()
                .setCurrentAtomicActivities(atomicActivities.stream().map(AtomicActivity::getContent).toList());
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

    public List<Code> getCodes() {
        return codes;
    }

    public void addCode(Code code) {
        this.codes.add(code);
    }

    public void removeCode(Code code) {
        this.codes.remove(code);
    }

    public int getMaxRequirementSerialNumber() {
        return maxRequirementSerialNumber;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
        maxRequirementSerialNumber++;
    }

    public void removeRequirement(Requirement requirement) {
        this.requirements.remove(requirement);
    }

    public List<Verification> getVerifications() {
        return verifications;
    }

    public void addVerification(Verification verification) {
        this.verifications.add(verification);
    }

    public void removeVerification(Verification verification) {
        this.verifications.remove(verification);
    }
}
