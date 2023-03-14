package sl.fside.ui.editors.actionEditor;

import com.google.inject.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.actionEditor.controls.*;

import java.util.*;

public class ActionEditorController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final List<Pair<AnchorPane, ActionController>> uiElementActionPairs = new ArrayList<>();

    @FXML
    public TitledPane actionEditorRoot;
    @FXML
    public AnchorPane actionEditorAnchorPane;
    @FXML
    public ListView<AnchorPane> actionsList;
    @FXML
    public Button addActionButton;
    @FXML
    public Button showCurrentAtomicActivitiesButton;
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

        updateActionEditor();
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
        if (actionContent != null && !actionContent.isEmpty()) {
            if (scenario != null) {

                // creating new action
                Action newAction = modelFactory.createAction(scenario, UUID.randomUUID(), actionContent);
                var uiElementPair = uiElementsFactory.createAction(newAction);
                uiElementActionPairs.add(uiElementPair);

                // add listener to actionContent, to define atomic activities
                addListenerToUiElementActionPair(uiElementPair);

                // TODO usuwanie akcji
//            uiElementPair.getValue().setOnRemoveClicked(this::removeUseCase);

                // dodaje nową akcję do obecnych
                actionsList.getItems().add(uiElementPair.getKey());
            } else {
                System.out.println("addActionButtonClicked - Nigdy nie powinien się tu znaleźć");
            }
        }
    }

    private void addListenerToUiElementActionPair(Pair<AnchorPane, ActionController> uiElementPair) {
        uiElementPair.getValue().getBoldedWords().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {

                // jeśli dodano element do listy
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(c -> {
                        uiElementActionPairs.forEach(pair -> pair.getValue().boldExistingAtomicActivities(c));
                        modelFactory.createAtomicActivity(scenario, c);
                    });
                }

                // jeśli usunięto element z listy
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(c -> {
                        uiElementActionPairs.forEach(pair -> pair.getValue().unBoldExistingAtomicActivities(c));
                        scenario.removeAtomicActivity(c);
                    });
                }
            }
        });
    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateActionEditor();
        actionsList.getItems().clear();

        // create new actions
        var actionPairs = scenario.getActions().stream().map(uiElementsFactory::createAction).toList();
        uiElementActionPairs.clear();
        uiElementActionPairs.addAll(actionPairs);

        // show existing atomic activities
        for (AtomicActivity atomicActivity : scenario.getAtomicActivities()) {
            uiElementActionPairs.forEach(pair -> {
                pair.getValue().boldExistingAtomicActivities(atomicActivity.getContent());
                pair.getValue().addBoldedWord(atomicActivity.getContent());
            });
        }

        // add listener to actionContent, to define atomic activities
        actionPairs.forEach(this::addListenerToUiElementActionPair);

        // TODO usuwanie akcji
//        scenarioPairs.stream().map(Pair::getValue)
//                .forEach(scenarioController -> scenarioController.setOnRemoveClicked(this::removeUseCase));

        // add new actions to list
        actionsList.getItems().addAll(actionPairs.stream().map(Pair::getKey).toList());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateActionEditor();
        actionsList.getItems().clear();
        uiElementActionPairs.clear();
    }

    private void updateActionEditor() {
        // setting disable property of the actionEditorRoot TitledPane based on the value of the scenario variable
        actionEditorRoot.setDisable(scenario == null);
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

    @FXML
    public void showCurrentAtomicActivitiesButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(scenario.showAtomicActivities());
        alert.showAndWait();
    }

    // the purpose of this class is to block the visual selection of Action from the list in the panel
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
