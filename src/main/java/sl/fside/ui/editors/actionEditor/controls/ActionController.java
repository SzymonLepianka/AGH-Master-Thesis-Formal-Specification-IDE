package sl.fside.ui.editors.actionEditor.controls;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Random;

public class ActionController {

    @FXML
    public TextFlow textFlow;
    @FXML
    public AnchorPane actionRoot;

    @Inject
    public ActionController() {
    }

    public void load(String action) {
        addSelectingAtomicActivities(action);
        actionRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    private void addSelectingAtomicActivities(String action) {
        for (String word : action.split(" ")) {
            Text wordText = new Text(word + " ");
            wordText.setOnMouseClicked(event -> {
                if (wordText.getStyle().contains("-fx-font-weight: bold")) {
                    wordText.setStyle("");
                } else {
                    wordText.setStyle("-fx-font-weight: bold");
                }
            });
            wordText.textProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null && newSelection.equals(wordText.getText())) {
                    wordText.setStyle("-fx-font-weight: bold");
                } else {
                    wordText.setStyle("");
                }
            });
            textFlow.getChildren().add(wordText);
        }
    }
}
