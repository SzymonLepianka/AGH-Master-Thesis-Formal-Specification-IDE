package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class ProjectNameList extends ModelRootAggregate {

    @JsonCreator
    public ProjectNameList(@JsonProperty("id") UUID id) {
        super(id);
    }
}
