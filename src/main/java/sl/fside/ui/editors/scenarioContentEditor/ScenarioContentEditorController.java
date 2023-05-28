package sl.fside.ui.editors.scenarioContentEditor;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;

import java.util.*;
import java.util.regex.*;

public class ScenarioContentEditorController {

    private final IModelFactory modelFactory;
    private final LoggerService loggerService;
    private final MainWindowController mainWindowController;

    @FXML
    public TitledPane scenarioContentEditorRoot;
    @FXML
    public AnchorPane scenarioContentEditorAnchorPane;
    @FXML
    public Button showCurrentAtomicActivitiesButton;
    @FXML
    public StyleClassedTextArea scenarioContentTextArea;

    private Scenario scenario;

    @Inject
    public ScenarioContentEditorController(IModelFactory modelFactory, LoggerService loggerService,
                                           MainWindowController mainWindowController) {
        this.modelFactory = modelFactory;
        this.loggerService = loggerService;
        this.mainWindowController = mainWindowController;
    }

    public void initialize() {
        scenarioContentEditorRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        scenarioContentTextArea.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        updateScenarioContentEditor();

        scenarioContentTextArea.getStylesheets()
                .add(ScenarioContentEditorController.class.getResource("custom-styles.css").toExternalForm());
    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateScenarioContentEditor();

        // clear all existing styles
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        spansBuilder.add(Collections.emptyList(), 0);
        StyleSpans<? extends Collection<String>> spans = spansBuilder.create();
        scenarioContentTextArea.clear();
        scenarioContentTextArea.setStyleSpans(0, spans);

        // show scenario content
        scenarioContentTextArea.appendText(scenario.getContent());

        // show existing atomic activities
        for (AtomicActivity atomicActivity : scenario.getAtomicActivities()) {
            markWordsInTextArea(atomicActivity.getContent(), List.of("own-bold"));
        }

        // ustawia tytuł panelu
        if (scenario.getScenarioName() != null && scenario.getScenarioName().length() > 35) {
            scenarioContentEditorRoot.setText(
                    "Scenario content (for '" + scenario.getScenarioName().substring(0, 34) + "...')");
        } else {
            scenarioContentEditorRoot.setText("Scenario content (for '" + scenario.getScenarioName() + "')");
        }

        loggerService.logInfo("Scenario set to ScenarioContentEditor - " + scenario.getId());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateScenarioContentEditor();
        scenarioContentTextArea.clear();
        scenarioContentEditorRoot.setText("Scenario content");
    }

    private void updateScenarioContentEditor() {
        // setting disable property of the scenarioContentEditorRoot TitledPane based on the value of the scenario variable
        scenarioContentEditorRoot.setDisable(scenario == null);

        // Set the background color using CSS
        if (scenario == null) {
            scenarioContentTextArea.setStyle("-fx-background-color: #F0F0F0;");
        } else {
            scenarioContentTextArea.setStyle("-fx-background-color: #FFFFFF;");
        }
    }

    @FXML
    public void showCurrentAtomicActivitiesButtonClicked() {
        String atomicActivitiesFromGeneralUseCase = checkIfScenarioIsInSpecificUseCase();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Atomic activities for Scenario - '" + scenario.getScenarioName() + "'");
        alert.setHeaderText(null);
        if (atomicActivitiesFromGeneralUseCase == null) {
            alert.setContentText(scenario.showAtomicActivities());
        } else {
            alert.setContentText(
                    scenario.showAtomicActivities() + "\nFrom general UseCase:\n" + atomicActivitiesFromGeneralUseCase);
        }
        alert.showAndWait();
    }

    private String checkIfScenarioIsInSpecificUseCase() {
        if (scenario.isMainScenario()) { // TODO tylko dla głównego scenariusza?
            UseCaseDiagram useCaseDiagram = mainWindowController.getCurrentProject().getUseCaseDiagram();
            UseCase specificUseCase =
                    mainWindowController.useCaseSelectorEditorController.getCurrentlySelectedUseCase();
            List<Relation> allRelations = useCaseDiagram.getRelations();
            List<Relation> genRelations = allRelations.stream()
                    .filter(r -> r.getType() == Relation.RelationType.GENERALIZATION &&
                            r.getFromId().equals(specificUseCase.getId())).toList();
            for (Relation r : genRelations) {
                Scenario generalUseCaseMainScenario = useCaseDiagram.getUseCaseFromId(r.getToId()).getMainScenario();
                return generalUseCaseMainScenario.showAtomicActivities();
            }
        }
        return null;
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
        Pattern pattern = Pattern.compile("(?<=\\s|^)" + Pattern.quote(wordToMark) + "(?=\\s|$)");
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
