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
import sl.fside.ui.editors.activityDiagramEditor.managers.*;

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
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        updateResultsPanel();
    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateResultsPanel();
        showResults();
        loggerService.logInfo("Scenario set to ResultsPanel - " + scenario.getId());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateResultsPanel();
        removeResults();
    }

    private void updateResultsPanel() {
        // setting disable property of the resultsPanelRoot TitledPane based on the value of the scenario variable
        resultsPanelRoot.setDisable(scenario == null);
    }

    public void showResults() {
        if (scenario.getPatternExpression() != null) {
            patternExpressionTextArea.setText(scenario.getPatternExpression().getPeWithProcessedNesting());
            folTextArea.setText(scenario.getFolLogicalSpecificationString());
            ltlTextArea.setText(scenario.getLtlLogicalSpecificationString());
        } else {
            patternExpressionTextArea.setText("");
            folTextArea.setText("");
            ltlTextArea.setText("");
        }
        NodesManager.getInstance().setSpecificationFromScenario(scenario);
    }

    public Scenario getCurrentScenario() {
        return scenario;
    }

    private void removeResults() {
        patternExpressionTextArea.setText("");
        folTextArea.setText("");
        ltlTextArea.setText("");
    }
}
