package sl.fside.ui.editors.verificationEditor.controls;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import sl.fside.model.Scenario;
import sl.fside.model.Verification;
import sl.fside.services.LoggerService;
import sl.fside.services.docker_service.DockerService;
import sl.fside.ui.MainWindowController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


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
            sendToProverButton.setDisable(true);
            proverComboBox.setDisable(true);
            showResultButton.setDisable(false);
            showLogsButton.setDisable(false);
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
                    Path inputFilePath = createProver9Input();
                    dockerService.executeProver9Command(inputFilePath);
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
        sendToProverButton.setDisable(true);
        proverComboBox.setDisable(true);
        showResultButton.setDisable(false);
        showLogsButton.setDisable(false);
    }

    private Path createProver9Input() throws Exception {

        Scenario scenario = mainWindowController.resultsPanelController.getCurrentScenario();
        if (scenario == null) {
            throw new Exception("Scenario is null! Nigdy nie powinien się tu znaleźć!");
        }
        String folLogicalSpecification = scenario.getFolLogicalSpecification();
        if (folLogicalSpecification == null) {
            throw new Exception("FOL Logical Specification was not generated");
        }

        List<String> convertedFormulas = changeFolToProver9Syntax(folLogicalSpecification);
        StringBuilder proverInput = new StringBuilder();
        proverInput.append("formulas(sos).\n  ");
        for (String convertedFormula : convertedFormulas) {
            proverInput.append(convertedFormula);
            proverInput.append(".\n  ");
        }
        proverInput.delete(proverInput.length() - 2, proverInput.length());
        proverInput.append("end_of_list.\n");

        // Create the folder path
        String folderPath = "prover_input/";
        checkIfFolderExists(folderPath);
        Path inputFilePath = Path.of(folderPath + verification.getId() + "_" + verification.getProver().toLowerCase() +
                "_input.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath.toString()));
//        writer.write("""
//                formulas(sos).
//                  exists x (arg0(x)).
//                  all x (arg0(x) -> exists y (arg1(y))).
//                  all x all y (-(arg0(x) & arg1(y))).
//                end_of_list.
//                """);
//        writer.write("""
//                formulas(sos).
//                  exists x (arg0(x)).
//                  all x all y all z (arg0(x) -> exists y (arg1(y)) & exists z (arg2(z))).
//                  all x all y all z (-(arg0(x) & arg1(y))).
//                  all x all y all z (-(arg0(x) & arg2(z))).
//                end_of_list.
//                """);
        writer.write("""
                formulas(sos).
                  all x1 (card1(x1) -> exists x2 (card2(x2))).
                  exists x1 (card1(x1)).
                  all x1 all x2( -(card1(x1) & card2(x2))).
                end_of_list.
                """);
//        writer.write(proverInput.toString());
        writer.flush();
        return inputFilePath;
    }

    private List<String> changeFolToProver9Syntax(String folLogicalSpecification) {
        folLogicalSpecification = folLogicalSpecification.substring(0,
                folLogicalSpecification.length() - 1); // Remove the last character (comma)
        folLogicalSpecification = folLogicalSpecification.replace(" ", ""); // remove spaces
        List<String> formulas = Arrays.stream(folLogicalSpecification.split(",")).toList();
        List<String> results = new ArrayList<>();
        for (String formula : formulas) {
            StringBuilder temp = new StringBuilder();
            StringBuilder result = new StringBuilder();
            int index = 0;
            for (var c : formula.toCharArray()) {
                if (!Character.isLetter(c) && !Character.isDigit(c) && temp.length() > 0) {
                    if (temp.toString().equals("ForAll")) {
                        index++;
                        result.append("all x").append(index).append(" ");
                        result.append(c);
                        temp = new StringBuilder();
                    } else if (temp.toString().equals("Exist")) {
                        index++;
                        result.append("exists x").append(index).append(" ");
                        result.append(c);
                        temp = new StringBuilder();
                    } else {
                        result.append(temp).append("(x").append(index).append(")");
                        result.append(c);
                        temp = new StringBuilder();
                    }
                } else if (Character.isLetter(c) || Character.isDigit(c)) {
                    temp.append(c);
                } else {
                    result.append(c);
                }
            }
            String newResult = result.toString();
            newResult = newResult.replace("=>", " -> ");
            newResult = newResult.replace("~", " -");
            newResult = newResult.replace("^", " & ");
            newResult = newResult.replace("|", " | ");
            results.add(newResult);
        }
        return results;
    }

    private Path createSpassInput() throws Exception {
        // Create the folder path
        String folderPath = "prover_input/";
        checkIfFolderExists(folderPath);
        Path inputFilePath = Path.of(folderPath + verification.getId() + "_" + verification.getProver().toLowerCase() +
                "_input.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath.toString()));
        writer.write("""
                begin_problem(FSIDE).
                    
                list_of_descriptions.
                name({*FSIDE-SPASS*}).
                author({*SL*}).
                status(unsatisfiable).
                description({*SPASS test*}).
                end_of_list.
                             
                list_of_symbols.
                predicates[(arg0, 1), (arg1, 1)].
                end_of_list.
                                
                list_of_formulae(axioms).
                formula(exists([T], arg0(T))).
                formula(forall([T], implies(arg0(T), exists([T1], arg1(T1))))).
                formula(forall([T], forall([T1], not(and(arg0(T), arg1(T1)))))).
                end_of_list.
                                
                end_problem.
                                
                """);
//            writer.write(verification.getContent());
        writer.flush();
        return inputFilePath;
    }

    private Path createInkresatInput() throws Exception {
        // Create the folder path
        String folderPath = "prover_input/";
        checkIfFolderExists(folderPath);
        Path inputFilePath = Path.of(folderPath + verification.getId() + "_" + verification.getProver().toLowerCase() +
                "_input.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath.toString()));
//            writer.write(verification.getContent());
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
        writer.flush();
        return inputFilePath;
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
                case "Prover9" -> inputFilePath = createProver9Input();
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

    private void showProverResult(Path filePath) throws Exception {
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

    private void showErrorMessage(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
