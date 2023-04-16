package sl.fside.ui;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.util.*;
import sl.fside.model.*;
import sl.fside.ui.editors.requirementEditor.controls.*;
import sl.fside.ui.editors.scenarioSelector.controls.*;
import sl.fside.ui.editors.useCaseSelector.controls.*;
import sl.fside.ui.editors.verificationEditor.controls.*;

import java.io.*;

public class UIElementsFactory {

    private final Injector injector;

    private final String editorsPath = "editors/";
    private final String useCaseSelectorControlsPath = editorsPath + "useCaseSelector/controls/";
    private final String scenarioSelectorControlsPath = editorsPath + "scenarioSelector/controls/";
    private final String actionEditorControlsPath = editorsPath + "actionEditor/controls/";
    private final String requirementEditorControlsPath = editorsPath + "requirementEditor/controls/";
    private final String verificationEditorControlsPath = editorsPath + "verificationEditor/controls/";

    @Inject
    public UIElementsFactory(Injector injector) {
        this.injector = injector;
    }

    public Pair<AnchorPane, UseCaseController> createUseCase(UseCase useCase) {
        var pair = this.<AnchorPane, UseCaseController>loadFromFxmnl(useCaseSelectorControlsPath + "UseCase.fxml");
        pair.getValue().load(useCase);
        return pair;
    }

    public Pair<AnchorPane, ScenarioController> createScenario(Scenario scenario) {
        var pair = this.<AnchorPane, ScenarioController>loadFromFxmnl(scenarioSelectorControlsPath + "Scenario.fxml");
        pair.getValue().load(scenario);
        return pair;
    }

//    public Pair<AnchorPane, ActionController> createAction(Action action) {
//        var pair = this.<AnchorPane, ActionController>loadFromFxmnl(actionEditorControlsPath + "Action.fxml");
//        pair.getValue().load(action);
//        return pair;
//    }

    public Pair<AnchorPane, RequirementController> createRequirement(Requirement requirement) {
        var pair = this.<AnchorPane, RequirementController>loadFromFxmnl(
                requirementEditorControlsPath + "Requirement.fxml");
        pair.getValue().load(requirement);
        return pair;
    }

    public Pair<AnchorPane, VerificationController> createVerification(Verification verification) {
        var pair = this.<AnchorPane, VerificationController>loadFromFxmnl(
                verificationEditorControlsPath + "Verification.fxml");
        pair.getValue().load(verification);
        return pair;
    }

    private <TUIElement extends Node, TController> Pair<TUIElement, TController> loadFromFxmnl(String path) {
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource(path));
            fxmlLoader.setControllerFactory(injector::getInstance);

            var result = fxmlLoader.<TUIElement>load();
            var controller = fxmlLoader.<TController>getController();
            return new Pair<>(result, controller);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
    }
}
