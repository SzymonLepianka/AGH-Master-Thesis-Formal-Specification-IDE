package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class AtomicActivity   {

    private final String atomicActivity;
    private UUID id;

    public UUID getId() {
        return id;
    }

    @JsonCreator
    public AtomicActivity(@JsonProperty("id") UUID id, @JsonProperty("atomicActivity") String atomicActivity) {
        this.id = id;
        this.atomicActivity = atomicActivity;
    }

    public String getAtomicActivity() {
        return atomicActivity;
    }
}
