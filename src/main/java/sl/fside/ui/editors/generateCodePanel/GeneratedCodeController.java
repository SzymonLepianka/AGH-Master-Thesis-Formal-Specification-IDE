package sl.fside.ui.editors.generateCodePanel;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.fxmisc.richtext.*;

public class GeneratedCodeController {
    @FXML
    public AnchorPane generatedCodeRoot;
    @FXML
    public TitledPane javaTitledPane;
    @FXML
    public CodeArea javaCodeArea;
    @FXML
    public TitledPane pythonTitledPane;
    @FXML
    public CodeArea pythonCodeArea;

    public void initialize() {

        javaTitledPane.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        pythonTitledPane.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        // add line numbers to the left of area
        javaCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(javaCodeArea));
        pythonCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(pythonCodeArea));
    }

    public void setCode(String javaCode, String pythonCode) {
        javaCodeArea.clear();
        javaCodeArea.appendText(javaCode);
        pythonCodeArea.clear();
        pythonCodeArea.appendText(pythonCode);
    }
}
