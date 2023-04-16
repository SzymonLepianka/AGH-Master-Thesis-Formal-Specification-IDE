package sl.fside.ui.editors.generateCodePanel;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;

import java.util.*;

public class GenerateCodePanelController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;

    @FXML
    public TitledPane generateCodePanelRoot;
    @FXML
    public AnchorPane generateCodePanelAnchorPane;
    @FXML
    public Button generateJavaButton;
    @FXML
    public Button generatePythonButton;

    private Scenario scenario;

    @Inject
    public GenerateCodePanelController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory,
                                       LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {

        generateCodePanelRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

//        activityDiagramPanelAnchorPane.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        openActivityDiagramEditorButton.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        updateGenerateCodePanel();
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateGenerateCodePanel();
        loggerService.logInfo("Scenario set to GenerateCodePanel - " + scenario.getId());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateGenerateCodePanel();
    }

    private void updateGenerateCodePanel() {
        // setting disable property of the generateCodePanelRoot TitledPane based on the value of the scenario variable
        generateCodePanelRoot.setDisable(scenario == null);
    }

    @FXML
    public void generateJavaButtonClicked() {

        // kontrola czy kod może być wygenerowany
        if (scenario == null) {
            showWarningMessage("Scenario is not selected (It should never occur)");
            return;
        }
        // TODO change to pattern exp
        if (scenario.getPatternExpressionAfterProcessingNesting() == null) {
            showWarningMessage("No PatternExpression defined!");
            return;
        }

        // TODO open popup with generated java code
    }

    @FXML
    public void generatePythonButtonClicked() {

        // kontrola czy kod może być wygenerowany
        if (scenario == null) {
            showWarningMessage("Scenario is not selected (It should never occur)");
            return;
        }
        // TODO change to pattern exp
        if (scenario.getPatternExpressionAfterProcessingNesting() == null) {
            showWarningMessage("No PatternExpression defined!");
            return;
        }

        // TODO open popup with generated python code
    }

    private void showWarningMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Formal Specification IDE");
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
