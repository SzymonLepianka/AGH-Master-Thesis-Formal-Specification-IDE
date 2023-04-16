package sl.fside.ui.editors.activityDiagramEditor.resultsEditor;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;

public class ResultsEditorController {

    @FXML
    public TextArea folTextArea;
    @FXML
    public TextArea ltlTextArea;
    @FXML
    public Text folTextTitle;
    @FXML
    public Text patternTextTitle;
    @FXML
    public TextArea patternTextArea;
    @FXML
    public Text ltlTextTitle;
    @FXML
    public Text temp1;
    @FXML
    private AnchorPane root;

    /**
     * Called by JavaFX when FXML is loaded.
     */
    public void initialize() {
        patternTextTitle.setFont(Font.font("FontAwesome", 16));
        patternTextTitle.setText("Pattern Expression");
        patternTextArea.setText(NodesManager.getInstance().getPatternExpressionAfterProcessingNesting());

        folTextTitle.setFont(Font.font("FontAwesome", 16));
        folTextTitle.setText("First Order Logic");
        folTextArea.setText(NodesManager.getInstance().getFolLogicalSpecification());

        ltlTextTitle.setFont(Font.font("FontAwesome", 16));
        ltlTextTitle.setText("Linear Temporal Logic");
        ltlTextArea.setText(NodesManager.getInstance().getLtlLogicalSpecification());

        temp1.setFont(Font.font("FontAwesome", 5));
    }
}
