package sl.fside.model;

import bgs.formalspecificationide.events.*;
import bgs.formalspecificationide.utilities.*;
import com.fasterxml.jackson.annotation.*;

import java.util.*;

public abstract class ModelRootAggregate extends ModelAggregate implements IIsDirty, ICanSetDirty, IAggregateRoot<ModelBase> {

    @JsonIgnore
    private boolean isDirty;

    public ModelRootAggregate(UUID id) {
        super(id);
        isDirty = false;
    }

    @Override
    public final boolean isDirty() {
        return isDirty;
    }

    @Override
    public final void clearIsDirty() {
        isDirty = false;
    }

    @Override
    public final void setDirty() {
        isDirty = true;
    }

    @Override
    public void notify(Event<?> event) {
        super.notify(event);

        if (event instanceof IsDirtyEvent) {
            isDirty = true;
        }
    }

    @Override
    protected void propertyChanged(String propertyName) {
        super.propertyChanged(propertyName);
        isDirty = true;
    }
}
