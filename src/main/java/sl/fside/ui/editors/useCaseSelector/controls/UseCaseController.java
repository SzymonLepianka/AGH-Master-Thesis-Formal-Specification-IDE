package sl.fside.ui.editors.useCaseSelector.controls;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.model.*;

import java.util.function.*;

public class UseCaseController {

    private UseCase useCase;
    private Function<Pair<AnchorPane, UseCaseController>, Void> onRemoveClicked;

    @FXML
    private AnchorPane useCaseRoot;
    @FXML
    private TextField useCaseNameTextField;
    @FXML
    private Button removeButton;
    @FXML
    private CheckBox isSelectedCheckBox;
    @FXML
    private CheckBox isImportedCheckBox;

    @Inject
    public UseCaseController() {
    }

    public void initialize() {
    }

    public void load(UseCase useCase) {
        this.useCase = useCase;
        useCaseNameTextField.setText(useCase.getUseCaseName());
        useCaseNameTextField.setEditable(!useCase.isImported());
        isImportedCheckBox.setSelected(useCase.isImported());
        removeButton.setDisable(useCase.isImported());
        useCaseRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
    }

    public void setIsSelectedCheckBox(boolean isSelectedCheckBox) {
        this.isSelectedCheckBox.setSelected(isSelectedCheckBox);
    }

    public UseCase getUseCase() {
        return useCase;
    }

    @FXML
    public void onRemoveUseCaseButtonClicked() {
        if (onRemoveClicked != null) onRemoveClicked.apply(new Pair<>(useCaseRoot, this));
    }

    public void setOnRemoveClicked(Function<Pair<AnchorPane, UseCaseController>, Void> onRemoveClicked) {
        this.onRemoveClicked = onRemoveClicked;
    }

    @FXML
    private void useCaseNameChanged() {
        useCase.setName(useCaseNameTextField.getText());
    }
}
