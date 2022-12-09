package sl.fside.ui.editors.useCaseSelector.controls;

import bgs.formalspecificationide.model.*;
import bgs.formalspecificationide.ui.*;
import com.google.inject.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.lang.reflect.*;
import java.util.function.*;

public class UseCaseController implements IController {

    private UseCase useCase;
    
    @FXML
    private AnchorPane pane;
    
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
    public UseCaseController() { }
    
    public void initialize() {
        isImportedCheckBox.setDisable(true);
    }
    
    public ObjectProperty<EventHandler<ActionEvent>> onIsSelectedChanged() {
        return isSelectedCheckBox.onActionProperty();
    }

    public void onRemoveButtonClicked() throws InvocationTargetException, IllegalAccessException {
        useCase.getParent().ifPresent(x -> x.removeChild(useCase));
        
        if (onRemoveClicked != null)
            onRemoveClicked.apply(pane);
    }

    public bgs.formalspecificationide.model.UseCase getUseCase() {
        return useCase;
    }

    @Override
    public void load(ModelBase object) {
        if (object instanceof UseCase useCase) {
            this.useCase = useCase;
            useCaseNameTextField.setText(useCase.getUseCaseName());
            isImportedCheckBox.setSelected(useCase.isImported());
            removeButton.setDisable(useCase.isImported());
        }
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void unload() {

    }

    public void setOnRemoveClicked(Function<AnchorPane, Void> onRemoveClicked) {
        this.onRemoveClicked = onRemoveClicked;
    }
    
    @FXML
    private void useCaseNameChanged() {
        useCase.setName(useCaseNameTextField.getText());
    }
}
