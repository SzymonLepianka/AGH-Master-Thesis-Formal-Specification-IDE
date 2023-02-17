package sl.fside.ui.editors.scenarioSelector;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import sl.fside.factories.IModelFactory;
import sl.fside.model.UseCase;
import sl.fside.ui.UIElementsFactory;

import java.util.Random;
import java.util.UUID;

public class ScenarioSelectorEditorController {
    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
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
            var newScenario = modelFactory.createScenario(useCase, UUID.randomUUID(), false);
            var uiElementPair = uiElementsFactory.createScenario(newScenario);

            // TODO usuwanie scenariusza
//            uiElementPair.getValue().setOnRemoveClicked(this::removeUseCase);

            scenarioList.getItems().add(uiElementPair.getKey());
        } else {
            //TODO usunąć to
            var newScenario = modelFactory.createScenario(null, UUID.randomUUID(), false);
            var uiElementPair = uiElementsFactory.createScenario(newScenario);
            scenarioList.getItems().add(uiElementPair.getKey());
        }
    }

    public void setUseCaseSelection(UseCase useCase) {
        this.useCase = useCase;
        scenarioList.getItems().clear();
        var scenarioPairs = useCase.getScenarioList().stream().map(uiElementsFactory::createScenario).toList();

        // TODO usuwanie scenariusza
//        scenarioPairs.stream().map(Pair::getValue)
//                .forEach(scenarioController -> scenarioController.setOnRemoveClicked(this::removeUseCase));

        scenarioList.getItems().addAll(scenarioPairs.stream().map(Pair::getKey).toList());
    }
}
