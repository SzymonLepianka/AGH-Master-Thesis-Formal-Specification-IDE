package sl.fside.events.modelEvents;

import sl.fside.events.*;
import sl.fside.model.*;

public abstract class ModelAggregateModifiedEvent extends CollectionModifiedEvent<ModelAggregate, ModelBase> {
    public ModelAggregateModifiedEvent(ModelAggregate publisher, ModelBase item, ModificationEnum modification) {
        super(publisher, item, modification, "children");
    }
}
