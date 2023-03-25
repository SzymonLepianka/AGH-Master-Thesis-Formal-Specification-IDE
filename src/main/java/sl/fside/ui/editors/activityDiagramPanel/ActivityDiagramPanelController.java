package sl.fside.ui.editors.activityDiagramPanel;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.stage.Stage;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.activityDiagramEditor.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;

import java.io.*;
import java.util.*;

public class ActivityDiagramPanelController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;

    @FXML
    public TitledPane activityDiagramPanelRoot;
    @FXML
    public AnchorPane activityDiagramPanelAnchorPane;
    @FXML
    public Button openActivityDiagramEditorButton;


    private Scenario scenario;

    @Inject
    public ActivityDiagramPanelController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory,
                                          LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {

        activityDiagramPanelRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        activityDiagramPanelAnchorPane.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        openActivityDiagramEditorButton.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        updateActivityDiagramPanel();
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
        updateActivityDiagramPanel();
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateActivityDiagramPanel();
    }

    private void updateActivityDiagramPanel() {
        // setting disable property of the actionEditorRoot TitledPane based on the value of the scenario variable
        activityDiagramPanelRoot.setDisable(scenario == null);
    }

    public void openActivityDiagramEditorButtonClicked() {

        // kontrola czy diagram aktywności może być uruchomiony
        if (scenario == null) {
            showWarningMessage("Scenario is not selected (It should never occur)");
            return;
        }
        if (scenario.getAtomicActivities().isEmpty()) {
            showWarningMessage("No atomic activities defined!");
            return;
        }

        // ustawia atomiczne aktywności dla edytora diagramu aktywności
        NodesManager.getInstance().setCurrentAtomicActivities(
                scenario.getAtomicActivities().stream().map(AtomicActivity::getContent).toList());

        var stage = new Stage();
        final var loader =
                new FXMLLoader(ActivityDiagramEditorController.class.getResource("ActivityDiagramEditor.fxml"));
        final Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Scene scene = new Scene(root, 830, 630);

        scene.getStylesheets().add(ActivityDiagramEditorController.class.getResource("demo.css").toExternalForm());
        Font.loadFont(ActivityDiagramEditorController.class.getResource("fontawesome.ttf").toExternalForm(), 12);

        // Make the stage modal (it disables clicking other windows)
        stage.initModality(Modality.APPLICATION_MODAL);

        // show confirmation dialog on closing window
        stage.setOnCloseRequest(event -> {
            // Create a new confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Exit");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("Any unsaved changes will be lost.");

            // Show the dialog and wait for a response
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // If the user clicked OK, close the window
                stage.close();
            } else {
                // If the user clicked Cancel or closed the dialog, consume the event
                event.consume();
            }
        });

        // save generated results in scenario
        stage.setOnHidden(event -> {
            if (NodesManager.getInstance().wasSpecificationGenerated()) {
                scenario.setPatternExpression(NodesManager.getInstance().getPatternExpression());
                scenario.setFolLogicalSpecification(NodesManager.getInstance().getFolLogicalSpecification());
                scenario.setLtlLogicalSpecification(NodesManager.getInstance().getLtlLogicalSpecification());
            }
        });

        stage.setScene(scene);
        stage.setTitle("Formal Specification IDE - Activity Diagram Editor");

        stage.show();

        final ActivityDiagramEditorController controller = loader.getController();
        controller.panToCenter();
    }

    private void showWarningMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Formal Specification IDE");
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
