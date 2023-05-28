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

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;


public class VerificationController {
    private final LoggerService loggerService;
    private final DockerService dockerService;
    @FXML
    public AnchorPane verificationRoot;
    @FXML
    public Button removeButton;
    @FXML
    public TextArea textArea;
    @FXML
    public ComboBox<String> proverComboBox;
    @FXML
    public Button sendToProverButton;
    @FXML
    public Button showResultButton;
    private Verification verification;
    private Function<Pair<AnchorPane, VerificationController>, Void> onRemoveClicked;

    @Inject
    public VerificationController(LoggerService loggerService, DockerService dockerService) {
        this.loggerService = loggerService;
        this.dockerService = dockerService;
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
            sendToProverButton.setText("Sent to " + verification.getProver() + "!");
            sendToProverButton.setDisable(true);
            sendToProverButton.setPrefWidth(120.0);
            proverComboBox.setVisible(false);
            showResultButton.setVisible(true);
        }
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
        sendToProverButton.setText("Sent to " + verification.getProver() + "!");
        sendToProverButton.setDisable(true);
        sendToProverButton.setPrefWidth(120.0);
        proverComboBox.setVisible(false);
        showResultButton.setVisible(true);
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

        // check if verification output exists
        Path outputFilePath =
                Path.of("prover_output/" + verification.getId() + "_" + verification.getProver().toLowerCase() +
                        "_output.txt");

        String fileContent;
        try {
            fileContent = Files.readString(outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage("Error during showing result!", e.getMessage());
            return;
        }

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
        controller.setVerificationResult(fileContent, outputFilePath);

        loggerService.logInfo("GeneratedCode window opened");
    }

    private void showErrorMessage(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
