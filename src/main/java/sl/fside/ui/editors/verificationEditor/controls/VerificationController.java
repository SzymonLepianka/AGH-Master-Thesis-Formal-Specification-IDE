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
import org.apache.commons.lang3.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.services.docker_service.*;
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
                case "InKreSAT" -> throw new NotImplementedException("InKreSAT not implemented");
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
        writer.write("""
                formulas(sos).
                  all x all y (subset(x,y) <-> (all z (member(z,x) -> member(z,y)))).
                end_of_list.
                formulas(goals).
                  all x all y all z (subset(x,y) & subset(y,z) -> subset(x,z)).
                end_of_list.
                """);
//            writer.write(verification.getContent());
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
                begin_problem(Pelletier54).
                                
                list_of_descriptions.
                name({*Pelletier's Problem No. 54*}).
                author({*Christoph Weidenbach*}).
                status(unsatisfiable).
                description({*Problem taken in revised form from the "Pelletier Collection", Journal of Automated
                	Reasoning, Vol. 2, No. 2, pages 191-216*}).
                end_of_list.
                                
                list_of_symbols.
                  predicates[(F,2)].
                end_of_list.
                                
                list_of_formulae(axioms).
                                
                formula(forall([U],exists([V],forall([W],equiv(F(W,V),equal(W,U)))))).
                end_of_list.
                                
                list_of_formulae(conjectures).
                                
                formula(not(exists([U],forall([V],equiv(F(V,U),forall([W],implies(F(V,W),exists([X],and(F(X,W),not(exists([Y],and(F(Y,W),F(Y,X))))))))))))).
                                
                end_of_list.
                                
                end_problem.
                                
                """);
//            writer.write(verification.getContent());
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
    public void showResultButtonClicked() {
        Path outputFilePath =
                Path.of("prover_output/" + verification.getId() + "_" + verification.getProver().toLowerCase() +
                        "_output.txt");
        try {
            showProverResult(outputFilePath);
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
            showProverResult(logsFilePath);
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
        stage.setTitle("Formal Specification IDE - VerificationResult - " + verification.getProver());

        stage.show();

        final VerificationResultController controller = loader.getController();
        controller.setVerificationResult(fileContent, filePath);
    }

    private void showErrorMessage(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
