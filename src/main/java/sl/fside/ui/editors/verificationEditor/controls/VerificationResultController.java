package sl.fside.ui.editors.verificationEditor.controls;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class VerificationResultController {
    @FXML
    public AnchorPane verificationResultRoot;
    @FXML
    public TitledPane titledPane;
    @FXML
    public VirtualizedScrollPane<CodeArea> scrollPane;
    @FXML
    public CodeArea codeArea;

    public void initialize() {
        titledPane.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    }

    public void setVerificationResult(String outputContent, String title) {
        codeArea.clear();
        codeArea.appendText(outputContent);
        titledPane.setText(title);
    }
}
