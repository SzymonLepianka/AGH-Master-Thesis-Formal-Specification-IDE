package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class AtomicActivity extends ModelBase {

    private final String atomicActivity;

    @JsonCreator
    public AtomicActivity(@JsonProperty("id") UUID id, @JsonProperty("atomicActivity") String atomicActivity) {
        super(id);
        this.atomicActivity = atomicActivity;
    }

    public String getAtomicActivity() {
        return atomicActivity;
    }
}
