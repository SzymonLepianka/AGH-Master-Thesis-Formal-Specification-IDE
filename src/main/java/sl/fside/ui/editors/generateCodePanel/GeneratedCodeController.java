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
    public TitledPane titledPane;
    @FXML
    public CodeArea codeArea;

    public void initialize() {

        titledPane.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    }

    public void setCode(String code, String language) {
        codeArea.clear();
        codeArea.appendText(code);
        titledPane.setText(language + " code");
    }
}
