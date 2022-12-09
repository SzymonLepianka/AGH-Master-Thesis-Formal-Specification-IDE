package sl.fside.ui;

import sl.fside.ui.editors.actionEditor.*;
import sl.fside.ui.editors.actionEditor.controls.*;
import sl.fside.ui.editors.imageViewer.*;
import sl.fside.ui.editors.scenarioSelector.*;
import sl.fside.ui.editors.useCaseSelector.*;
import sl.fside.ui.editors.useCaseSelector.controls.*;
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
