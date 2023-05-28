package sl.fside.ui.editors.verificationEditor.controls;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.fxmisc.flowless.*;
import org.fxmisc.richtext.*;

import java.nio.file.*;

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

    public void setVerificationResult(String outputContent, Path outputFilePath) {
        codeArea.clear();
        codeArea.appendText(outputContent);
        titledPane.setText("Output saved to: " + outputFilePath.toString());
    }
}
