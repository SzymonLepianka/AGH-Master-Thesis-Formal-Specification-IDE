package sl.fside.ui.editors.actionEditor;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.fxmisc.richtext.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;

import java.util.*;
import java.util.regex.*;

public class ActionEditorController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;

    @FXML
    public TitledPane actionEditorRoot;
    @FXML
    public AnchorPane actionEditorAnchorPane;
    @FXML
    public Button showCurrentAtomicActivitiesButton;
    @FXML
    public StyleClassedTextArea scenarioContentTextArea;
    private Scenario scenario;

    @Inject
    public ActionEditorController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory,
                                  LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {
        actionEditorRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        scenarioContentTextArea.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        updateActionEditor();

        scenarioContentTextArea.getStylesheets()
                .add(ActionEditorController.class.getResource("custom-styles.css").toExternalForm());
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateActionEditor();

        // show scenario content
        scenarioContentTextArea.clear();
        scenarioContentTextArea.appendText(scenario.getContent());

        // show existing atomic activities
        for (AtomicActivity atomicActivity : scenario.getAtomicActivities()) {
            markWordsInTextArea(atomicActivity.getContent(), List.of("own-bold"));
        }

        // ustawia tytuł panelu
        if (scenario.getScenarioName() != null && scenario.getScenarioName().length() > 35) {
            actionEditorRoot.setText("Scenario content (for '" + scenario.getScenarioName().substring(0, 34) + "...')");
        } else {
            actionEditorRoot.setText("Scenario content (for '" + scenario.getScenarioName() + "')");
        }

        loggerService.logInfo("Scenario set to ActionEditor - " + scenario.getId());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateActionEditor();
        scenarioContentTextArea.clear();
        actionEditorRoot.setText("Scenario content");
    }

    private void updateActionEditor() {
        // setting disable property of the actionEditorRoot TitledPane based on the value of the scenario variable
        actionEditorRoot.setDisable(scenario == null);

        // Set the background color using CSS
        if (scenario == null) {
            scenarioContentTextArea.setStyle("-fx-background-color: #F0F0F0;");
        } else {
            scenarioContentTextArea.setStyle("-fx-background-color: #FFFFFF;");
        }
    }

    @FXML
    public void showCurrentAtomicActivitiesButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Atomic activities for Scenario - '" + scenario.getScenarioName() + "'");
        alert.setHeaderText(null);
        alert.setContentText(scenario.showAtomicActivities());
        alert.showAndWait();
    }

    // Add an event handler to the text area to bold words when clicked
    public void scenarioContentOnMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            int clickIndex = scenarioContentTextArea.getCaretPosition();
            int startIndex = getWordStartIndex(scenarioContentTextArea, clickIndex);
            int endIndex = getWordEndIndex(scenarioContentTextArea, clickIndex);

            // Check if the clicked word is already bolded
            boolean isBold = false;
            for (String styleClass : scenarioContentTextArea.getStyleSpans(startIndex, endIndex).getStyleSpan(0)
                    .getStyle()) {
                if (styleClass.equals("own-bold")) {
                    isBold = true;
                    break;
                }
            }

            // Toggle the bold style of the clicked word
            if (isBold) {
                scenarioContentTextArea.setStyle(startIndex, endIndex, Collections.emptyList());
                scenario.removeAtomicActivity(scenarioContentTextArea.getText(startIndex, endIndex));

                // remove bolding from other occurrences of the same word
                markWordsInTextArea(scenarioContentTextArea.getText(startIndex, endIndex), Collections.emptyList());

            } else {
                scenarioContentTextArea.setStyle(startIndex, endIndex, List.of("own-bold"));
                modelFactory.createAtomicActivity(scenario, scenarioContentTextArea.getText(startIndex, endIndex));

                // bold other occurrences of the same word
                markWordsInTextArea(scenarioContentTextArea.getText(startIndex, endIndex), List.of("own-bold"));
            }
        }
    }

    private void markWordsInTextArea(String wordToMark, List<String> styles) {
        String text = scenarioContentTextArea.getText();
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(wordToMark) + "\\b");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            scenarioContentTextArea.setStyle(start, end, styles);
        }
    }

    // Helper method to get the index of the start of the word at the given index
    private int getWordStartIndex(StyleClassedTextArea textArea, int index) {
        String text = textArea.getText();
        int startIndex = index;
        while (startIndex > 0 && !Character.isWhitespace(text.charAt(startIndex - 1))) {
            startIndex--;
        }
        return startIndex;
    }

    // Helper method to get the index of the end of the word at the given index
    private int getWordEndIndex(StyleClassedTextArea textArea, int index) {
        String text = textArea.getText();
        int endIndex = index;
        while (endIndex < text.length() && !Character.isWhitespace(text.charAt(endIndex))) {
            endIndex++;
        }
        return endIndex;
    }

    @FXML
    private void scenarioContentChanged() {
        scenario.setContent(scenarioContentTextArea.getText());
        getCurrentlyBoldedWords();
    }

    private void getCurrentlyBoldedWords() {
        String text = scenarioContentTextArea.getText();
        String[] words = text.split("\\s+"); // split text by ' ' and '\n'
        List<String> boldedWords = new ArrayList<>();

        int start = 0;
        for (String word : words) {
            if (!word.isEmpty()) {
                int startIndex = text.indexOf(word, start);
                int endIndex = startIndex + word.length() - 1;
                start = endIndex + 1; // Add 1 to account for the whitespace separator

                // check if word is bolded
                for (String styleClass : scenarioContentTextArea.getStyleSpans(startIndex, endIndex + 1).getStyleSpan(0)
                        .getStyle()) {
                    if (styleClass.equals("own-bold")) {
                        boldedWords.add(scenarioContentTextArea.getText(startIndex, endIndex + 1));
                        break;
                    }
                }
            }
        }

        // replace old atomic activities with new ones
        Set<String> boldedWordsSet = new HashSet<>(boldedWords);
        scenario.removeAllAtomicActivities();
        boldedWordsSet.forEach(x -> modelFactory.createAtomicActivity(scenario, x));
    }
}
