package sl.fside.events;

import bgs.formalspecificationide.model.*;

public class PropertyChangedEvent extends Event<ModelBase> {

    private final String propertyName;


    public PropertyChangedEvent(ModelBase publisher, String propertyName) {
        super(publisher);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}