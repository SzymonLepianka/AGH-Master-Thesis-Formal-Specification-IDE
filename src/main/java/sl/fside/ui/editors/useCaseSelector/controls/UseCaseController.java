package sl.fside.ui.editors.useCaseSelector.controls;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.model.*;

import java.util.*;
import java.util.function.*;

public class UseCaseController {

    private UseCase useCase;
    private Function<Pair<AnchorPane, UseCaseController>, Void> onRemoveClicked;

    @FXML
    private AnchorPane useCaseRoot;
    @FXML
    private TextField useCaseNameTextField;
    @FXML
    private CheckBox isSelectedCheckBox;
    @FXML
    private CheckBox isImportedCheckBox;
    @FXML
    private Button removeButton;

    @Inject
    public UseCaseController() {
    }

    public void initialize() {
        isImportedCheckBox.setDisable(true);
    }

//    public ObjectProperty<EventHandler<ActionEvent>> onIsSelectedChanged() {
//        return isSelectedCheckBox.onActionProperty();
//    }


    public void setIsSelectedCheckBox(boolean isSelectedCheckBox) {
        this.isSelectedCheckBox.setSelected(isSelectedCheckBox);
    }

    public sl.fside.model.UseCase getUseCase() {
        return useCase;
    }

    public void load(UseCase useCase) {
        this.useCase = useCase;
        useCaseNameTextField.setText(useCase.getUseCaseName());
        isImportedCheckBox.setSelected(useCase.isImported());
        removeButton.setDisable(useCase.isImported());

        useCaseRoot.setBorder(new Border(
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
