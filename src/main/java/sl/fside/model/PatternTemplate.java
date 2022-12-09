package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class PatternTemplate extends ModelBase {

    private String name;

    private int inputs;

    private int outputs;

    private final HashSet<String> elementaryPatternList;

    @JsonCreator
    public PatternTemplate(@JsonProperty("id") UUID id,@JsonProperty("name") String name,@JsonProperty("inputs") int inputs,@JsonProperty("outputs") int outputs) {
        super(id);
        this.name = name;
        this.inputs = inputs;
        this.outputs = outputs;
        elementaryPatternList = new HashSet<>();
    }

    public void addElementaryPattern(String elementaryPattern) {
        elementaryPatternList.add(elementaryPattern);
        propertyChanged("elementaryPatternList");
    }

    public void removeElementaryPattern(String elementaryPattern) {
        if (elementaryPatternList.remove(elementaryPattern))
            propertyChanged("elementaryPatternList");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInputs() {
        return inputs;
    }

    public void setInputs(int inputs) {
        this.inputs = inputs;
    }

    public int getOutputs() {
        return outputs;
    }

    public void setOutputs(int outputs) {
        this.outputs = outputs;
    }
}
