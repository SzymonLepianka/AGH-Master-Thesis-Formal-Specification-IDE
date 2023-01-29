package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class ProjectNameList   {

    private UUID id;

    @JsonCreator
    public ProjectNameList(@JsonProperty("id") UUID id) {
        this.id = id;
    }
}
