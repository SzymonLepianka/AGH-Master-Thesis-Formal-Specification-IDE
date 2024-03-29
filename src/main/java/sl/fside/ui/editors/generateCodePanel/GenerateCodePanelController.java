package sl.fside.ui.editors.generateCodePanel;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.stage.*;
import javafx.util.*;
import org.json.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.ui.*;
import sl.fside.ui.editors.generateCodePanel.controls.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static sl.fside.services.code_generator1.functions.GenJava.*;
import static sl.fside.services.code_generator2.compiler.Compiler.*;
import static sl.fside.services.code_generator1.functions.GenPython.*;

public class GenerateCodePanelController {

    private final IModelFactory modelFactory;
    private final UIElementsFactory uiElementsFactory;
    private final LoggerService loggerService;
    private final String CONFIG_FILENAME = "config.json";
    private final List<Pair<AnchorPane, CodeController>> uiElementCodePairs = new ArrayList<>();
    @FXML
    public TitledPane generateCodePanelRoot;
    @FXML
    public AnchorPane generateCodePanelAnchorPane;
    @FXML
    public Button generateJavaButton;
    @FXML
    public Button generatePythonButton;
    @FXML
    public ListView<AnchorPane> codesList;
    @FXML
    public Button addCodeButton;
    private String codeGeneratorType;
    private Scenario scenario;

    @Inject
    public GenerateCodePanelController(IModelFactory modelFactory, UIElementsFactory uiElementsFactory,
                                       LoggerService loggerService) {
        this.modelFactory = modelFactory;
        this.uiElementsFactory = uiElementsFactory;
        this.loggerService = loggerService;
    }

    public void initialize() {

        generateCodePanelRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

//        activityDiagramPanelAnchorPane.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//
//        openActivityDiagramEditorButton.setBorder(new Border(
//                new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        updateGenerateCodePanel();
        setCodeGeneratorType();
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    private void setCodeGeneratorType() {
        // get code generator type from config
        try {
            String content = Files.readString(Paths.get(CONFIG_FILENAME));
            JSONObject jsonObject = new JSONObject(content);
            String codeGeneratorType = jsonObject.getString("code_generator_type");
            if (codeGeneratorType.equals("v2")) {
                this.codeGeneratorType = "v2";
            } else {
                this.codeGeneratorType = "v1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.codeGeneratorType = "v1";
        }
    }

    @FXML
    public void addCodeButtonClicked() {
        if (scenario != null) {

            // creating new code
            Code newCode = modelFactory.createCode(scenario, UUID.randomUUID());
            Pair<AnchorPane, CodeController> uiElementPair = uiElementsFactory.createCode(newCode);
            uiElementCodePairs.add(uiElementPair);

            // ustawia AtomicActivities do wyboru w Code
            uiElementPair.getValue().setAtomicActivitiesToComboBox(
                    scenario.getAtomicActivities().stream().map(AtomicActivity::getContent).toList());

            // usuwanie Code
            uiElementPair.getValue().setOnRemoveClicked(this::removeCode);

            // dodaje nowy Code do obecnych
            codesList.getItems().add(uiElementPair.getKey());
        } else {
            System.out.println("addCodeButtonClicked - Nigdy nie powinien się tu znaleźć");
        }

    }

    public void setScenarioSelection(Scenario scenario) {
        this.scenario = scenario;
        updateGenerateCodePanel();
        codesList.getItems().clear();

        // create new codes
        var codePairs = scenario.getCodes().stream().map(uiElementsFactory::createCode).toList();
        uiElementCodePairs.clear();
        uiElementCodePairs.addAll(codePairs);

        // ustawia AtomicActivities do wyboru w Codes
        uiElementCodePairs.forEach(pair -> pair.getValue().setAtomicActivitiesToComboBox(
                scenario.getAtomicActivities().stream().map(AtomicActivity::getContent).toList()));

        // usuwanie Code
        uiElementCodePairs.forEach(pair -> pair.getValue().setOnRemoveClicked(this::removeCode));

        // add new codes to list
        codesList.getItems().addAll(codePairs.stream().map(Pair::getKey).toList());

        loggerService.logInfo("Scenario set to GenerateCodePanel - " + scenario.getId());
    }

    public void removeScenarioSelection() {
        this.scenario = null;
        updateGenerateCodePanel();
        codesList.getItems().clear();
        uiElementCodePairs.clear();
    }

    private void updateGenerateCodePanel() {
        // setting disable property of the generateCodePanelRoot TitledPane based on the value of the scenario variable
        generateCodePanelRoot.setDisable(scenario == null);
    }

    private Void removeCode(Pair<AnchorPane, CodeController> pair) {
        codesList.getItems().remove(pair.getKey());
        scenario.removeCode(pair.getValue().getCode());
        uiElementCodePairs.remove(pair);

        loggerService.logInfo("Code removed - " + pair.getValue().getCode().getId());
        return null;
    }

    @FXML
    public void generateJavaButtonClicked() {

        // kontrola czy kod może być wygenerowany
        if (scenario == null) {
            showMessage("Scenario is not selected (It should never occur)", Alert.AlertType.WARNING);
            return;
        }
        if (scenario.getPatternExpression() == null) {
            showMessage("No PatternExpression defined!", Alert.AlertType.WARNING);
            return;
        }
        try {
            checkIfGeneratedCodeFolderExists();
            String javaPE =
                    replaceCodeInPatternExpression(scenario.getPatternExpression().getPeWithProcessedNesting(), "Java");
            String javaCode;
            if (codeGeneratorType != null && codeGeneratorType.equals("v2")) {
                javaCode = compile(javaPE, Language.JAVA);
                javaCode = addFunctionNames(javaCode, "Java");
                saveGeneratedCodeToFile("codeJava_" + UUID.randomUUID() + ".java", javaCode);
            } else {
                javaCode = genJava(javaPE, UUID.randomUUID().toString());
            }
            showGeneratedCode(javaCode, "Java");
            loggerService.logInfo("Java code generated - (scenarioId=" + scenario.getId() + ")");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void generatePythonButtonClicked() {

        // kontrola czy kod może być wygenerowany
        if (scenario == null) {
            showMessage("Scenario is not selected (It should never occur)", Alert.AlertType.WARNING);
            return;
        }
        if (scenario.getPatternExpression() == null) {
            showMessage("No PatternExpression defined!", Alert.AlertType.WARNING);
            return;
        }
        try {
            checkIfGeneratedCodeFolderExists();
            String pythonPE =
                    replaceCodeInPatternExpression(scenario.getPatternExpression().getPeWithProcessedNesting(),
                            "Python");
            String pythonCode;
            if (codeGeneratorType != null && codeGeneratorType.equals("v2")) {
                pythonCode = compile(pythonPE, Language.PYTHON);
                pythonCode = addFunctionNames(pythonCode, "Python");
                saveGeneratedCodeToFile("codePython_" + UUID.randomUUID() + ".py", pythonCode);
            } else {
                pythonCode = genPython(pythonPE, UUID.randomUUID().toString());
            }
            showGeneratedCode(pythonCode, "Python");
            loggerService.logInfo("Python code generated - (scenarioId=" + scenario.getId() + ")");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String addFunctionNames(String code, String language) {
        List<Code> languageCodes = scenario.getCodes().stream()
                .filter(c -> c.getLanguage() != null && c.getLanguage().equals(language) && c.getCode().endsWith("()"))
                .toList();
        for (Code c : languageCodes) {
            code = code.replaceFirst(" \\(\\)", " " + c.getCode());
        }
        return code;
    }

    private void saveGeneratedCodeToFile(String filename, String code) throws Exception {
        FileWriter writerTxt = new FileWriter("generated_code/" + filename);
        writerTxt.write(code);
        writerTxt.close();
        loggerService.logInfo("Generated code has been saved to file generated_code/" + filename);
    }

    private void checkIfGeneratedCodeFolderExists() throws Exception {
        // Create generated_code folder if it doesn't exist
        File generatedCodeFolder = new File("generated_code/");
        if (!generatedCodeFolder.exists()) {
            boolean created = generatedCodeFolder.mkdirs();
            if (!created) {
                // Handle the case when folder creation fails
                throw new Exception("Failed to create the folder generated_code/");
            }
        }
    }

    // TODO kontrola że zamieniany string jest całą aktywnością
    private String replaceCodeInPatternExpression(String patternExpression, String language) {
        String languagePatternExpression = patternExpression;
        List<Code> languageCodes =
                scenario.getCodes().stream().filter(c -> c.getLanguage() != null && c.getLanguage().equals(language))
                        .toList();
        for (Code code : languageCodes) {
            languagePatternExpression =
                    languagePatternExpression.replace(code.getAtomicActivity() + ",", code.getCode() + ",");
            languagePatternExpression =
                    languagePatternExpression.replace(code.getAtomicActivity() + ")", code.getCode() + ")");
        }
        return languagePatternExpression;
    }

    private void showMessage(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Formal Specification IDE");
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showGeneratedCode(String code, String language) {
        var stage = new Stage();
        final var loader = new FXMLLoader(GeneratedCodeController.class.getResource("GeneratedCode.fxml"));
        final Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Scene scene = new Scene(root, 600, 500);

        // Make the stage modal (it disables clicking other windows)
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.setTitle("Formal Specification IDE - Generated code - " + language);

        stage.show();

        final GeneratedCodeController controller = loader.getController();
        controller.setCode(code, language);

        loggerService.logInfo("GeneratedCode window opened");
    }
}
