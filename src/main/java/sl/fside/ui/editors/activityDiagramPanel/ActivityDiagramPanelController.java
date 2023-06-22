package sl.fside.ui.editors.activityDiagramPanel;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.activityDiagramEditor.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;
import sl.fside.ui.editors.resultsPanel.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class ActivityDiagramPanelController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;
    private final MainWindowController mainWindowController;

    @FXML
    public TitledPane activityDiagramPanelRoot;
    @FXML
    public AnchorPane activityDiagramPanelAnchorPane;
    @FXML
    public Button openActivityDiagramEditorButton;

    private Scenario scenario;
    private ResultsPanelController resultsPanelController;

    @Inject
    public ActivityDiagramPanelController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory,
                                          LoggerService loggerService, MainWindowController mainWindowController) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
        this.mainWindowController = mainWindowController;
    }

    public void initialize() {

        activityDiagramPanelRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

//        activityDiagramPanelAnchorPane.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        openActivityDiagramEditorButton.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        updateActivityDiagramPanel();
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    public void setScenarioSelection(Scenario scenario, ResultsPanelController resultsPanelController) {
        this.scenario = scenario;
        this.resultsPanelController = resultsPanelController;
        updateActivityDiagramPanel();
        loggerService.logInfo("Scenario set to ActivityDiagramPanel - " + scenario.getId());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateActivityDiagramPanel();
    }

    private void updateActivityDiagramPanel() {
        // setting disable property of the activityDiagramPanelRoot TitledPane based on the value of the scenario variable
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

        // kontrola czy wśród atomicznych aktywności są inkludowane UseCase
        if (scenario.isMainScenario()) { // TODO tylko dla głównego scenariusza?
            UseCaseDiagram useCaseDiagram = mainWindowController.getCurrentProject().getUseCaseDiagram();
            UseCase useCase = mainWindowController.useCaseSelectorEditorController.getCurrentlySelectedUseCase();
            List<Relation> allRelations = useCaseDiagram.getRelations();
            List<AtomicActivity> atomicActivities = scenario.getAtomicActivities();
            List<Relation> relations = allRelations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.INCLUDE && r.getFromId().equals(useCase.getId()))
                    .toList();
            for (Relation r : relations) {
                String targetUseCaseName = useCaseDiagram.getUseCaseNameFromId(r.getToId());
                String obligatoryAtomicActivity = "<<include>>" + targetUseCaseName;
                if (atomicActivities.stream().noneMatch(aa -> aa.getContent().equals(obligatoryAtomicActivity))) {
                    showWarningMessage(
                            "Obligatory atomic activity was not defined!\n'" + obligatoryAtomicActivity + "'");
                    return;
                }
            }
        }

        // kontrola czy wśród atomicznych aktywności są extendowane UseCase
        if (scenario.isMainScenario()) { // TODO tylko dla głównego scenariusza?
            UseCaseDiagram useCaseDiagram = mainWindowController.getCurrentProject().getUseCaseDiagram();
            UseCase useCase = mainWindowController.useCaseSelectorEditorController.getCurrentlySelectedUseCase();
            List<Relation> allRelations = useCaseDiagram.getRelations();
            List<AtomicActivity> atomicActivities = scenario.getAtomicActivities();
            List<Relation> extendRelations = allRelations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.EXTEND && r.getToId().equals(useCase.getId()))
                    .toList();
            for (Relation r : extendRelations) {
                String targetUseCaseName = useCaseDiagram.getUseCaseNameFromId(r.getFromId());
                String obligatoryRelationName = "<<extend>>";
                if (atomicActivities.stream().noneMatch(aa -> aa.getContent().startsWith(obligatoryRelationName) &&
                        aa.getContent().endsWith(targetUseCaseName))) {
                    showWarningMessage(
                            "Obligatory atomic activity was not defined!\n'" + obligatoryRelationName + "CONDITION?" +
                                    targetUseCaseName + "'");
                    return;
                }
            }
        }

        // kontrola synchronizacji między generalnym i specyficznym UseCase, w przypadku próby edycji GeneralPatternExpression
        if (scenario.isMainScenario()) { // TODO tylko dla głównego scenariusza?
            UseCaseDiagram useCaseDiagram = mainWindowController.getCurrentProject().getUseCaseDiagram();
            UseCase generalUseCase = mainWindowController.useCaseSelectorEditorController.getCurrentlySelectedUseCase();
            List<Relation> allRelations = useCaseDiagram.getRelations();
            List<Relation> genRelations = allRelations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.GENERALIZATION &&
                            r.getToId().equals(generalUseCase.getId())).toList();
            for (Relation r : genRelations) {
                UseCase specificUseCase = useCaseDiagram.getUseCaseFromId(r.getFromId());
                PatternExpression specificPatternExpression = specificUseCase.getMainScenario().getPatternExpression();
                PatternExpression generalPatternExpression = generalUseCase.getMainScenario().getPatternExpression();
                if (specificPatternExpression == null && generalPatternExpression != null) {
                    showWarningMessage("SpecificPatternExpression " + specificUseCase.getUseCaseName() +
                            " nie jest ustawiony, pomimo istnienia GeneralPatternExpression " +
                            generalUseCase.getUseCaseName() + "! Taka sytuacja nie powinna nigdy wystąpić");
                    return;
                }
                if (specificPatternExpression != null && generalPatternExpression == null) {
                    showWarningMessage("SpecificPatternExpression " + specificUseCase.getUseCaseName() +
                            " został stworzony mimo że GeneralPatternExpression " + generalUseCase.getUseCaseName() +
                            " nie istnieje! Taka sytuacja nie powinna nigdy wystąpić");
                    return;
                }

                if (generalPatternExpression == null) {
                    // OK - pierwsze tworzenie GeneralActivityDiagram
                    continue;
                }

                if (!specificPatternExpression.toString().equals(generalPatternExpression.toString())) {
                    // Create a new confirmation dialog
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm general UseCase edit");
                    alert.setHeaderText("Are you sure you want to edit ActivityDiagram of GeneralUseCase?");
                    alert.setContentText("ActivityDiagram of SpecificUseCase (" + specificUseCase.getUseCaseName() +
                            ") was previously edited! When you edit ActivityDiagram of GeneralUseCase (" +
                            generalUseCase.getUseCaseName() +
                            ") you will lose changes in ActivityDiagram of SpecificUseCase!");

                    // Show the dialog and wait for a response
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        // If user didn't click OK, don't open ActivityDiagramEditor
                        return;
                    }
                }
            }
        }

        // kontrola czy istnieje GeneralPatternExpression w przypadku próby edycji SpecificPatternExpression
        if (scenario.isMainScenario()) { // TODO tylko dla głównego scenariusza?
            UseCaseDiagram useCaseDiagram = mainWindowController.getCurrentProject().getUseCaseDiagram();
            UseCase specificUseCase =
                    mainWindowController.useCaseSelectorEditorController.getCurrentlySelectedUseCase();
            List<Relation> allRelations = useCaseDiagram.getRelations();
            List<Relation> genRelations = allRelations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.GENERALIZATION &&
                            r.getFromId().equals(specificUseCase.getId())).toList();
            for (Relation r : genRelations) {
                UseCase generalUseCase = useCaseDiagram.getUseCaseFromId(r.getToId());
                PatternExpression specificPatternExpression = specificUseCase.getMainScenario().getPatternExpression();
                PatternExpression generalPatternExpression = generalUseCase.getMainScenario().getPatternExpression();
                if (specificPatternExpression == null && generalPatternExpression != null) {
                    showWarningMessage("SpecificPatternExpression (" + specificUseCase.getUseCaseName() +
                            ") nie jest ustawiony, pomimo istnienia GeneralPatternExpression (" +
                            generalUseCase.getUseCaseName() + ")! Taka sytuacja nie powinna nigdy wystąpić");
                    return;
                }
                if (specificPatternExpression != null && generalPatternExpression == null) {
                    showWarningMessage("SpecificPatternExpression (" + specificUseCase.getUseCaseName() +
                            ") został stworzony mimo że GeneralPatternExpression (" + generalUseCase.getUseCaseName() +
                            ") nie istnieje! Taka sytuacja nie powinna nigdy wystąpić");
                    return;
                }

                if (specificPatternExpression == null) {
                    showWarningMessage("SpecificPatternExpression (" + specificUseCase.getUseCaseName() +
                            ") musi zostać edytowany po stworzeniu GeneralPatternExpression (" +
                            generalUseCase.getUseCaseName() + ")!");
                    return;
                }
            }
        }

        // ustawia atomiczne aktywności dla edytora diagramu aktywności
        List<AtomicActivity> additionalAtomicActivities = checkForAdditionalAtomicActivitiesInGeneralUseCase();
        if (additionalAtomicActivities == null) {
            showWarningMessage("Error: additionalAtomicActivities is null! To nigdy nie powinno wystąpić!");
            return;
        }
        List<AtomicActivity> allAtomicActivities =
                Stream.concat(scenario.getAtomicActivities().stream(), additionalAtomicActivities.stream()).toList();
        NodesManager.getInstance()
                .setCurrentAtomicActivities(allAtomicActivities.stream().map(AtomicActivity::getContent).toList());

        // ustawia UseCase dla edytora diagramu aktywności (potrzebny, aby przetworzyć zagnieżdżenia)
        NodesManager.getInstance()
                .setCurrentUseCase(mainWindowController.useCaseSelectorEditorController.getCurrentlySelectedUseCase());

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
                NodesManager.getInstance().setWasSpecificationGenerated(false);
                stage.close();
            } else {
                // If the user clicked Cancel or closed the dialog, consume the event
                event.consume();
            }
        });

        // save generated results in scenario
        stage.setOnHidden(event -> {
            if (NodesManager.getInstance().wasSpecificationGenerated()) {
                replaceSpecificPeIfGeneralPeChanged(NodesManager.getInstance().getPatternExpression(),
                        NodesManager.getInstance().getFolLogicalSpecification(),
                        NodesManager.getInstance().getLtlLogicalSpecification());
                scenario.setPatternExpression(NodesManager.getInstance().getPatternExpression());
                scenario.setFolLogicalSpecification(NodesManager.getInstance().getFolLogicalSpecification());
                scenario.setLtlLogicalSpecification(NodesManager.getInstance().getLtlLogicalSpecification());
                resultsPanelController.showResults();
            }
        });

        stage.setScene(scene);
        stage.setTitle("Formal Specification IDE - Activity Diagram Editor");

        stage.show();

        final ActivityDiagramEditorController controller = loader.getController();
        controller.panToCenter();

        controller.checkExistingActivityDiagram();

        loggerService.logInfo("ActivityDiagramEditor opened (scenarioId=" + scenario.getId());
    }

    private void showWarningMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Formal Specification IDE");
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void replaceSpecificPeIfGeneralPeChanged(PatternExpression newGeneralPe, List<String> newFolSpecification,
                                                     List<String> newLtlSpecification) {
        if (scenario.isMainScenario()) { // TODO tylko dla głównego scenariusza?
            UseCaseDiagram useCaseDiagram = mainWindowController.getCurrentProject().getUseCaseDiagram();
            UseCase generalUseCase = mainWindowController.useCaseSelectorEditorController.getCurrentlySelectedUseCase();
            List<Relation> allRelations = useCaseDiagram.getRelations();
            List<Relation> genRelations = allRelations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.GENERALIZATION &&
                            r.getToId().equals(generalUseCase.getId())).toList();
            for (Relation r : genRelations) {
                Scenario specificUseCaseMainScenario = useCaseDiagram.getUseCaseFromId(r.getFromId()).getMainScenario();
                PatternExpression generalPatternExpression = generalUseCase.getMainScenario().getPatternExpression();

                // przypadek, w którym pierwszy raz wygenerowano GeneralPatternExpression
                if (generalPatternExpression == null) {
                    specificUseCaseMainScenario.setPatternExpression(newGeneralPe);
                    specificUseCaseMainScenario.setFolLogicalSpecification(newFolSpecification);
                    specificUseCaseMainScenario.setLtlLogicalSpecification(newLtlSpecification);
                    continue;
                }

                // przypadek, w którym GeneralPatternExpression się zmienił
                if (!generalPatternExpression.toString().equals(newGeneralPe.toString())) {
                    specificUseCaseMainScenario.setPatternExpression(newGeneralPe);
                    specificUseCaseMainScenario.setFolLogicalSpecification(newFolSpecification);
                    specificUseCaseMainScenario.setLtlLogicalSpecification(newLtlSpecification);
                }

                // w przypadku, gdy GeneralPatternExpression się nie zmienił, nie nadpisuj SpecificPatternExpression
                // continue;
            }
        }
    }

    private List<AtomicActivity> checkForAdditionalAtomicActivitiesInGeneralUseCase() {
        if (scenario.isMainScenario()) { // TODO tylko dla głównego scenariusza?
            UseCaseDiagram useCaseDiagram = mainWindowController.getCurrentProject().getUseCaseDiagram();
            UseCase specificUseCase =
                    mainWindowController.useCaseSelectorEditorController.getCurrentlySelectedUseCase();
            List<Relation> allRelations = useCaseDiagram.getRelations();
            List<Relation> genRelations = allRelations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.GENERALIZATION &&
                            r.getFromId().equals(specificUseCase.getId())).toList();
            for (Relation r : genRelations) {
                Scenario generalUseCaseMainScenario = useCaseDiagram.getUseCaseFromId(r.getToId()).getMainScenario();
                return generalUseCaseMainScenario.getAtomicActivities();
            }
        }
        return new ArrayList<>();
    }
}
