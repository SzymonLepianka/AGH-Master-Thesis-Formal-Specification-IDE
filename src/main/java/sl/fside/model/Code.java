package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Code {

    private final UUID id;
    private String atomicActivity;
    private String code;

    @JsonCreator
    public Code(@JsonProperty("id") UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getAtomicActivity() {
        return atomicActivity;
    }

    public void setAtomicActivity(String atomicActivity) {
        this.atomicActivity = atomicActivity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
