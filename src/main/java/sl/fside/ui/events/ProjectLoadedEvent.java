package sl.fside.ui.events;

import sl.fside.events.*;
import sl.fside.model.*;
import sl.fside.ui.*;

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
