package sl.fside.ui.editors.resultsPanel;

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

public class ResultsPanelController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;
    @FXML
    public TitledPane resultsPanelRoot;
    @FXML
    public AnchorPane resultsPanelAnchorPane;
    @FXML
    public TextArea patternExpressionTextArea;
    @FXML
    public TextArea folTextArea;
    @FXML
    public TextArea ltlTextArea;

    private Scenario scenario;

    @Inject
    public ResultsPanelController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory,
                                  LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {

        resultsPanelRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        resultsPanelAnchorPane.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        updateResultsPanel();
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
        updateResultsPanel();
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateResultsPanel();
    }

    private void updateResultsPanel() {
        // setting disable property of the actionEditorRoot TitledPane based on the value of the scenario variable
        resultsPanelRoot.setDisable(scenario == null);
    }
}
