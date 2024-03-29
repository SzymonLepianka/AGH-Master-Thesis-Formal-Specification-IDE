package sl.fside.ui.editors.verificationEditor.controls;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.stage.*;
import javafx.util.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.services.docker_service.*;
import sl.fside.services.logic_formula_generator.*;
import sl.fside.ui.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;


public class VerificationController {
    private final LoggerService loggerService;
    private final DockerService dockerService;
    private final MainWindowController mainWindowController;
    @FXML
    public AnchorPane verificationRoot;
    @FXML
    public TextArea textArea;
    @FXML
    public Button removeButton;
    @FXML
    public Button sendToProverButton;
    @FXML
    public ComboBox<String> proverComboBox;
    @FXML
    public Button showInputButton;
    @FXML
    public Button showResultButton;
    @FXML
    public Button showLogsButton;
    private Verification verification;
    private Function<Pair<AnchorPane, VerificationController>, Void> onRemoveClicked;

    @Inject
    public VerificationController(LoggerService loggerService, DockerService dockerService,
                                  MainWindowController mainWindowController) {
        this.loggerService = loggerService;
        this.dockerService = dockerService;
        this.mainWindowController = mainWindowController;
    }

    public void load(Verification verification) {
        this.verification = verification;
        verificationRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        proverComboBox.getItems().addAll("SPASS", "Prover9", "InKreSAT");

        // ustawia treść weryfikacji (jeśli istnieje)
        if (verification.getContent() != null) {
            textArea.setText(verification.getContent());
        }

        // ustawia wybrany prover (jeśli wybrano)
        if (verification.getProver() != null) {
            proverComboBox.getSelectionModel().select(verification.getProver());
        }

        // ustawia możliwość wyświetlenia wyniku
        if (verification.isResultGenerated()) {
            sendToProverButton.setText("Sent!");
            textArea.setDisable(true);
            sendToProverButton.setDisable(true);
            proverComboBox.setDisable(true);
            showResultButton.setDisable(false);
            showLogsButton.setDisable(false);
        } else {
            showResultButton.setDisable(true);
            showLogsButton.setDisable(true);
        }
    }

    public void initialize() {
    }

    @FXML
    public void onRemoveButtonClicked() {
        if (onRemoveClicked != null) onRemoveClicked.apply(new Pair<>(verificationRoot, this));
    }

    public void setOnRemoveClicked(Function<Pair<AnchorPane, VerificationController>, Void> onRemoveClicked) {
        this.onRemoveClicked = onRemoveClicked;
    }

    public Verification getVerification() {
        return verification;
    }

    @FXML
    public void onChooseProverComboBoxClicked() {
        String selectedItem = proverComboBox.getSelectionModel().getSelectedItem();
        verification.setProver(selectedItem);
    }

    @FXML
    private void verificationContentChanged() {
        verification.setContent(textArea.getText());
    }

    @FXML
    public void sendToProverButtonClicked() {
        if (mainWindowController.resultsPanelController.getCurrentScenario() == null) {
            showErrorMessage("Error during sending to prover!", "Scenario is null! Nigdy nie powinien się tu znaleźć!");
            return;
        }
        if (verification.getProver() == null) {
            showErrorMessage("Error during sending to prover!", "Prover not set!");
            return;
        }
        if (verification.getContent() == null) {
            showErrorMessage("Error during sending to prover!", "Content not set!");
            return;
        }

        try {
            switch (verification.getProver()) {
                case "Prover9" -> {
                    Path inputFilePath = createSpassInput();
                    Path convertedInputFilePath = dockerService.convertInputFromSpassToProver9(inputFilePath);
                    dockerService.executeProver9Command(convertedInputFilePath);
                }
                case "SPASS" -> {
                    Path inputFilePath = createSpassInput();
                    dockerService.executeSpassCommand(inputFilePath);
                }
                case "InKreSAT" -> {
                    Path inputFilePath = createInkresatInput();
                    dockerService.executeInkresatCommand(inputFilePath);
                }
                default -> throw new Exception("Unknown prover name: " + verification.getProver());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error during sending to prover!", e.getMessage());
            return;
        }

        verification.setResultGenerated(true);
        sendToProverButton.setText("Sent!");
        textArea.setDisable(true);
        sendToProverButton.setDisable(true);
        proverComboBox.setDisable(true);
        showResultButton.setDisable(false);
        showLogsButton.setDisable(false);
    }

    private Path createProver9Input() throws Exception {

        // Create the folder path
        String folderPath = "prover_input/";
        checkIfFolderExists(folderPath);
        Path inputFilePath = Path.of(folderPath + verification.getId() + "_" + verification.getProver().toLowerCase() +
                "_input.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath.toString()));
        writer.write("""
                formulas(sos).
                  exists x (arg0(x)).
                  all x (arg0(x) -> exists y (arg1(y))).
                  all x all y (-(arg0(x) & arg1(y))).
                end_of_list.
                """);
//        writer.write(proverInput.toString());
        writer.flush();
        return inputFilePath;
    }

    private Path createSpassInput() throws Exception {

        Scenario scenario = mainWindowController.resultsPanelController.getCurrentScenario();

        // weź aktywności ze scenariusza + te z wyrażenia
        List<String> atomicActivities =
                scenario.getAtomicActivities().stream().map(AtomicActivity::getContent).toList();
        List<String> atomicActivitiesFromPatternExpression =
                scenario.getPatternExpression().getAtomicActivitiesFromPE();
        List<String> allAtomicActivities = new ArrayList<>();
        allAtomicActivities.addAll(atomicActivities);
        allAtomicActivities.addAll(atomicActivitiesFromPatternExpression);
        allAtomicActivities = new ArrayList<>(new HashSet<>(allAtomicActivities)); // removes duplicates

        // stwórz formuły na podstawie verificationContent
        String content = verification.getContent().replace(" ", ""); // remove spaces form content
        List<String> elementsAfterImplication = Arrays.stream(content.split("=>")).toList();

        // check if '=>' appears only ones
        if (elementsAfterImplication.size() > 2) {
            throw new Exception("'=>' appears more than once");
        }

        List<String> formulaAxioms = new ArrayList<>();
        List<String> formulaConjectures = new ArrayList<>();
        for (int i = 0; i < elementsAfterImplication.size(); i++) {
            String elementAfterImplication = elementsAfterImplication.get(i);
            List<String> elementsAfterConjunction = Arrays.stream(elementAfterImplication.split("\\^")).toList();
            for (String elementAfterConjunction : elementsAfterConjunction) {
                if (elementAfterConjunction.equals("FOL")) {
                    List<String> folFormulas = scenario.getFolLogicalSpecification();
                    if (folFormulas == null) {
                        throw new Exception("FOL Logical Specification was not generated");
                    }
                    folFormulas = folFormulas.stream().map(f -> f.replace(" ", "")).toList(); // remove spaces

                    if (i == 0) {
                        formulaAxioms.addAll(folFormulas);
                    } else {
                        formulaConjectures.addAll(folFormulas);
                    }
                } else {

                    // weź wszystkie Requirements z FOL i niepustą treścią
                    List<Requirement> folRequirements = scenario.getRequirements().stream()
                            .filter(r -> r.getLogic() != null && r.getLogic().equals("First Order Logic") &&
                                    r.getContent() != null && !r.getContent().isEmpty()).toList();
                    Requirement requirement =
                            folRequirements.stream().filter(r -> r.getName().equals(elementAfterConjunction))
                                    .findFirst().orElseThrow(() -> {
                                        StringBuilder errorMsg = new StringBuilder(
                                                "Unknown requirement '" + elementAfterConjunction +
                                                        "' for FOL! Available:\n- 'FOL'\n");
                                        if (!folRequirements.isEmpty()) {
                                            for (Requirement r : folRequirements) {
                                                errorMsg.append("- '").append(r.getName()).append("'\n");
                                            }
                                        }
                                        errorMsg.append("- '=>'\n");
                                        errorMsg.append("- '^'");
                                        return new Exception(errorMsg.toString());
                                    });
                    if (i == 0) {
                        formulaAxioms.add(requirement.getContent());
                    } else {
                        formulaConjectures.add(requirement.getContent());
                    }
                }
            }
        }

        StringBuilder proverInput = new StringBuilder();
        proverInput.append("begin_problem(FSIDE).\n");
        proverInput.append("\n");
        proverInput.append("list_of_descriptions.\n");
        proverInput.append("name({*FSIDE-name*}).\n");
        proverInput.append("author({*SL*}).\n");
        proverInput.append("status(unsatisfiable).\n");
        proverInput.append("description({*SPASS test*}).\n");
        proverInput.append("end_of_list.\n");
        proverInput.append("\n");
        proverInput.append("list_of_symbols.\n");
        proverInput.append("predicates[(");

        for (String atomicActivity : allAtomicActivities) {
            if (!atomicActivity.contains(
                    "<")) { // nie dodawaj aktywności związanych z relacjami, np. <<include>>create_order
                proverInput.append(atomicActivity);
                proverInput.append(", 1), (");
                proverInput.append(atomicActivity).append("Plus");
                proverInput.append(", 1), (");
                proverInput.append(atomicActivity).append("Minus");
                proverInput.append(", 1), (");
            }
        }
        proverInput.deleteCharAt(proverInput.length() - 1);
        proverInput.deleteCharAt(proverInput.length() - 1);
        proverInput.deleteCharAt(proverInput.length() - 1);
        proverInput.append("].\n");
        proverInput.append("end_of_list.\n");
        proverInput.append("\n");
        proverInput.append("list_of_formulae(axioms).\n");
        formulaAxioms = replacePipeWithOr(formulaAxioms);
        for (String axiom : formulaAxioms) {
            proverInput.append("formula(");
            proverInput.append(axiom);
            proverInput.append(").\n");
        }
        proverInput.append("end_of_list.\n");
        proverInput.append("\n");
        if (!formulaConjectures.isEmpty()) {
            proverInput.append("list_of_formulae(conjectures).\n");
            for (String conjecture : formulaConjectures) {
                proverInput.append("formula(");
                proverInput.append(conjecture);
                proverInput.append(").\n");
            }
            proverInput.append("end_of_list.\n");
            proverInput.append("\n");
        }
        proverInput.append("end_problem.\n");

        // Create the folder path
        String folderPath = "prover_input/";
        checkIfFolderExists(folderPath);
        Path inputFilePath = Path.of(folderPath + verification.getId() + "_" + verification.getProver().toLowerCase() +
                "_input.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath.toString()));
//        writer.write("""
//                begin_problem(FSIDE).
//
//                list_of_descriptions.
//                name({*FSIDE-SPASS*}).
//                author({*SL*}).
//                status(unsatisfiable).
//                description({*SPASS test*}).
//                end_of_list.
//
//                list_of_symbols.
//                predicates[(arg0, 1), (arg1, 1)].
//                end_of_list.
//
//                list_of_formulae(axioms).
//                formula(exists([T], arg0(T))).
//                formula(forall([T], implies(arg0(T), exists([T1], arg1(T1))))).
//                formula(forall([T], not(and(arg0(T), arg1(T))))).
//                end_of_list.
//
//                end_problem.
//
//                """);
        writer.write(proverInput.toString());
        writer.flush();
        return inputFilePath;
    }

    private Path createInkresatInput() throws Exception {
        Scenario scenario = mainWindowController.resultsPanelController.getCurrentScenario();

        // stwórz formuły na podstawie verificationContent
        String content = verification.getContent().replace(" ", ""); // remove spaces form content
        List<String> elementsAfterImplication = Arrays.stream(content.split("=>")).toList();

        // check if '=>' appears only ones
        if (elementsAfterImplication.size() > 2) {
            throw new Exception("'=>' appears more than once");
        }

        List<String> formulaAxioms = new ArrayList<>();
        List<String> formulaConjectures = new ArrayList<>();
        for (int i = 0; i < elementsAfterImplication.size(); i++) {
            String elementAfterImplication = elementsAfterImplication.get(i);
            List<String> elementsAfterConjunction = Arrays.stream(elementAfterImplication.split("\\^")).toList();
            for (String elementAfterConjunction : elementsAfterConjunction) {
                if (elementAfterConjunction.equals("LTL")) {
                    List<String> ltlFormulas = scenario.getLtlLogicalSpecification();
                    if (ltlFormulas == null) {
                        throw new Exception("LTL Logical Specification was not generated");
                    }
                    ltlFormulas = ltlFormulas.stream().map(f -> f.replace(" ", "")).toList(); // remove spaces
                    if (i == 0) {
                        formulaAxioms.addAll(ltlFormulas);
                    } else {
                        formulaConjectures.addAll(ltlFormulas);
                    }
                } else {

                    // weź wszystkie Requirements z LTL i niepustą treścią
                    List<Requirement> ltlRequirements = scenario.getRequirements().stream()
                            .filter(r -> r.getLogic() != null && r.getLogic().equals("Linear Temporal Logic") &&
                                    r.getContent() != null && !r.getContent().isEmpty()).toList();
                    Requirement requirement =
                            ltlRequirements.stream().filter(r -> r.getName().equals(elementAfterConjunction))
                                    .findFirst().orElseThrow(() -> {
                                        StringBuilder errorMsg = new StringBuilder(
                                                "Unknown requirement '" + elementAfterConjunction +
                                                        "' for LTL! Available:\n- 'LTL'\n");
                                        if (!ltlRequirements.isEmpty()) {
                                            for (Requirement r : ltlRequirements) {
                                                errorMsg.append("- '").append(r.getName()).append("'\n");
                                            }
                                        }
                                        errorMsg.append("- '=>'\n");
                                        errorMsg.append("- '^'");
                                        return new Exception(errorMsg.toString());
                                    });
                    if (i == 0) {
                        formulaAxioms.add(requirement.getContent());
                    } else {
                        formulaConjectures.add(requirement.getContent());
                    }
                }
            }
        }
        List<String> convertedFormulaAxioms = changeLtlToInkresatSyntax(formulaAxioms);
        List<String> convertedFormulaConjectures = changeLtlToInkresatSyntax(formulaConjectures);
        StringBuilder proverInput = new StringBuilder();
        proverInput.append("begin\n");
        for (String convertedFormula : convertedFormulaAxioms) {
            proverInput.append(convertedFormula);
            proverInput.append("\n& ");
        }
        if (!convertedFormulaConjectures.isEmpty()) {
            proverInput.append("(");
        }
        for (String convertedFormula : convertedFormulaConjectures) {
            proverInput.append("~");
            proverInput.append(convertedFormula);
            proverInput.append(" | ");
        }
        if (!convertedFormulaConjectures.isEmpty()) {
            proverInput.delete(proverInput.length() - 3, proverInput.length());
            proverInput.append(")\n");
        } else {
            proverInput.delete(proverInput.length() - 2, proverInput.length());
        }
        proverInput.append("end\n");

        // Create the folder path
        String folderPath = "prover_input/";
        checkIfFolderExists(folderPath);
        Path inputFilePath = Path.of(folderPath + verification.getId() + "_" + verification.getProver().toLowerCase() +
                "_input.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath.toString()));
//        writer.write("""
//                begin
//                [](~[]<>p | ~[](~p | []q))
//                end
//                """);
//        writer.write("""
//                begin
//                <>(arg0)
//                & [](arg0 -> <>(arg1))
//                & [](~(arg0 & arg1))
//                end
//                """);
//        writer.write("""
//                begin
//                [](~(ui1 & ui3))
//                & [](ui1 -> <>(ui2))
//                & [](ui1 -> <>(ui3))
//                & [](ui1 -> (<>(ui2) & ~(<>(ui3)) | (~(<>(ui2)) & <>(ui3))))
//                & <>(ui1)
//                & [](~(ui1 & ui2))
//                & (-<>(ui1) | -<>(ui2))
//                end
//                """);
        writer.write(proverInput.toString());
        writer.flush();
        return inputFilePath;
    }

    private List<String> changeLtlToInkresatSyntax(List<String> formulas) {
        List<String> results = new ArrayList<>();
        for (String formula : formulas) {
            String newResult = formula;
            newResult = newResult.replace("ForAll", "[]");
            newResult = newResult.replace("Exist", "<>");
            newResult = newResult.replace("=>", " -> ");
            newResult = newResult.replace("^", " & ");
            newResult = newResult.replace("|", " | ");
            results.add(newResult);
        }
        return results;
    }

    private List<String> replacePipeWithOr(List<String> formulas) {
        List<String> formulasFixed = new ArrayList<>();
        for (String formula : formulas) {
            String[] split = formula.split("\\|");
            if (split.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < split.length - 1; i++) {
                    int idx = Math.max(split[i].lastIndexOf("("), split[i].lastIndexOf(","));
                    int idx2 = split[i + 1].indexOf("(");
                    int idx3 = split[i + 1].indexOf(")");
                    sb.append(split[i], 0, idx + 1);
                    sb.append("or(");
                    sb.append(split[i], idx + 1, split[i].length());
                    sb.append(split[i + 1], idx2, idx3 + 1);
                    sb.append(",");
                    split[i + 1] = split[i + 1].substring(0, idx3 + 1) + ")" + split[i + 1].substring(idx3 + 1);
                }
                sb.append(split[split.length - 1]);
                formulasFixed.add(sb.toString());
            } else {
                formulasFixed.add(formula);
            }
        }
        return formulasFixed;
    }

    private void checkIfFolderExists(String folderPath) throws Exception {
        // Create the folder if it doesn't exist
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                // Handle the case when folder creation fails
                throw new Exception("Failed to create the folder " + folderPath);
            }
        }
    }

    @FXML
    public void showInputButtonClicked() {

        if (verification.getProver() == null) {
            showErrorMessage("Error showing prover input!", "Prover not set!");
            return;
        }
        if (verification.getContent() == null) {
            showErrorMessage("Error showing prover input!", "Content not set!");
            return;
        }

        Path inputFilePath;
        try {
            switch (verification.getProver()) {
                case "Prover9" -> {
                    Path spassInputFilePath = createSpassInput();
                    inputFilePath = dockerService.convertInputFromSpassToProver9(spassInputFilePath);
                }
                case "SPASS" -> inputFilePath = createSpassInput();
                case "InKreSAT" -> inputFilePath = createInkresatInput();
                default -> throw new Exception("Unknown prover name: " + verification.getProver());
            }
            showProverData(inputFilePath, "Input", "Prover input saved to: " + inputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error showing prover input!", e.getMessage());
            return;
        }
        loggerService.logInfo("VerificationResult window opened");
    }

    @FXML
    public void showResultButtonClicked() {
        Path outputFilePath =
                Path.of("prover_output/" + verification.getId() + "_" + verification.getProver().toLowerCase() +
                        "_output.txt");
        try {
            showProverData(outputFilePath, "Result", "Output saved to: " + outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error during showing result!", e.getMessage());
            return;
        }
        loggerService.logInfo("VerificationResult window opened");
    }

    @FXML
    public void showLogsButtonClicked() {
        Path logsFilePath =
                Path.of("prover_logs/" + verification.getId() + "_" + verification.getProver().toLowerCase() +
                        "_logs.txt");
        try {
            showProverData(logsFilePath, "Logs", "Logs saved to: " + logsFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error during showing result!", e.getMessage());
            return;
        }
        loggerService.logInfo("VerificationResult window opened");
    }

    private void showProverData(Path filePath, String type, String title) throws Exception {
        String fileContent = Files.readString(filePath);

        var stage = new Stage();
        final var loader = new FXMLLoader(VerificationResultController.class.getResource("VerificationResult.fxml"));
        final Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Scene scene = new Scene(root, 800, 600);

        // Make the stage modal (it disables clicking other windows)
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.setTitle("Formal Specification IDE - Verification " + type + " - " + verification.getProver());

        stage.show();

        final VerificationResultController controller = loader.getController();
        controller.setVerificationResult(fileContent, title);
    }

    private void showWarningMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Formal Specification IDE");
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
