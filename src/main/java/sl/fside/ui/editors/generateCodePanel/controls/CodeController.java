package sl.fside.ui.editors.generateCodePanel.controls;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.model.*;

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
    private Code code;
    private Function<Pair<AnchorPane, CodeController>, Void> onRemoveClicked;

    @Inject
    public CodeController() {
    }

    public void load(Code code) {
        this.code = code;
        codeRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        // TODO
//        atomicActivityComboBox.getItems().addAll("First OrderOrderOrderOrderOrderOrderOrderOrderOrderOrder Logic", "Linear Temporal Logic");
//
//        // ustawia treść formuły (jeśli istnieje)
//        if (requirement.getContent() != null) {
//            textArea.setText(requirement.getContent());
//        }
//
//        // ustawia wybraną logikę (jeśli wybrano)
//        if (requirement.getLogic() != null) {
//            logicComboBox.getSelectionModel().select(requirement.getLogic());
//        }
//
//        // ustawia aktywność of Requirement
//        changeRequirementActivity();
    }

    public void initialize() {
    }

//    private void changeRequirementActivity() {
//        boolean isActive = requirement.isActive();
//
//        if (isActive) {
//            disableButton.setText("Deactivate");
//        } else {
//            disableButton.setText("Activate");
//        }
//
//        textArea.setDisable(!isActive);
//        removeButton.setDisable(!isActive);
//        logicComboBox.setDisable(!isActive);
//    }

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

    public Code getCode() {
        return code;
    }

//    @FXML
//    public void onDisableButtonClicked() {
//        requirement.setActive(!requirement.isActive());
//        changeRequirementActivity();
//    }

    @FXML
    public void onComboBoxClicked() {
        String selectedItem = atomicActivityComboBox.getSelectionModel().getSelectedItem();
        code.setAtomicActivity(selectedItem);
    }

    @FXML
    private void codeContentChanged() {
        code.setCode(textArea.getText());
    }
}
