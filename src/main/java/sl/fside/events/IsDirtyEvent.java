package sl.fside.events;

import bgs.formalspecificationide.model.*;

public class IsDirtyEvent extends Event<ModelBase> {

    public IsDirtyEvent(ModelBase publisher) {
        super(publisher);
    }
}
