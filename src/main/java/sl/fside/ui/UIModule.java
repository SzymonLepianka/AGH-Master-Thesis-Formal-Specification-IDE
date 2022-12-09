package sl.fside.ui;

import bgs.formalspecificationide.ui.editors.actionEditor.*;
import bgs.formalspecificationide.ui.editors.actionEditor.controls.*;
import bgs.formalspecificationide.ui.editors.imageViewer.*;
import bgs.formalspecificationide.ui.editors.scenarioSelector.*;
import bgs.formalspecificationide.ui.editors.useCaseSelector.*;
import bgs.formalspecificationide.ui.editors.useCaseSelector.controls.*;
import com.google.inject.*;

public class UIModule extends PrivateModule {

    @Override
    protected void configure() {
        
        // Factories
        bind(IUIElementFactory.class).to(UIElementsFactory.class).in(Scopes.SINGLETON);
        
        // Controllers
        bind(MainWindowController.class).in(Scopes.SINGLETON);
        bind(UseCaseSelectorEditorController.class);
        bind(UseCaseController.class);
        bind(ScenarioSelectorEditorController.class);
        bind(ActionEditorController.class);
        bind(ActionController.class);
        bind(ImageViewerController.class);

        // Expose
        expose(IUIElementFactory.class);
        expose(MainWindowController.class);
        expose(UseCaseSelectorEditorController.class);
        expose(UseCaseController.class);
        expose(ScenarioSelectorEditorController.class);
        expose(ActionEditorController.class);
        expose(ActionController.class);
        expose(ImageViewerController.class);

        // Submodules
    }

}
