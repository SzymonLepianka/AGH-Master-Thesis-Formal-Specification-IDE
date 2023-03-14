package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Action {

    private final String actionContent;
    private final UUID id;

    @JsonCreator
    public Action(@JsonProperty("id") UUID id, @JsonProperty("actionContent") String actionContent) {
        this.id = id;
        this.actionContent = actionContent;
    }

    public UUID getId() {
        return id;
    }

    public String getActionContent() {
        return actionContent;
    }
}
