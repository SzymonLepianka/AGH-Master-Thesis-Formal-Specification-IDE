package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Requirement {

    private final UUID id;
    private String requirementContent;

    @JsonCreator
    public Requirement(@JsonProperty("id") UUID id, @JsonProperty("requirementContent") String requirementContent) {
        this.id = id;
        this.requirementContent = requirementContent;
    }

    public UUID getId() {
        return id;
    }

    public String getRequirementContent() {
        return requirementContent;
    }

    public void setRequirementContent(String requirementContent) {
        this.requirementContent = requirementContent;
    }
}
