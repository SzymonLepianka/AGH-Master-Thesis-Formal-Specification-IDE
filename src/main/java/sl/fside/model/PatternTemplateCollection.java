package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class PatternTemplateCollection {

    private final UUID id;
    private List<PatternTemplate> patternTemplates = new ArrayList<>();

    @JsonCreator
    public PatternTemplateCollection(@JsonProperty("id") UUID id) {
        this.id = id;
    }

    public List<PatternTemplate> getPatternTemplates() {
        return patternTemplates;
//        return getChildren().stream().filter(x -> x instanceof PatternTemplate).map(x -> (PatternTemplate) x).toList();
    }

    public Optional<PatternTemplate> getPatternTemplateById(UUID id) {
        return patternTemplates.stream().filter(x -> x.getId().equals(id)).findFirst();
    }
}
