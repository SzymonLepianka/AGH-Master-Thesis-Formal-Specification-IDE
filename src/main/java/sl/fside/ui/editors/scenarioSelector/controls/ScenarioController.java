package sl.fside.ui.editors.scenarioSelector.controls;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.model.*;
import sl.fside.services.*;

import java.util.function.*;

public class ScenarioController {

    private final LoggerService loggerService;

    @FXML
    public AnchorPane scenarioRoot;
    @FXML
    public TextField scenarioNameTextField;
    @FXML
    public Button removeScenarioButton;
    @FXML
    private CheckBox isMainScenarioCheckBox;
    private Scenario scenario;
    private Function<Pair<AnchorPane, ScenarioController>, Void> onRemoveClicked;

    @Inject
    public ScenarioController(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public void load(Scenario scenario) {
        this.scenario = scenario;
        scenarioNameTextField.setText(scenario.getScenarioName());
        isMainScenarioCheckBox.setSelected(scenario.isMainScenario());
        scenarioRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        // if scenario is main, checkbox and removeButton should be disabled
        if (scenario.isMainScenario()) {
            isMainScenarioCheckBox.setDisable(true);
            removeScenarioButton.setDisable(true);
        }

        // if the checkBox is checked, it should be blocked from unchecking, because there must be one main scenario
        isMainScenarioCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isMainScenarioCheckBox.setDisable(newValue);
            removeScenarioButton.setDisable(newValue);
            scenario.setIsMainScenario(newValue);
        });

        loggerService.logInfo("Scenario (" + scenario.getScenarioName() + ") loaded to ScenarioController");
    }

    public CheckBox getIsMainScenarioCheckBox() {
        return isMainScenarioCheckBox;
    }

    public Scenario getScenario() {
        return scenario;
    }

    @FXML
    public void onRemoveScenarioButtonClicked() {
        if (onRemoveClicked != null) onRemoveClicked.apply(new Pair<>(scenarioRoot, this));
    }

    public void setOnRemoveClicked(Function<Pair<AnchorPane, ScenarioController>, Void> onRemoveClicked) {
        this.onRemoveClicked = onRemoveClicked;
    }

    @FXML
    private void scenarioNameChanged() {
        scenario.setScenarioName(scenarioNameTextField.getText());
    }
}
