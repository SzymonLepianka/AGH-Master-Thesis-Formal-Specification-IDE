package sl.fside.ui.editors.requirementEditor;

import com.google.inject.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.requirementEditor.controls.*;

import java.util.*;

public class RequirementEditorController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;
    private final List<Pair<AnchorPane, RequirementController>> uiElementRequirementPairs = new ArrayList<>();

    @FXML
    public TitledPane requirementEditorRoot;
    @FXML
    public AnchorPane requirementEditorAnchorPane;
    @FXML
    public ListView<AnchorPane> requirementsList;
    @FXML
    public Button addRequirementButton;

    private Scenario scenario;

    @Inject
    public RequirementEditorController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory,
                                       LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {
        requirementsList.setSelectionModel(new NoSelectionModel<>());

        requirementEditorRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        requirementEditorAnchorPane.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        requirementsList.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        addRequirementButton.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        updateRequirementEditor();
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    @FXML
    public void addRequirementButtonClicked() {
        if (scenario != null) {

            // creating new requirement
            Requirement newRequirement = modelFactory.createRequirement(scenario, UUID.randomUUID());
            Pair<AnchorPane, RequirementController> uiElementPair = uiElementsFactory.createRequirement(newRequirement);
            uiElementRequirementPairs.add(uiElementPair);

            // usuwanie Requirement
            uiElementPair.getValue().setOnRemoveClicked(this::removeRequirement);

            // dodaje nową akcję do obecnych
            requirementsList.getItems().add(uiElementPair.getKey());
        } else {
            System.out.println("addRequirementButtonClicked - Nigdy nie powinien się tu znaleźć");
        }

    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateRequirementEditor();
        requirementsList.getItems().clear();

        // create new requirements
        var requirementPairs = scenario.getRequirements().stream().map(uiElementsFactory::createRequirement).toList();
        uiElementRequirementPairs.clear();
        uiElementRequirementPairs.addAll(requirementPairs);

        // usuwanie Requirement
        uiElementRequirementPairs.forEach(pair -> pair.getValue().setOnRemoveClicked(this::removeRequirement));

        // add new requirements to list
        requirementsList.getItems().addAll(requirementPairs.stream().map(Pair::getKey).toList());

        loggerService.logInfo("Scenario set to RequirementEditor - " + scenario.getId());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateRequirementEditor();
        requirementsList.getItems().clear();
        uiElementRequirementPairs.clear();
    }

    private void updateRequirementEditor() {
        // setting disable property of the requirementEditorRoot TitledPane based on the value of the scenario variable
        requirementEditorRoot.setDisable(scenario == null);
    }

    private Void removeRequirement(Pair<AnchorPane, RequirementController> pair) {
        requirementsList.getItems().remove(pair.getKey());
        scenario.removeRequirement(pair.getValue().getRequirement());
        uiElementRequirementPairs.remove(pair);

        loggerService.logInfo("Requirement removed - " + pair.getValue().getRequirement().getId());
        return null;
    }

    // the purpose of this class is to block the visual selection of Requirement from the list in the panel
    public static class NoSelectionModel<T> extends MultipleSelectionModel<T> {

        @Override
        public ObservableList<Integer> getSelectedIndices() {
            return FXCollections.emptyObservableList();
        }

        @Override
        public ObservableList<T> getSelectedItems() {
            return FXCollections.emptyObservableList();
        }

        @Override
        public void selectIndices(int index, int... indices) {
        }

        @Override
        public void selectAll() {
        }

        @Override
        public void selectFirst() {
        }

        @Override
        public void selectLast() {
        }

        @Override
        public void clearAndSelect(int index) {
        }

        @Override
        public void select(int index) {
        }

        @Override
        public void select(T obj) {
        }

        @Override
        public void clearSelection(int index) {
        }

        @Override
        public void clearSelection() {
        }

        @Override
        public boolean isSelected(int index) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void selectPrevious() {
        }

        @Override
        public void selectNext() {
        }
    }
}