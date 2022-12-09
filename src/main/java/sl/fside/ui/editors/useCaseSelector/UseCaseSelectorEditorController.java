package sl.fside.ui.editors.useCaseSelector;

import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;
import sl.fside.ui.events.*;
import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;

import java.util.*;

public class UseCaseSelectorEditorController implements IController {

    @FXML
    private ListView<AnchorPane> useCasesList;

    @FXML
    private Button addOptionalUseCaseButton;
    
    @FXML
    private Label currentlySelectedUseCaseLabel;
    
    @FXML
    private ComboBox<UseCaseDiagramPresenter> useCaseDiagramComboBox;
    
    private final IModelFactory modelFactory;
    private final IUIElementFactory uiElementFactory;
    private final EventAggregatorService eventAggregatorService;
    
    private Project project;
    
    private UseCaseDiagram useCaseDiagram;
    
//    public UseCaseSelectorEditor(){
//        modelFactory = null;
//    }
    
    @Inject
    public UseCaseSelectorEditorController(IModelFactory modelFactory, IUIElementFactory uiElementFactory, EventAggregatorService eventAggregatorService) {        
        super();
        this.modelFactory = modelFactory;
        this.uiElementFactory = uiElementFactory;
        this.eventAggregatorService = eventAggregatorService;

        subscribeToEvents();

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

    private void subscribeToEvents() {
        eventAggregatorService.subscribe(ProjectLoadedEvent.class, event -> load(event.getProject()));
    }

    public void addUseCaseButtonClicked() {        
        if (useCaseDiagram != null) {
            var newUseCase = modelFactory.createUseCase(useCaseDiagram, UUID.randomUUID(), "New use case", false);
            var uiElementPair = uiElementFactory.CreateUseCase(newUseCase);
            uiElementPair.getValue().setOnRemoveClicked(this::removeUseCase);
            useCasesList.getItems().add(uiElementPair.getKey());
        }
    }

    @Override
    public void load(ModelBase object) {
        if (object instanceof Project project) {
            this.project = project;
            useCaseDiagramComboBox.getItems().clear();
            useCaseDiagramComboBox.getItems().addAll(project.getUseCaseDiagramList().stream().map(UseCaseDiagramPresenter::new).toList());
        }
        else
            throw new UnsupportedOperationException();
    }

    @Override
    public void unload() {
        // TODO
    }
    
    @FXML
    private void useCaseDiagramSelectionChanged() {
        this.useCaseDiagram = useCaseDiagramComboBox.getSelectionModel().getSelectedItem().useCaseDiagram();
        useCasesList.getItems().clear();
        var useCasePairs = useCaseDiagram.getUseCaseList().stream().map(uiElementFactory::CreateUseCase).toList();
        useCasePairs.stream().map(Pair::getValue).forEach(useCaseController -> useCaseController.setOnRemoveClicked(this::removeUseCase));
        useCasesList.getItems().addAll(useCasePairs.stream().map(Pair::getKey).toList());
    }

    private Void removeUseCase(AnchorPane pane) {
        useCasesList.getItems().remove(pane);
        return null;
    }
    
    private record UseCaseDiagramPresenter(UseCaseDiagram useCaseDiagram) {
        @Override
        public String toString() {
            return useCaseDiagram.getId().toString();
        }
    }
    
}
