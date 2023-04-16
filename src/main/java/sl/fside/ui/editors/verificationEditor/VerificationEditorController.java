package sl.fside.ui.editors.verificationEditor;

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
import sl.fside.ui.editors.verificationEditor.controls.*;

import java.util.*;

public class VerificationEditorController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;
    private final List<Pair<AnchorPane, VerificationController>> uiElementVerificationPairs = new ArrayList<>();

    @FXML
    public TitledPane verificationEditorRoot;
    @FXML
    public AnchorPane verificationEditorAnchorPane;
    @FXML
    public ListView<AnchorPane> verificationsList;
    @FXML
    public Button addVerificationButton;

    private Scenario scenario;

    @Inject
    public VerificationEditorController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory,
                                        LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {
        verificationsList.setSelectionModel(new NoSelectionModel<>());

        verificationEditorRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        verificationEditorAnchorPane.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        verificationsList.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        addVerificationButton.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        updateVerificationEditor();
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    @FXML
    public void addVerificationButtonClicked() {
        if (scenario != null) {

            // creating new Verification
            Verification newVerification = modelFactory.createVerification(scenario, UUID.randomUUID());
            Pair<AnchorPane, VerificationController> uiElementPair =
                    uiElementsFactory.createVerification(newVerification);
            uiElementVerificationPairs.add(uiElementPair);

            // usuwanie Verification
            uiElementPair.getValue().setOnRemoveClicked(this::removeVerification);

            // dodaje nową weryfikację do obecnych
            verificationsList.getItems().add(uiElementPair.getKey());
        } else {
            System.out.println("addVerificationButtonClicked - Nigdy nie powinien się tu znaleźć");
        }

    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateVerificationEditor();
        verificationsList.getItems().clear();

        // create new Verifications
        var verificationPairs =
                scenario.getVerifications().stream().map(uiElementsFactory::createVerification).toList();
        uiElementVerificationPairs.clear();
        uiElementVerificationPairs.addAll(verificationPairs);

        // usuwanie Verification
        uiElementVerificationPairs.forEach(pair -> pair.getValue().setOnRemoveClicked(this::removeVerification));

        // add new Verifications to list
        verificationsList.getItems().addAll(verificationPairs.stream().map(Pair::getKey).toList());

        loggerService.logInfo("Scenario set to VerificationEditor - " + scenario.getId());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateVerificationEditor();
        verificationsList.getItems().clear();
        uiElementVerificationPairs.clear();
    }

    private void updateVerificationEditor() {
        // setting disable property of the verificationEditorRoot TitledPane based on the value of the scenario variable
        verificationEditorRoot.setDisable(scenario == null);
    }

    private Void removeVerification(Pair<AnchorPane, VerificationController> pair) {
        verificationsList.getItems().remove(pair.getKey());
        scenario.removeVerification(pair.getValue().getVerification());
        uiElementVerificationPairs.remove(pair);

        loggerService.logInfo("Verification removed - " + pair.getValue().getVerification().getId());
        return null;
    }

    // the purpose of this class is to block the visual selection of Verification from the list in the panel
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