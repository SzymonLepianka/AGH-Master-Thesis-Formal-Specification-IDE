package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class AtomicActivity {

    private final String atomicActivityContent;
    private final UUID id;

    @JsonCreator
    public AtomicActivity(@JsonProperty("id") UUID id,
                          @JsonProperty("atomicActivityContent") String atomicActivityContent) {
        this.id = id;
        this.atomicActivityContent = atomicActivityContent;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return atomicActivityContent;
    }
}
