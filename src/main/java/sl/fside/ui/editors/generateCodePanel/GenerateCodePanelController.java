package sl.fside.ui.editors.generateCodePanel;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.stage.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;

import java.io.*;
import java.util.*;

import static sl.fside.services.code_generator1.functions.GenJava.*;
import static sl.fside.services.code_generator1.functions.GenPython.*;

public class GenerateCodePanelController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;

    @FXML
    public TitledPane generateCodePanelRoot;
    @FXML
    public AnchorPane generateCodePanelAnchorPane;
    @FXML
    public Button generateCodeButton;

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
    public void generateCodeButtonClicked() {

        // kontrola czy kod może być wygenerowany
        if (scenario == null) {
            showMessage("Scenario is not selected (It should never occur)", Alert.AlertType.WARNING);
            return;
        }
        if (scenario.getPatternExpressionAfterProcessingNesting() == null) {
            showMessage("No PatternExpression defined!", Alert.AlertType.WARNING);
            return;
        }
        try {
            String javaCode =
                    genJava(scenario.getPatternExpressionAfterProcessingNesting(), UUID.randomUUID().toString());
            String pythonCode =
                    genPython(scenario.getPatternExpressionAfterProcessingNesting(), UUID.randomUUID().toString());
            showGeneratedCode(javaCode, pythonCode);
            loggerService.logInfo("Code generated - (scenarioId=" + scenario.getId() + ")");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showMessage(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Formal Specification IDE");
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showGeneratedCode(String javaCode, String pythonCode) {
        var stage = new Stage();
        final var loader = new FXMLLoader(GeneratedCodeController.class.getResource("GeneratedCode.fxml"));
        final Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Scene scene = new Scene(root, 800, 500);

        // Make the stage modal (it disables clicking other windows)
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.setTitle("Formal Specification IDE - Generated code");

        stage.show();

        final GeneratedCodeController controller = loader.getController();
        controller.setCode(javaCode, pythonCode);

        loggerService.logInfo("GeneratedCode window opened");
    }
}
