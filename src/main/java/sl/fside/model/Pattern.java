package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Pattern {

    private String name;

    private final UUID id;
    private final UUID patternTemplateId;

    private final HashMap<String, UUID> fieldValues;

    private final ArrayList<UUID> inputs;

    private final ArrayList<UUID> outputs;

    @JsonCreator
    public Pattern(@JsonProperty("id") UUID id, @JsonProperty("name") String name, @JsonProperty("patternTemplateId") UUID patternTemplateId){
        this.id = id;
        this.name = name;
        this.patternTemplateId = patternTemplateId;
        this.fieldValues = new HashMap<>();
        this.outputs = new ArrayList<>();
        this.inputs = new ArrayList<>();
    }

    public void setName(String name){
        if (!this.name.equals(name)) {
            this.name = name;
//            propertyChanged("name");
        }
    }

//    public void addInput(Pattern pattern){
//        this.inputs.add(pattern.getId());
//
//        //notifyObservers(new CollectionModifiedEvent<>(this, pattern, CollectionModifiedEvent.ModificationEnum.ADDED, "inputs"));
//        //notifyDirty();
////        propertyChanged("inputs");
//    }
//
//    public void removeInput(Pattern pattern){
//        if(this.inputs.remove(pattern.getId())){
//            //notifyObservers(new CollectionModifiedEvent<>(this, pattern, CollectionModifiedEvent.ModificationEnum.REMOVED, "inputs"));
//            //notifyDirty();
////            propertyChanged("inputs");
//        }
//    }

//    public void addOutput(Pattern pattern){
//        this.outputs.add(pattern.getId());
//        //notifyObservers(new CollectionModifiedEvent<>(this, pattern, CollectionModifiedEvent.ModificationEnum.ADDED, "outputs"));
//        //notifyDirty();
//        propertyChanged("outputs");
//    }
//
//    public void removeOutput(Pattern pattern){
//        if(this.outputs.remove(pattern.getId())){
//            //notifyObservers(new CollectionModifiedEvent<>(this, pattern, CollectionModifiedEvent.ModificationEnum.REMOVED, "outputs"));
//            //notifyDirty();
//            propertyChanged("outputs");
//        }
//    }

//    public void addFieldValue(String fieldID, UUID patternTemplateId){
//        var oldValue = fieldValues.get(fieldID);
//        fieldValues.put(fieldID, patternTemplateId);
//        /*if(oldValue != null) {
//            // TODO Those two events should be changed to something different or skipped altogether (maybe put here some generic event like CollectionModified without items - just a collection name)
//            //notifyObservers(new CollectionModifiedEvent<>(this, fieldID, CollectionModifiedEvent.ModificationEnum.REMOVED, "fieldValues"));
//        }*/
//        //notifyObservers(new CollectionModifiedEvent<>(this, fieldID, CollectionModifiedEvent.ModificationEnum.ADDED, "fieldValues"));
//        //notifyDirty();
//        propertyChanged("fieldValues");
//    }
//
//    public void removeRelations(String fieldID, UUID patternTemplateId){
//        if(fieldValues.remove(fieldID) != null){
//            //notifyObservers(new CollectionModifiedEvent<>(this, fieldID, CollectionModifiedEvent.ModificationEnum.REMOVED, "fieldValues"));
//            propertyChanged("fieldValues");
//        }
//    }
//
//    public UUID getPatternTemplateId() {
//        return patternTemplateId;
//    }
}
