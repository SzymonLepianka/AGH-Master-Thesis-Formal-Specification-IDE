package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class ActivityDiagram extends ModelAggregate{


    @JsonCreator
    public ActivityDiagram(@JsonProperty("id")UUID id){
        super(id);
    }

    public List<Pattern> getPatternList() {
        return getChildrenOfType(Pattern.class);
    }

    public void addPatternList(Pattern pattern) {
        addChild(pattern);
    }

    public void removePatternList(Pattern pattern) {
        removeChild(pattern);
    }

}
