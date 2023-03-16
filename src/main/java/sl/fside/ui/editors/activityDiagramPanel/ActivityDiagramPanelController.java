package sl.fside.ui.editors.activityDiagramPanel;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.activityDiagramEditor.*;

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

        stage.setScene(scene);
        stage.setTitle("Activity Diagram Editor");

        stage.show();

        final ActivityDiagramEditorController controller = loader.getController();
        controller.panToCenter();
    }
}
