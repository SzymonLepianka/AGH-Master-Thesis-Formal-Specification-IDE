package sl.fside.ui.editors.requirementEditor.controls;

import com.google.inject.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
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
    @FXML public Button disableButton;
    private Requirement requirement;
    private Function<Pair<AnchorPane, RequirementController>, Void> onRemoveClicked;

    @Inject
    public RequirementController() {
    }

    public void load(Requirement requirement) {
        this.requirement = requirement;
        requirementRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
    }

    public void initialize() {
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
        //TODO
    }
}
