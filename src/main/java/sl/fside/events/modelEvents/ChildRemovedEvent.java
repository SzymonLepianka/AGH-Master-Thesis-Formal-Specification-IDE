package sl.fside.events.modelEvents;

import bgs.formalspecificationide.model.*;

public class ChildRemovedEvent extends ModelAggregateModifiedEvent{
    public ChildRemovedEvent(ModelAggregate publisher, ModelBase item) {
        super(publisher, item, ModificationEnum.REMOVED);
    }
}
