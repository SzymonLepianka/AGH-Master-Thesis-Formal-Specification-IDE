package sl.fside.ui.events;

import bgs.formalspecificationide.events.*;
import bgs.formalspecificationide.model.*;
import bgs.formalspecificationide.ui.*;

public class ProjectLoadedEvent extends Event<IController> {

    public Project getProject() {
        return project;
    }

    final Project project;
    
    public ProjectLoadedEvent(IController publisher, Project project) {
        super(publisher);
        this.project = project;
    }
}
