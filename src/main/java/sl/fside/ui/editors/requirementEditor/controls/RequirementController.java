package sl.fside.ui.editors.requirementEditor.controls;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.model.*;

import java.util.*;
import java.util.function.*;


public class RequirementController {

    @FXML
    public AnchorPane requirementRoot;
    @FXML
    public Button removeButton;
    @FXML
    public TextArea textArea;
    @FXML
    public Button disableButton;
    @FXML
    public ComboBox<String> logicComboBox;
    private Requirement requirement;
    private Function<Pair<AnchorPane, RequirementController>, Void> onRemoveClicked;

    @Inject
    public RequirementController() {
    }

    public void load(Requirement requirement) {
        this.requirement = requirement;
        requirementRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        logicComboBox.getItems().addAll("First Order Logic", "Linear Temporal Logic");

        // ustawia treść formuły (jeśli istnieje)
        if (requirement.getContent() != null) {
            textArea.setText(requirement.getContent());
        }

        // ustawia wybraną logikę (jeśli wybrano)
        if (requirement.getLogic() != null) {
            logicComboBox.getSelectionModel().select(requirement.getLogic());
        }

        // ustawia aktywność of Requirement
        changeRequirementActivity();
    }

    public void initialize() {
    }

    private void changeRequirementActivity() {
        boolean isActive = requirement.isActive();

        if (isActive) {
            disableButton.setText("Deactivate");
        } else {
            disableButton.setText("Activate");
        }

        textArea.setDisable(!isActive);
        removeButton.setDisable(!isActive);
        logicComboBox.setDisable(!isActive);
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
        if (onRemoveClicked != null) onRemoveClicked.apply(new Pair<>(requirementRoot, this));
    }

    public void setOnRemoveClicked(Function<Pair<AnchorPane, RequirementController>, Void> onRemoveClicked) {
        this.onRemoveClicked = onRemoveClicked;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    @FXML
    public void onDisableButtonClicked() {
        requirement.setActive(!requirement.isActive());
        changeRequirementActivity();
    }

    @FXML
    public void onComboBoxClicked() {
        String selectedItem = logicComboBox.getSelectionModel().getSelectedItem();
        requirement.setLogic(selectedItem);
    }

    @FXML
    private void requirementContentChanged() {
        requirement.setContent(textArea.getText());
    }
}
