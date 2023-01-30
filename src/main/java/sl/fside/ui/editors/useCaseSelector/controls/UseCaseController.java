package sl.fside.ui.editors.useCaseSelector.controls;

import com.google.inject.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import sl.fside.model.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public class UseCaseController {

    private UseCase useCase;

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

    private Function<AnchorPane, Void> onRemoveClicked;

    @Inject
    public UseCaseController() {
    }

    public void initialize() {
        isImportedCheckBox.setDisable(true);
    }

    public ObjectProperty<EventHandler<ActionEvent>> onIsSelectedChanged() {
        return isSelectedCheckBox.onActionProperty();
    }

    public void onRemoveButtonClicked() throws InvocationTargetException, IllegalAccessException {
//        useCase.getParent().ifPresent(x -> x.removeChild(useCase));

        if (onRemoveClicked != null) onRemoveClicked.apply(useCaseRoot);
    }

    public sl.fside.model.UseCase getUseCase() {
        return useCase;
    }

    //    @Override
    public void load(UseCase useCase) {
//        if (object instanceof UseCase useCase) {
        this.useCase = useCase;
        useCaseNameTextField.setText(useCase.getUseCaseName());
        isImportedCheckBox.setSelected(useCase.isImported());
        removeButton.setDisable(useCase.isImported());

        useCaseRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

//        }
//        else
//            throw new IllegalArgumentException();
    }

    //    @Override
//    public void unload() {
//
//    }
    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    public void setOnRemoveClicked(Function<AnchorPane, Void> onRemoveClicked) {
        this.onRemoveClicked = onRemoveClicked;
    }

    @FXML
    private void useCaseNameChanged() {
        useCase.setName(useCaseNameTextField.getText());
    }
}
