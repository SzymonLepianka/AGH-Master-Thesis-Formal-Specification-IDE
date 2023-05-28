package sl.fside.ui.editors.scenarioSelector;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.activityDiagramPanel.*;
import sl.fside.ui.editors.generateCodePanel.*;
import sl.fside.ui.editors.requirementEditor.*;
import sl.fside.ui.editors.resultsPanel.*;
import sl.fside.ui.editors.scenarioContentEditor.*;
import sl.fside.ui.editors.scenarioSelector.controls.*;
import sl.fside.ui.editors.verificationEditor.*;

import java.util.*;

public class ScenarioSelectorEditorController {
    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;
    private final List<Pair<AnchorPane, ScenarioController>> uiElementScenarioPairs = new ArrayList<>();
    @FXML
    public TitledPane scenarioSelectorEditorRoot;
    @FXML
    public AnchorPane scenarioSelectorEditorAnchorPane;
    @FXML
    public ListView<AnchorPane> scenarioList;
    @FXML
    public Button addScenarioButton;
    @FXML
    private Label currentlySelectedScenarioLabel;
    private UseCase useCase;
    private ScenarioContentEditorController scenarioContentEditorController;
    private ActivityDiagramPanelController activityDiagramPanelController;
    private ResultsPanelController resultsPanelController;
    private GenerateCodePanelController generateCodePanelController;
    private RequirementEditorController requirementEditorController;
    private VerificationEditorController verificationEditorController;

    @Inject
    public ScenarioSelectorEditorController(IModelFactory modelFactory, LoggerService loggerService,
                                            UIElementsFactory uiElementsFactory) {
        this.modelFactory = modelFactory;
        this.loggerService = loggerService;
        this.uiElementsFactory = uiElementsFactory;
    }

    public void initialize() {
        scenarioSelectorEditorRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        scenarioSelectorEditorAnchorPane.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        scenarioList.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        addScenarioButton.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        currentlySelectedScenarioLabel.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        updateScenarioSelectorEditor();
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    @FXML
    public void addScenarioButtonClicked() {
        if (useCase != null) {

            // creating new scenario
            Scenario newScenario = modelFactory.createScenario(useCase, UUID.randomUUID(), false);
            Pair<AnchorPane, ScenarioController> uiElementPair = uiElementsFactory.createScenario(newScenario);
            uiElementScenarioPairs.add(uiElementPair);

            // add listener to scenario's checkbox, to define main scenario
            addListenerToUiElementScenarioPair(uiElementPair);

            // usuwanie Scenario
            uiElementPair.getValue().setOnRemoveClicked(this::removeScenario);

            // dodaje nowy scenariusz do obecnych
            scenarioList.getItems().add(uiElementPair.getKey());

            loggerService.logInfo("New scenario added - " + newScenario.getId());
        } else {
            loggerService.logError("Nigdy nie powinien się tu znaleźć (addScenarioButtonClicked)");
        }
    }

    private Void removeScenario(Pair<AnchorPane, ScenarioController> pair) {
        scenarioList.getItems().remove(pair.getKey());
        useCase.removeScenario(pair.getValue().getScenario());
        uiElementScenarioPairs.remove(pair);
        loggerService.logInfo("Scenario removed - " + pair.getValue().getScenario().getId());
        return null;
    }

    private void addListenerToUiElementScenarioPair(Pair<AnchorPane, ScenarioController> uiElementPair) {
        uiElementPair.getValue().getIsMainScenarioCheckBox().selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue) { // I want to trigger listener only if this is clicked one
                        handleCheckboxSelected(uiElementPair.getValue());
                    }
                });
    }

    private void handleCheckboxSelected(ScenarioController mainScenarioController) {
        for (var scenarioPair : uiElementScenarioPairs) {
            ScenarioController sc = scenarioPair.getValue();

            // odznacz poprzedni scenariusz główny
            if (sc.getIsMainScenarioCheckBox().isSelected() && !sc.equals(mainScenarioController)) {
                sc.getIsMainScenarioCheckBox().setSelected(false);
            }
        }
    }

    public void setUseCaseSelection(UseCase useCase, ScenarioContentEditorController scenarioContentEditorController,
                                    ActivityDiagramPanelController activityDiagramPanelController,
                                    ResultsPanelController resultsPanelController,
                                    GenerateCodePanelController generateCodePanelController,
                                    RequirementEditorController requirementEditorController,
                                    VerificationEditorController verificationEditorController) {
        this.useCase = useCase;
        this.scenarioContentEditorController = scenarioContentEditorController;
        this.activityDiagramPanelController = activityDiagramPanelController;
        this.resultsPanelController = resultsPanelController;
        this.generateCodePanelController = generateCodePanelController;
        this.requirementEditorController = requirementEditorController;
        this.verificationEditorController = verificationEditorController;
        updateScenarioSelectorEditor();
        scenarioList.getItems().clear();

        // create new scenarios
        var scenarioPairs = useCase.getScenarioList().stream().map(uiElementsFactory::createScenario).toList();
        uiElementScenarioPairs.clear();
        uiElementScenarioPairs.addAll(scenarioPairs);

        // add listener to scenario's checkboxes, to define main scenario
        scenarioPairs.forEach(this::addListenerToUiElementScenarioPair);

        // ustawia kontrolny tekst w panelu z Scenario'ami - bez zaznaczeń
        currentlySelectedScenarioLabel.setText("No Scenario is selected");

        // ustawia tytuł panelu z Scenario'ami
        scenarioSelectorEditorRoot.setText("Scenarios manager for UseCase (" +
                (useCase.getUseCaseName().length() > 25 ? useCase.getUseCaseName().substring(0, 25) + "..." :
                        useCase.getUseCaseName()) + ")");

        // usuwa ewentualne poprzednie zaznaczenie Scenario
        scenarioContentEditorController.removeScenarioSelection();
        activityDiagramPanelController.removeScenarioSelection();
        resultsPanelController.removeScenarioSelection();
        generateCodePanelController.removeScenarioSelection();
        requirementEditorController.removeScenarioSelection();
        verificationEditorController.removeScenarioSelection();

        // usuwanie Scenario
        scenarioPairs.forEach(pair -> pair.getValue().setOnRemoveClicked(this::removeScenario));

        // add new scenarios to list
        scenarioList.getItems().addAll(scenarioPairs.stream().map(Pair::getKey).toList());

        // capturing change of the selected Scenario
        scenarioList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {

                // A new item has been selected
                // wyciągnij Scenario z zaznaczonego Pane (trzeba go ustawić w panelu z akcjami)
                AnchorPane selectedItem = scenarioList.getSelectionModel().getSelectedItem();
                ScenarioController scenarioController =
                        uiElementScenarioPairs.stream().filter(sp -> sp.getKey().equals(selectedItem)).findFirst()
                                .orElseThrow().getValue();
                Scenario scenario = scenarioController.getScenario();

                // ustawia kontrolny tekst w panelu z Scenario'ami
                currentlySelectedScenarioLabel.setText("Selected Scenario name: " + scenario.getScenarioName());

                // Set selected Scenario to scenarioContentEditorPanel and activityDiagramPanel
                scenarioContentEditorController.setScenarioSelection(scenario);
                activityDiagramPanelController.setScenarioSelection(scenario, resultsPanelController);
                resultsPanelController.setScenarioSelection(scenario);
                generateCodePanelController.setScenarioSelection(scenario);
                requirementEditorController.setScenarioSelection(scenario);
                verificationEditorController.setScenarioSelection(scenario);

            } else {
                // No item is selected
                System.out.println("No item (Scenario) is selected.");
            }
        });

        loggerService.logInfo("UseCase set to ScenarioSelectorEditor - " + useCase.getId());
    }

    public void removeUseCaseSelection() {
        this.useCase = null;
        updateScenarioSelectorEditor();
        scenarioList.getItems().clear();
        uiElementScenarioPairs.clear();
        scenarioSelectorEditorRoot.setText("Scenarios manager for UseCase (no UseCase selected)");
        currentlySelectedScenarioLabel.setText("No Scenario is selected");
    }

    private void updateScenarioSelectorEditor() {
        // setting disable property of the scenarioSelectorEditorRoot TitledPane based on the value of the useCase variable
        scenarioSelectorEditorRoot.setDisable(useCase == null);
    }
}
