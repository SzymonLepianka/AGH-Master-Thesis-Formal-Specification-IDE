package sl.fside.ui.editors.generateCodePanel.controls;

import com.google.inject.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.model.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;

import java.util.*;
import java.util.function.*;


public class CodeController {

    @FXML
    public AnchorPane codeRoot;
    @FXML
    public Button removeButton;
    @FXML
    public TextArea textArea;
    @FXML
    public ComboBox<String> atomicActivityComboBox;
    @FXML public ComboBox<String> languageComboBox;
    private Code code;
    private Function<Pair<AnchorPane, CodeController>, Void> onRemoveClicked;

    @Inject
    public CodeController() {
    }

    public void load(Code code) {
        this.code = code;
        codeRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        // ustawia treść kodu (jeśli istnieje)
        if (code.getCode() != null) {
            textArea.setText(code.getCode());
        }

        // ustawia wybrany AtomicActivity (jeśli wybrano)
        if (code.getAtomicActivity() != null) {
            atomicActivityComboBox.getSelectionModel().select(code.getAtomicActivity());
        }

        // ustawia wybrany język (jeśli wybrano)
        if (code.getLanguage() != null) {
            languageComboBox.getSelectionModel().select(code.getLanguage());
        }
    }

    public void initialize() {
        atomicActivityComboBox.setOnMouseClicked(event -> {
            if (!NodesManager.getInstance().getCurrentAtomicActivities().isEmpty()) {
                setAtomicActivitiesToComboBox(NodesManager.getInstance().getCurrentAtomicActivities());
            }
        });

        languageComboBox.getItems().addAll("Java", "Python");
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    @FXML
    public void onRemoveButtonClicked() {
        if (onRemoveClicked != null) onRemoveClicked.apply(new Pair<>(codeRoot, this));
    }

    public void setOnRemoveClicked(Function<Pair<AnchorPane, CodeController>, Void> onRemoveClicked) {
        this.onRemoveClicked = onRemoveClicked;
    }

    public void setAtomicActivitiesToComboBox(List<String> atomicActivities) {
        ObservableList<String> comboBoxItems = atomicActivityComboBox.getItems();

        // Remove items that are not in atomicActivities
        comboBoxItems.removeIf(item -> !atomicActivities.contains(item));

        // Add items that are in atomicActivities but not in the comboBox
        for (String activity : atomicActivities) {
            if (!comboBoxItems.contains(activity)) {
                comboBoxItems.add(activity);
            }
        }
    }

    public Code getCode() {
        return code;
    }

    @FXML
    public void onAtomicActivityComboBoxClicked() {
        String selectedItem = atomicActivityComboBox.getSelectionModel().getSelectedItem();
        code.setAtomicActivity(selectedItem);
    }

    @FXML
    private void codeContentChanged() {
        code.setCode(textArea.getText());
    }

    @FXML
    public void onLanguageComboBoxClicked() {
        String selectedItem = languageComboBox.getSelectionModel().getSelectedItem();
        code.setLanguage(selectedItem);
    }
}
