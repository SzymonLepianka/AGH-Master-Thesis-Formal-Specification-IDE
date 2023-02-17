package sl.fside.ui.editors.actionEditor;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import sl.fside.factories.IModelFactory;
import sl.fside.model.Scenario;
import sl.fside.ui.UIElementsFactory;

import java.util.Random;

public class ActionEditorController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    @FXML
    public TitledPane actionEditorRoot;
    @FXML
    public AnchorPane actionEditorAnchorPane;
    @FXML
    public ListView<AnchorPane> actionsList;
    @FXML
    public Button addActionButton;
    private Scenario scenario;

    @Inject
    public ActionEditorController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
    }

    public void initialize() {
        actionsList.setSelectionModel(new NoSelectionModel<>());

        actionEditorRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        actionEditorAnchorPane.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        actionsList.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        addActionButton.setBorder(new Border(
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
    public void addActionButtonClicked() {
        String actionContent = showActionContentDialog();
        if (!actionContent.isEmpty()) {
            if (scenario != null) {
                var uiElementPair = uiElementsFactory.createAction(actionContent);

                // TODO usuwanie akcji
//            uiElementPair.getValue().setOnRemoveClicked(this::removeUseCase);

                actionsList.getItems().add(uiElementPair.getKey());
            } else {
                //TODO usunąć to
                var uiElementPair = uiElementsFactory.createAction(actionContent);
                actionsList.getItems().add(uiElementPair.getKey());
            }
        }
    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        actionsList.getItems().clear();
        var actionPairs = scenario.getActions().stream().map(uiElementsFactory::createAction).toList();

        // TODO usuwanie akcji
//        scenarioPairs.stream().map(Pair::getValue)
//                .forEach(scenarioController -> scenarioController.setOnRemoveClicked(this::removeUseCase));

        actionsList.getItems().addAll(actionPairs.stream().map(Pair::getKey).toList());
    }

    private String showActionContentDialog() {

        // Show input action content dialog
        var actionContentInputDialog = new TextInputDialog("action_content");
        actionContentInputDialog.setTitle("Creating action...");
        actionContentInputDialog.setHeaderText("Enter a action content");

        // disable empty action content
        actionContentInputDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(actionContentInputDialog.getEditor().textProperty().isEmpty());

        actionContentInputDialog.showAndWait();

        // get action content from input dialog
        return actionContentInputDialog.getResult();
    }


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
