package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class UseCase {

    private final boolean isImported;
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
    }

    public UUID getId() {
        return id;
    }

    public void setName(String name) {
        if (!useCaseName.equals(name)) {
            this.useCaseName = name;
        }
    }

    public void setPrettyName(String name) {
        if (useCasePrettyName == null || !useCasePrettyName.equals(name)) {
            this.useCasePrettyName = name;
        }
    }

    public void removeScenario(Scenario scenario) {
        if (scenario.isMainScenario()) {
            throw new IllegalArgumentException("Can't remove the main scenario");
        }
        scenarioList.remove(scenario);
    }

    public void addScenario(Scenario scenario) {
        if (scenario.isMainScenario() && getScenarioList().stream().anyMatch(Scenario::isMainScenario)) {
            throw new IllegalArgumentException("Can't add more than one main scenario");
        }
        scenarioList.add(scenario);
    }

    public List<Scenario> getScenarioList() {
        return scenarioList;
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
}
