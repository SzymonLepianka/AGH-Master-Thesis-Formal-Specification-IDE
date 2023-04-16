package sl.fside.ui;

import com.google.inject.*;
import sl.fside.ui.editors.actionEditor.*;
import sl.fside.ui.editors.activityDiagramPanel.*;
import sl.fside.ui.editors.generateCodePanel.*;
import sl.fside.ui.editors.imageViewer.*;
import sl.fside.ui.editors.requirementEditor.*;
import sl.fside.ui.editors.requirementEditor.controls.*;
import sl.fside.ui.editors.resultsPanel.*;
import sl.fside.ui.editors.scenarioSelector.*;
import sl.fside.ui.editors.scenarioSelector.controls.*;
import sl.fside.ui.editors.useCaseSelector.*;
import sl.fside.ui.editors.useCaseSelector.controls.*;
import sl.fside.ui.editors.verificationEditor.*;
import sl.fside.ui.editors.verificationEditor.controls.*;

public class UIModule extends PrivateModule {

    @Override
    protected void configure() {

        // Factories
        bind(UIElementsFactory.class).in(Scopes.SINGLETON);

        // Controllers
        bind(MainWindowController.class).in(Scopes.SINGLETON);
        bind(UseCaseSelectorEditorController.class);
        bind(UseCaseController.class);
        bind(ImageViewerController.class);
        bind(ScenarioSelectorEditorController.class);
        bind(ScenarioController.class);
        bind(ActionEditorController.class);
        bind(ActivityDiagramPanelController.class);
        bind(ResultsPanelController.class);
        bind(GenerateCodePanelController.class);
        bind(RequirementEditorController.class);
        bind(RequirementController.class);
        bind(VerificationEditorController.class);
        bind(VerificationController.class);

        // Expose
        expose(UIElementsFactory.class);
        expose(MainWindowController.class);
        expose(UseCaseSelectorEditorController.class);
        expose(UseCaseController.class);
        expose(ImageViewerController.class);
        expose(ScenarioSelectorEditorController.class);
        expose(ScenarioController.class);
        expose(ActionEditorController.class);
        expose(ActivityDiagramPanelController.class);
        expose(ResultsPanelController.class);
        expose(GenerateCodePanelController.class);
        expose(RequirementEditorController.class);
        expose(RequirementController.class);
        expose(VerificationEditorController.class);
        expose(VerificationController.class);

        // Submodules
    }

}
