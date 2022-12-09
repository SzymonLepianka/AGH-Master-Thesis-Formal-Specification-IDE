package sl.fside.ui;

import sl.fside.model.*;

public interface IDomainAware {

    void load(ModelBase object);
    
    void unload();
}
