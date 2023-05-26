package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Verification {

    private final UUID id;
    private String content;
    private String prover;
    private boolean isResultGenerated = false;

    @JsonCreator
    public Verification(@JsonProperty("id") UUID id) {
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

    public String getProver() {
        return prover;
    }

    public void setProver(String prover) {
        this.prover = prover;
    }

    public boolean isResultGenerated() {
        return isResultGenerated;
    }

    public void setResultGenerated(boolean resultGenerated) {
        isResultGenerated = resultGenerated;
    }
}
