package sl.fside.ui.editors.verificationEditor.controls;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import sl.fside.model.*;
import sl.fside.services.*;
import sl.fside.services.docker_service.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;


public class VerificationController {
    private final XmlParserService xmlParserService;
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
    public VerificationController(XmlParserService xmlParserService, DockerService dockerService) {
        this.xmlParserService = xmlParserService;
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
            showErrorMessage("Prover not set!");
            return;
        }
        if (verification.getContent() == null) {
            showErrorMessage("Content not set!");
            return;
        }

        System.out.println(verification.getContent());
        System.out.println(verification.getProver());
        System.out.println(verification.getId());

        // Create the folder path
        String folderPath = "prover_input/";

        // Create the folder if it doesn't exist
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                // Handle the case when folder creation fails
                showErrorMessage("Failed to create the folder " + folderPath);
                return;
            }
        }

        Path inputFilePath = Path.of(folderPath + verification.getId() + "_" + verification.getProver().toLowerCase() +
                "_input.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath.toString()))) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dockerService.executeProver9Command(inputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e.getMessage());
            return;
        }

        //TODO get output

        sendToProverButton.setText("Sent to " + verification.getProver() + "!");
        sendToProverButton.setDisable(true);

        sendToProverButton.setPrefWidth(120.0);
        proverComboBox.setVisible(false);
        showResultButton.setVisible(true);
    }

    @FXML
    public void showResultButtonClicked() {
        //TODO
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error during sending to prover!");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
