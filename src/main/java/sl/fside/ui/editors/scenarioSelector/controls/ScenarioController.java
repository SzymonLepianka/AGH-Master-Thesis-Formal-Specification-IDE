package sl.fside.ui.editors.scenarioSelector.controls;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import sl.fside.model.Scenario;

import java.util.Random;

public class ScenarioController {
    public CheckBox isMainScenarioCheckBox;
    public Label scenarioNameLabel;
    @FXML
    public AnchorPane scenarioRoot;
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
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }
}
