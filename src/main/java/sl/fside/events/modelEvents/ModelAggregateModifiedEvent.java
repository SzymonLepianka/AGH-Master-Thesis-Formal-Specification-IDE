package sl.fside.events.modelEvents;

import bgs.formalspecificationide.events.*;
import bgs.formalspecificationide.model.*;

public abstract class ModelAggregateModifiedEvent extends CollectionModifiedEvent<ModelAggregate, ModelBase> {
    public ModelAggregateModifiedEvent(ModelAggregate publisher, ModelBase item, ModificationEnum modification) {
        super(publisher, item, modification, "children");
    }
}
