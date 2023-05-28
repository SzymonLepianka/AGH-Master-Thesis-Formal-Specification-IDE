package sl.fside.ui.editors.useCaseSelector;

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
import sl.fside.ui.editors.activityDiagramEditor.managers.*;
import sl.fside.ui.editors.activityDiagramPanel.*;
import sl.fside.ui.editors.generateCodePanel.*;
import sl.fside.ui.editors.requirementEditor.*;
import sl.fside.ui.editors.resultsPanel.*;
import sl.fside.ui.editors.scenarioContentEditor.*;
import sl.fside.ui.editors.scenarioSelector.*;
import sl.fside.ui.editors.useCaseSelector.controls.*;
import sl.fside.ui.editors.verificationEditor.*;

import java.util.*;

public class UseCaseSelectorEditorController {

    private final LoggerService loggerService;
    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final List<Pair<AnchorPane, UseCaseController>> uiElementUseCasePairs = new ArrayList<>();
    @FXML
    public TitledPane useCaseSelectorEditorRoot;
    @FXML
    public AnchorPane useCaseSelectorEditorAnchorPane;
    @FXML
    public Button showRelationsInUseCaseDiagramButton;
    private UseCase currentlySelectedUseCase = null;
    private UseCaseDiagram useCaseDiagram;
    @FXML
    private ListView<AnchorPane> useCasesList;
    @FXML
    private Button addOptionalUseCaseButton;
    @FXML
    private Label currentlySelectedUseCaseLabel;

    private ScenarioSelectorEditorController scenarioSelectorEditorController;
    private ScenarioContentEditorController scenarioContentEditorController;
    private ActivityDiagramPanelController activityDiagramPanelController;
    private ResultsPanelController resultsPanelController;
    private GenerateCodePanelController generateCodePanelController;
    private RequirementEditorController requirementEditorController;
    private VerificationEditorController verificationEditorController;

    @Inject
    public UseCaseSelectorEditorController(IModelFactory modelFactory, LoggerService loggerService,
                                           UIElementsFactory uiElementsFactory) {
        this.modelFactory = modelFactory;
        this.loggerService = loggerService;
        this.uiElementsFactory = uiElementsFactory;
    }

    public void initialize() {
        useCaseSelectorEditorRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        updateUseCaseSelectorEditor();
    }

    @FXML
    public void addUseCaseButtonClicked() {
        if (useCaseDiagram != null) {
            // creating new UseCase
            UseCase newUseCase = modelFactory.createUseCase(useCaseDiagram, UUID.randomUUID(), "New use case", false);
            Pair<AnchorPane, UseCaseController> uiElementPair = uiElementsFactory.createUseCase(newUseCase);
            uiElementUseCasePairs.add(uiElementPair);

            // usuwanie UseCase
            uiElementPair.getValue().setOnRemoveClicked(this::removeUseCase);

            // dodaje nowy UseCase do obecnych
            useCasesList.getItems().add(uiElementPair.getKey());

            loggerService.logInfo("New UseCase added - " + newUseCase.getId());
        } else {
            loggerService.logError("Nigdy nie powinien się tu znaleźć (addUseCaseButtonClicked)");
        }
    }

    public void setUseCaseDiagramSelection(UseCaseDiagram useCaseDiagram,
                                           ScenarioSelectorEditorController scenarioSelectorEditorController,
                                           ScenarioContentEditorController scenarioContentEditorController,
                                           ActivityDiagramPanelController activityDiagramPanelController,
                                           ResultsPanelController resultsPanelController,
                                           GenerateCodePanelController generateCodePanelController,
                                           RequirementEditorController requirementEditorController,
                                           VerificationEditorController verificationEditorController) {
        this.useCaseDiagram = useCaseDiagram;
        this.scenarioSelectorEditorController = scenarioSelectorEditorController;
        this.scenarioContentEditorController = scenarioContentEditorController;
        this.activityDiagramPanelController = activityDiagramPanelController;
        this.resultsPanelController = resultsPanelController;
        this.generateCodePanelController = generateCodePanelController;
        this.requirementEditorController = requirementEditorController;
        this.verificationEditorController = verificationEditorController;
        useCasesList.getItems().clear();
        updateUseCaseSelectorEditor();

        // adds UseCaseDiagram to NodesManager
        NodesManager.getInstance().setCurrentUseCaseDiagram(useCaseDiagram);

        // create new useCases
        var useCasePairs = useCaseDiagram.getUseCaseList().stream().map(uiElementsFactory::createUseCase).toList();
        uiElementUseCasePairs.clear();
        uiElementUseCasePairs.addAll(useCasePairs);

        // usuwanie UseCase
        useCasePairs.forEach(pair -> pair.getValue().setOnRemoveClicked(this::removeUseCase));

        // usuwa ewentualne poprzednie zaznaczenie UseCase
        scenarioSelectorEditorController.removeUseCaseSelection();
        scenarioContentEditorController.removeScenarioSelection();
        activityDiagramPanelController.removeScenarioSelection();
        resultsPanelController.removeScenarioSelection();
        generateCodePanelController.removeScenarioSelection();
        requirementEditorController.removeScenarioSelection();
        verificationEditorController.removeScenarioSelection();

        // add new useCases to list
        useCasesList.getItems().addAll(useCasePairs.stream().map(Pair::getKey).toList());

        // capturing change of the selected UseCase
        useCasesList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {

                // A new item has been selected
                // wyciągnij UseCase z zaznaczonego Pane (trzeba go ustawić w panelu ze scenariuszami)
                AnchorPane selectedItem = useCasesList.getSelectionModel().getSelectedItem();
                UseCaseController useCaseController =
                        uiElementUseCasePairs.stream().filter(ucp -> ucp.getKey().equals(selectedItem)).findFirst()
                                .orElseThrow().getValue();
                UseCase useCase = useCaseController.getUseCase();

                // ustawia kontrolny tekst w panelu z UseCase'ami
                currentlySelectedUseCaseLabel.setText("Selected UC name: " + useCase.getUseCaseName());
                currentlySelectedUseCase = useCase;

                // Set selected UseCase to scenarioSelectorPanel
                scenarioSelectorEditorController.setUseCaseSelection(useCase, scenarioContentEditorController,
                        activityDiagramPanelController, resultsPanelController, generateCodePanelController,
                        requirementEditorController, verificationEditorController);

                // set checkbox in UseCasesPanel to selected UseCase
                uiElementUseCasePairs.forEach(ucp -> ucp.getValue().setIsSelectedCheckBox(false));
                useCaseController.setIsSelectedCheckBox(true);

            } else {
                // No item is selected
                loggerService.logWarning("No item is selected (setUseCaseDiagramSelection)");

                // ustawia kontrolny tekst w panelu z UseCase'ami
                currentlySelectedUseCaseLabel.setText("No UseCase is selected");
                currentlySelectedUseCase = null;
            }
        });

        loggerService.logInfo("UseCaseDiagram set to UseCaseSelectorEditor - " + useCaseDiagram.getUseCaseDiagramId());
    }

    private Void removeUseCase(Pair<AnchorPane, UseCaseController> pair) {
        useCasesList.getItems().remove(pair.getKey());
        useCaseDiagram.removeUseCase(pair.getValue().getUseCase());
        uiElementUseCasePairs.remove(pair);
        loggerService.logInfo("UseCase removed - " + pair.getValue().getUseCase().getId());
        return null;
    }

    private void updateUseCaseSelectorEditor() {
        // setting disable property of the useCaseSelectorEditorRoot TitledPane based on the value of the useCaseDiagram variable
        useCaseSelectorEditorRoot.setDisable(useCaseDiagram == null);
    }

    @FXML
    public void showRelationsInUseCaseDiagramButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Relations in UseCaseDiagram");
        alert.setHeaderText(null);
        alert.setContentText(useCaseDiagram.showRelations());
        alert.showAndWait();
    }

    public UseCase getCurrentlySelectedUseCase() {
        return currentlySelectedUseCase;
    }
}
