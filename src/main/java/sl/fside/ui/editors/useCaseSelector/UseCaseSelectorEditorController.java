package sl.fside.ui.editors.useCaseSelector;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.actionEditor.*;
import sl.fside.ui.editors.scenarioSelector.*;
import sl.fside.ui.editors.useCaseSelector.controls.*;

import java.util.*;

public class UseCaseSelectorEditorController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    @FXML
    public TitledPane useCaseSelectorEditorRoot;
    @FXML
    public AnchorPane useCaseSelectorEditorAnchorPane;
    private ScenarioSelectorEditorController scenarioSelectorEditorController;
    private ActionEditorController actionEditorController;
    @FXML
    private ListView<AnchorPane> useCasesList;
    @FXML
    private Button addOptionalUseCaseButton;
    //    @FXML
//    private ComboBox<UseCaseDiagramPresenter> useCaseDiagramComboBox;
    @FXML
    private Label currentlySelectedUseCaseLabel;
    //    private final EventAggregatorService eventAggregatorService;
    private Project project;

    private UseCaseDiagram useCaseDiagram;

//    public UseCaseSelectorEditor(){
//        modelFactory = null;
//    }

    @Inject
    public UseCaseSelectorEditorController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory
//                                           EventAggregatorService eventAggregatorService
    ) {
//        super();
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
//        this.eventAggregatorService = eventAggregatorService;

//        subscribeToEvents();

//        var fxmlLoader = new FXMLLoader(getClass().getResource(
//                "UseCaseSelectorEditor.fxml"));
//        fxmlLoader.setController(this);
//
//        try {
//            var result = fxmlLoader.<AnchorPane>load();
//            this.getChildren().add(result);
//        } catch (IOException exception) {
//            throw new RuntimeException(exception);
//        }

        // Event Handling
        // addOptionalUseCaseButton.setOnAction(actionEvent -> addUseCaseButtonClicked()); // TODO Event should be moved to IEventAggregator

    }

//    private void subscribeToEvents() {
//        eventAggregatorService.subscribe(ProjectLoadedEvent.class, event -> load(event.getProject()));
//    }

    public void initialize() {
        useCaseSelectorEditorRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        useCaseSelectorEditorAnchorPane.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        useCasesList.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        addOptionalUseCaseButton.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        currentlySelectedUseCaseLabel.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    public void addUseCaseButtonClicked() {
        if (useCaseDiagram != null) {
            var newUseCase = modelFactory.createUseCase(useCaseDiagram, UUID.randomUUID(), "New use case", false);
//            var uiElementPair = uiElementFactory.CreateUseCase(newUseCase);
//            uiElementPair.getValue().setOnRemoveClicked(this::removeUseCase);
//            useCasesList.getItems().add(uiElementPair.getKey());
        }
    }

//    @Override
//    public void load(Project project) {
//        if (object instanceof Project project) {
//            this.project = project;
//            useCaseDiagramComboBox.getItems().clear();
//            useCaseDiagramComboBox.getItems().addAll(project.getUseCaseDiagramList().stream().map(UseCaseDiagramPresenter::new).toList());
//        }
//        else
//            throw new UnsupportedOperationException();
//    }

//    @Override
//    public void unload() {
//        // TODO
//    }

    //    @FXML
    public void setUseCaseDiagramSelection(UseCaseDiagram useCaseDiagram,
                                           ScenarioSelectorEditorController scenarioSelectorEditorController,
                                           ActionEditorController actionEditorController) {
        this.useCaseDiagram = useCaseDiagram;
        this.scenarioSelectorEditorController = scenarioSelectorEditorController;
        this.actionEditorController = actionEditorController;

        useCasesList.getItems().clear();
        var useCasePairs = useCaseDiagram.getUseCaseList().stream().map(uiElementsFactory::createUseCase).toList();
        useCasePairs.stream().map(Pair::getValue)
                .forEach(useCaseController -> useCaseController.setOnRemoveClicked(this::removeUseCase));
        useCasesList.getItems().addAll(useCasePairs.stream().map(Pair::getKey).toList());

        // capturing change of the selected UseCase
        useCasesList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {

                // A new item has been selected
                // wyciągnij UseCase z zaznaczonego Pane (trzeba go ustawić w panelu ze scenariuszami)
                AnchorPane selectedItem = useCasesList.getSelectionModel().getSelectedItem();
                UseCaseController useCaseController =
                        useCasePairs.stream().filter(ucp -> ucp.getKey().equals(selectedItem)).findFirst().orElseThrow()
                                .getValue();
                UseCase useCase = useCaseController.getUseCase();

                // ustawia kontrolny tekst w panelu z UseCase'ami
                currentlySelectedUseCaseLabel.setText("Selected UC name: " + useCase.getUseCaseName());

                // Set selected UseCase to scenarioSelectorPanel
                scenarioSelectorEditorController.setUseCaseSelection(useCase, actionEditorController);

                // set checkbox in UseCasesPanel to selected UseCase
                useCasePairs.forEach(ucp -> ucp.getValue().setIsSelectedCheckBox(false));
                useCaseController.setIsSelectedCheckBox(true);

            } else {
                // No item is selected
                System.out.println("No item is selected.");
                System.out.println("Tu nie powinien się nigdy znaleźć");
            }
        });
    }

    private Void removeUseCase(AnchorPane pane) {
        useCasesList.getItems().remove(pane);
        return null;
    }

//    private record UseCaseDiagramPresenter(UseCaseDiagram useCaseDiagram) {
//        @Override
//        public String toString() {
//            return useCaseDiagram.getId().toString();
//        }
//    }

}
