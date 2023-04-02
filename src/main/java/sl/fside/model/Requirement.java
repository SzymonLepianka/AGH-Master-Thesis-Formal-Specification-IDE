package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Requirement {

    private final UUID id;
    private String content;
    private String logic;
    private boolean active = false;

    @JsonCreator
    public Requirement(@JsonProperty("id") UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
