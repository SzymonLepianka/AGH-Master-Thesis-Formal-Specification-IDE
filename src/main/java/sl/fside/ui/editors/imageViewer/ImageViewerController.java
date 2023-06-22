package sl.fside.ui.editors.imageViewer;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.stage.*;
import sl.fside.model.*;
import sl.fside.services.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ImageViewerController {

    private final LoggerService loggerService;
    @FXML
    public AnchorPane imageViewerAnchorPane;
    @FXML
    public Button addButton;
    @FXML
    public Button removeButton;
    @FXML
    private AnchorPane imageViewerRoot;

    private Project project;

    @Inject
    public ImageViewerController(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public void initialize() {
        imageViewerRoot.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        updateImageViewer();
    }

    @FXML
    public void addImageClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image file");

        // Set extension filter
        FileChooser.ExtensionFilter imageExtFilter =
                new FileChooser.ExtensionFilter("PNG/JPG images", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().addAll(imageExtFilter);

        // show file chooser
        File selectedFile = fileChooser.showOpenDialog(imageViewerRoot.getScene().getWindow());

        // get image
        if (selectedFile == null) return;
        Image image = new Image(selectedFile.toURI().toString());

        // save image to project
        try {
            byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
            String imageBase64 = Base64.getEncoder().encodeToString(fileContent);
            project.addImage(imageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage(e.getMessage());
            return;
        }

        // scale and display image
        ImageView imageView = scaleImage(image);
        addImageViewToPane(imageView);

        // hide add-button and show remove-button
        addButton.setVisible(false);
        removeButton.setVisible(true);

        loggerService.logInfo("Image added to project - " + project.getProjectId());
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error during adding image");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private ImageView scaleImage(Image image) {

        // podczas ładowania projektu przy starcie, na tym etapie width i height == 0.0
        double imageViewerAnchorPaneWidth =
                imageViewerAnchorPane.getWidth() == 0.0 ? imageViewerAnchorPane.getPrefWidth() :
                        imageViewerAnchorPane.getWidth();
        double imageViewerAnchorPaneHeight =
                imageViewerAnchorPane.getHeight() == 0.0 ? imageViewerAnchorPane.getPrefHeight() :
                        imageViewerAnchorPane.getHeight();

        ImageView imageView = new ImageView();
        double v1 = imageViewerAnchorPaneWidth / image.getWidth();
        double v2 = imageViewerAnchorPaneHeight / image.getHeight();
        double newWidth;
        double newHeight;
        if (v1 < v2) {
            newWidth = image.getWidth() * v1;
            newHeight = image.getHeight() * v1;
        } else {
            newWidth = image.getWidth() * v2;
            newHeight = image.getHeight() * v2;
        }
        imageView.setFitWidth(newWidth - 10);
        imageView.setFitHeight(newHeight - 10);

        imageView.setX((imageViewerAnchorPaneWidth - imageView.getBoundsInParent().getWidth()) / 2);
        imageView.setY((imageViewerAnchorPaneHeight - imageView.getBoundsInParent().getHeight()) / 2);

        imageView.setImage(image);
        return imageView;
    }

    private void addImageViewToPane(ImageView imageView) {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(imageView);
        borderPane.setPrefWidth(imageView.getFitWidth());
        borderPane.setPrefHeight(imageView.getFitHeight());

        borderPane.setBorder(new Border(
                new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        // Add a mouse click event listener to the image view
        imageView.setOnMouseClicked(event -> {
            showImageDetailsView(imageView, borderPane);
        });

        imageViewerAnchorPane.getChildren().add(borderPane);

        // podczas ładowania projektu przy starcie, na tym etapie width i height == 0.0
        double imageViewerAnchorPaneWidth =
                imageViewerAnchorPane.getWidth() == 0.0 ? imageViewerAnchorPane.getPrefWidth() :
                        imageViewerAnchorPane.getWidth();
        double imageViewerAnchorPaneHeight =
                imageViewerAnchorPane.getHeight() == 0.0 ? imageViewerAnchorPane.getPrefHeight() :
                        imageViewerAnchorPane.getHeight();

        AnchorPane.setLeftAnchor(borderPane, 0.5 * (imageViewerAnchorPaneWidth - imageView.getFitWidth() - 5));
        AnchorPane.setTopAnchor(borderPane, 0.5 * (imageViewerAnchorPaneHeight - imageView.getFitHeight() - 5));
    }

    private void showImageDetailsView(ImageView imageView, BorderPane borderPane) {
        if (borderPane.getBorder().getStrokes().get(0).getTopStroke().equals(Color.GRAY)) {
            borderPane.setBorder(new Border(
                    new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        } else {
            borderPane.setBorder(new Border(
                    new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        }

        // Create a new stage for the larger image view
        Stage stage = new Stage();
        stage.setTitle("Image Viewer");

        // Make the stage modal (it disables clicking other windows)
        stage.initModality(Modality.APPLICATION_MODAL);

        // create larger image view
        var view = new ImageView(imageView.getImage());
        view.setPreserveRatio(true);

        // scale image to be max 1000.0
        double imageScale;
        if (imageView.getImage().getWidth() > imageView.getImage().getHeight()) {
            imageScale = imageView.getImage().getWidth() / 1000.0;
            view.setFitWidth(1000.0);
            view.setFitHeight(imageView.getImage().getHeight() / imageScale);
        } else {
            imageScale = imageView.getImage().getHeight() / 1000.0;
            view.setFitWidth(imageView.getImage().getWidth() / imageScale);
            view.setFitHeight(1000.0);
        }

        // cursor appearance
        view.setOnMouseEntered(e -> {
            view.setCursor(Cursor.OPEN_HAND);
        });
        view.setOnMousePressed(e -> {
            view.setCursor(Cursor.CLOSED_HAND);
        });
        view.setOnMouseReleased(e -> {
            view.setCursor(Cursor.OPEN_HAND);
        });
        view.setOnMouseExited(e -> {
            view.setCursor(Cursor.DEFAULT);
        });

        // enable scrolling, dragging, zooming
        var sp = new ScrollPane(view);
        sp.setPannable(true);
        sp.setHvalue(0.5);
        sp.setVvalue(0.5);
        var slider = new Slider(1.0, 3.0, 1.0);
        slider.setBlockIncrement(0.1);
        slider.prefHeight(100);
        slider.valueProperty().addListener((o, oldV, newV) -> {
            var x = sp.getHvalue();
            var y = sp.getVvalue();
            view.setFitWidth(imageView.getImage().getWidth() / imageScale * newV.doubleValue());
            view.setFitHeight(imageView.getImage().getHeight() / imageScale * newV.doubleValue());
            sp.setHvalue(x);
            sp.setVvalue(y);
        });

        // Create a new scene and set it as the scene for the stage
        var root = new BorderPane(sp);
        root.setBottom(slider);
        Scene scene = new Scene(root, view.getFitWidth(), view.getFitHeight());
        stage.setScene(scene);

        // Show the stage
        stage.show();
    }

    @FXML
    public void removeImageClicked() {
        imageViewerAnchorPane.getChildren().clear();
        project.removeImage();

        // hide remove-button and show add-button
        removeButton.setVisible(false);
        addButton.setVisible(true);

        loggerService.logInfo("Image removed from project - " + project.getProjectId());
    }

    public void setProjectSelection(Project project) {
        this.project = project;
        updateImageViewer();

        // ustawia obraz, jeśli projekt go posiada
        if (project.getImage() != null) {

            // get image from project
            byte[] imageBytes = Base64.getDecoder().decode(project.getImage()); // działa bez "data:image/png;base64,"

            // scale and display image
            ImageView imageView = scaleImage(new Image(new ByteArrayInputStream(imageBytes)));
            addImageViewToPane(imageView);

            // hide add-button and show remove-button
            addButton.setVisible(false);
            removeButton.setVisible(true);
            loggerService.logInfo("Image set to ImageViewer - projectId=" + project.getProjectId());
        } else {
            // show add-button and remove remove-button
            addButton.setVisible(true);
            removeButton.setVisible(false);
            imageViewerAnchorPane.getChildren().clear();
            loggerService.logInfo("Project (" + project.getProjectId() + ") has no image");
        }
    }

    private void updateImageViewer() {
        // setting disable property of the imageViewerRoot TitledPane based on the value of the project variable
        imageViewerRoot.setDisable(project == null);
    }
}
