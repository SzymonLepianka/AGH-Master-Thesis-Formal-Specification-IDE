package sl.fside.ui.editors.scenarioSelector.controls;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import sl.fside.model.*;

import java.util.*;

public class ScenarioController {

    public Label scenarioNameLabel;
    @FXML
    public AnchorPane scenarioRoot;
    @FXML
    private CheckBox isMainScenarioCheckBox;
    private Scenario scenario;

    @Inject
    public ScenarioController() {
    }

    public void load(Scenario scenario) {
        this.scenario = scenario;
        scenarioNameLabel.setText(scenario.getId().toString());
        isMainScenarioCheckBox.setSelected(scenario.isMainScenario());
        scenarioRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        // if scenario is main, checkbox should be disabled
        if (scenario.isMainScenario()) {
            isMainScenarioCheckBox.setDisable(true);
        }

        // if the checkBox is checked, it should be blocked from unchecking, because there must be one main scenario
        isMainScenarioCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isMainScenarioCheckBox.setDisable(newValue);
            scenario.setIsMainScenario(newValue);
        });
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    public CheckBox getIsMainScenarioCheckBox() {
        return isMainScenarioCheckBox;
    }

    public Scenario getScenario() {
        return scenario;
    }
}
