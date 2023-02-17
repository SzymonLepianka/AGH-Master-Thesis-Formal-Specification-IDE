package sl.fside.ui;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import sl.fside.model.Scenario;
import sl.fside.model.UseCase;
import sl.fside.ui.editors.actionEditor.controls.ActionController;
import sl.fside.ui.editors.scenarioSelector.controls.ScenarioController;
import sl.fside.ui.editors.useCaseSelector.controls.UseCaseController;

import java.io.IOException;

public class UIElementsFactory {

    private final Injector injector;

    private final String editorsPath = "editors/";
    private final String useCaseSelectorControlsPath = editorsPath + "useCaseSelector/controls/";
    private final String scenarioSelectorControlsPath = editorsPath + "scenarioSelector/controls/";
    private final String actionEditorControlsPath = editorsPath + "actionEditor/controls/";

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

    public Pair<AnchorPane, ActionController> createAction(String action) {
        var pair = this.<AnchorPane, ActionController>loadFromFxmnl(actionEditorControlsPath + "Action.fxml");
        pair.getValue().load(action);
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
