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
import sl.fside.ui.editors.actionEditor.controls.*;
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
//    @FXML
//    public Button showCurrentAtomicActivitiesButton;
    private Scenario scenario;

    @Inject
    public RequirementEditorController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory, LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {
        requirementsList.setSelectionModel(new NoSelectionModel<>());

        requirementEditorRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        actionEditorAnchorPane.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        actionsList.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        addActionButton.setBorder(new Border(
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
        // TODO
//        String actionContent = showActionContentDialog();
//        if (actionContent != null && !actionContent.isEmpty()) {
//            if (scenario != null) {
//
//                // creating new action
//                Action newAction = modelFactory.createAction(scenario, UUID.randomUUID(), actionContent);
//                Pair<AnchorPane, ActionController> uiElementPair = uiElementsFactory.createAction(newAction);
//                uiElementActionPairs.add(uiElementPair);
//
//                // add listener to actionContent, to define atomic activities
//                addListenerToUiElementActionPair(uiElementPair);
//
//                // usuwanie Action
//                uiElementPair.getValue().setOnRemoveClicked(this::removeAction);
//
//                // dodaje nową akcję do obecnych
//                actionsList.getItems().add(uiElementPair.getKey());
//            } else {
//                System.out.println("addActionButtonClicked - Nigdy nie powinien się tu znaleźć");
//            }
//        }
    }

//    private void addListenerToUiElementActionPair(Pair<AnchorPane, ActionController> uiElementPair) {
//        uiElementPair.getValue().getBoldedWords().addListener((ListChangeListener<String>) change -> {
//            while (change.next()) {
//
//                // jeśli dodano element do listy
//                if (change.wasAdded()) {
//                    change.getAddedSubList().forEach(c -> {
//                        uiElementActionPairs.forEach(pair -> pair.getValue().boldExistingAtomicActivities(c));
//                        modelFactory.createAtomicActivity(scenario, c);
//                    });
//                }
//
//                // jeśli usunięto element z listy
//                if (change.wasRemoved()) {
//                    change.getRemoved().forEach(c -> {
//                        uiElementActionPairs.forEach(pair -> pair.getValue().unBoldExistingAtomicActivities(c));
//                        scenario.removeAtomicActivity(c);
//                    });
//                }
//            }
//        });
//    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateRequirementEditor();
        requirementsList.getItems().clear();

        // create new requirements
        var requirementPairs = scenario.getRequirements().stream().map(uiElementsFactory::createRequirement).toList();
        uiElementRequirementPairs.clear();
        uiElementRequirementPairs.addAll(requirementPairs);

//        // show existing atomic activities
//        for (AtomicActivity atomicActivity : scenario.getAtomicActivities()) {
//            uiElementActionPairs.forEach(pair -> {
//                pair.getValue().boldExistingAtomicActivities(atomicActivity.getContent());
//                pair.getValue().addBoldedWord(atomicActivity.getContent());
//            });
//        }
//
//        // add listener to actionContent, to define atomic activities
//        actionPairs.forEach(this::addListenerToUiElementActionPair);

        // usuwanie Action
        uiElementRequirementPairs.forEach(pair -> pair.getValue().setOnRemoveClicked(this::removeRequirement));

        // add new actions to list
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

//    private String showActionContentDialog() {
//
//        // Show input action content dialog
//        var actionContentInputDialog = new TextInputDialog("action_content");
//        actionContentInputDialog.setTitle("Creating action...");
//        actionContentInputDialog.setHeaderText("Enter a action content");
//
//        // disable empty action content
//        actionContentInputDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
//                .bind(actionContentInputDialog.getEditor().textProperty().isEmpty());
//
//        actionContentInputDialog.showAndWait();
//
//        // get action content from input dialog
//        return actionContentInputDialog.getResult();
//    }
//
//    @FXML
//    public void showCurrentAtomicActivitiesButtonClicked() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Information");
//        alert.setHeaderText(null);
//        alert.setContentText(scenario.showAtomicActivities());
//        alert.showAndWait();
//    }

    private Void removeRequirement(Pair<AnchorPane, RequirementController> pair) {
        requirementsList.getItems().remove(pair.getKey());
        scenario.removeRequirement(pair.getValue().getRequirement());
        uiElementRequirementPairs.remove(pair);

//        // checking if in removed Requirement there was atomic activity not used in other actions
//        ArrayList<AtomicActivity> atomicActivitiesToRemove = new ArrayList<>();
//        for (AtomicActivity atomicActivity : scenario.getAtomicActivities()) {
//            boolean removeAtomicActivity = uiElementActionPairs.stream().noneMatch(
//                    actionPair -> actionPair.getValue().isAtomicActivityInAction(atomicActivity.getContent()));
//            if (removeAtomicActivity) {
//                atomicActivitiesToRemove.add(atomicActivity);
//            }
//        }
//        scenario.removeAtomicActivities(atomicActivitiesToRemove);

        loggerService.logInfo("Requirement removed - " + pair.getValue().getRequirement().getId());
        return null;
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