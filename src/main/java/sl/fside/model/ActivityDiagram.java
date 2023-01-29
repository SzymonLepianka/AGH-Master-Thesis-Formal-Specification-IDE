package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class ActivityDiagram {

    private UUID id;
    private List<Pattern> patternList = new ArrayList<>();

    @JsonCreator
    public ActivityDiagram(@JsonProperty("id")UUID id){
        this.id = id;
    }

    public List<Pattern> getPatternList() {
        return patternList;
    }

    public void addPatternList(Pattern pattern) {
        patternList.add(pattern);
    }

    public void removePatternList(Pattern pattern) {
        patternList.remove(pattern);
    }

}
