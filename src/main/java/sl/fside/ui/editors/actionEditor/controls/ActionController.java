package sl.fside.ui.editors.actionEditor.controls;

import com.google.inject.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import sl.fside.model.*;

import java.util.*;


public class ActionController {

    private final ObservableList<String> boldedWords = FXCollections.observableArrayList();
    @FXML
    public AnchorPane actionRoot;
    @FXML
    private TextFlow textFlow;
    private Action action;

    @Inject
    public ActionController() {
    }

    public void load(Action action) {
        this.action = action;
        addSelectingAtomicActivities(action.getActionContent());
        actionRoot.setBorder(new Border(
                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
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

    private void addSelectingAtomicActivities(String actionContent) {
        for (String word : actionContent.split(" ")) {
            Text wordText = new Text(word + " ");
            wordText.setOnMouseClicked(event -> {
                if (wordText.getStyle().contains("-fx-font-weight: bold")) {
                    wordText.setStyle("");
                    boldedWords.remove(wordText.getText().trim());
                } else {
                    wordText.setStyle("-fx-font-weight: bold");
                    boldedWords.add(wordText.getText().trim());
                }
            });
            wordText.textProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null && newSelection.equals(wordText.getText())) {
                    wordText.setStyle("-fx-font-weight: bold");
                    boldedWords.add(wordText.getText().trim());
                } else {
                    wordText.setStyle("");
                    boldedWords.remove(wordText.getText().trim());
                }
            });
            textFlow.getChildren().add(wordText);
        }
    }

    public void boldExistingAtomicActivities(String atomicActivity) {
        for (var node : textFlow.getChildren()) {
            if (((Text) node).getText().equals(atomicActivity + " ")) { // TODO zrobić coś ze spacjami ; co jak atomic activity zawiera spację?
                node.setStyle("-fx-font-weight: bold");
            }
        }
    }

    public void unBoldExistingAtomicActivities(String atomicActivity) {
        for (var node : textFlow.getChildren()) {
            if (((Text) node).getText().equals(atomicActivity + " ")) {
                node.setStyle("");
            }
        }
    }

    public ObservableList<String> getBoldedWords() {
        return boldedWords;
    }
}
