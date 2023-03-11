package sl.fside.ui.editors.scenarioSelector;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.scenarioSelector.controls.*;

import java.util.*;

public class ScenarioSelectorEditorController {
    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final List<Pair<AnchorPane, ScenarioController>> uiElementScenarioPairs = new ArrayList<>();
    @FXML
    public TitledPane scenarioSelectorEditorRoot;
    @FXML
    public AnchorPane scenarioSelectorEditorAnchorPane;
    @FXML
    public ListView<AnchorPane> scenarioList;
    @FXML
    public Button addScenarioButton;
    private UseCase useCase;

    @Inject
    public ScenarioSelectorEditorController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
    }

    public void initialize() {
        scenarioSelectorEditorRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        scenarioSelectorEditorAnchorPane.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        scenarioList.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        addScenarioButton.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
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

            // TODO usuwanie scenariusza
//            uiElementPair.getValue().setOnRemoveClicked(this::removeUseCase);

            // dodaje nowy scenariusz do obecnych
            scenarioList.getItems().add(uiElementPair.getKey());

        } else {
            //TODO usunąć to
            var newScenario = modelFactory.createScenario(null, UUID.randomUUID(), false);
            var uiElementPair = uiElementsFactory.createScenario(newScenario);
            scenarioList.getItems().add(uiElementPair.getKey());
        }
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
        System.out.println("size" + uiElementScenarioPairs.size());

        for (var scenarioPair : uiElementScenarioPairs) {
            ScenarioController sc = scenarioPair.getValue();

            // odznacz poprzedni scenariusz główny
            if (sc.getIsMainScenarioCheckBox().isSelected() && !sc.equals(mainScenarioController)) {
                sc.getIsMainScenarioCheckBox().setSelected(false);
            }
        }
    }

    public void setUseCaseSelection(UseCase useCase) {
        this.useCase = useCase;
        scenarioList.getItems().clear();

        // create new scenarios
        var scenarioPairs = useCase.getScenarioList().stream().map(uiElementsFactory::createScenario).toList();
        uiElementScenarioPairs.clear();
        uiElementScenarioPairs.addAll(scenarioPairs);

        // add listener to scenario's checkboxes, to define main scenario
        scenarioPairs.forEach(this::addListenerToUiElementScenarioPair);

        // TODO usuwanie scenariusza
//        scenarioPairs.stream().map(Pair::getValue)
//                .forEach(scenarioController -> scenarioController.setOnRemoveClicked(this::removeUseCase));

        // add new scenarios to list
        scenarioList.getItems().addAll(scenarioPairs.stream().map(Pair::getKey).toList());
    }
}
