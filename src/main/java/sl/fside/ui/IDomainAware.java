package sl.fside.ui;

import bgs.formalspecificationide.model.*;

public interface IDomainAware {

    void load(ModelBase object);
    
    void unload();
}
