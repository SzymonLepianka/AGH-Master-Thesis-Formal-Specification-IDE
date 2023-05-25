package sl.fside.ui;

import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.*;
import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.persistence.repositories.*;
import sl.fside.services.*;
import sl.fside.services.docker_service.*;
import sl.fside.ui.editors.activityDiagramPanel.*;
import sl.fside.ui.editors.generateCodePanel.*;
import sl.fside.ui.editors.imageViewer.*;
import sl.fside.ui.editors.requirementEditor.*;
import sl.fside.ui.editors.resultsPanel.*;
import sl.fside.ui.editors.scenarioContentEditor.*;
import sl.fside.ui.editors.scenarioSelector.*;
import sl.fside.ui.editors.useCaseSelector.*;
import sl.fside.ui.editors.verificationEditor.*;

import java.util.*;

public class MainWindowController {
    private final XmlParserService xmlParserService;
    private final DockerService dockerService;
    private final LoggerService loggerService;
    private final IModelFactory modelFactory;
    private final IProjectRepository projectRepository;
    @FXML
    public AnchorPane mainWindowRoot;
    @FXML
    public ImageViewerController imageViewerController;
    @FXML
    public ScenarioSelectorEditorController scenarioSelectorEditorController;
    @FXML
    public ScenarioContentEditorController scenarioContentEditorController;
    @FXML
    public ActivityDiagramPanelController activityDiagramPanelController;
    @FXML
    public ResultsPanelController resultsPanelController;
    @FXML
    public GenerateCodePanelController generateCodePanelController;
    @FXML
    public RequirementEditorController requirementEditorController;
    @FXML
    public VerificationEditorController verificationEditorController;
    @FXML
    public UseCaseSelectorEditorController useCaseSelectorEditorController;
    @FXML
    public RadioMenuItem codeGenerationPanelVisibilityButton;
    private Project project;
    private Stage stage;


    @Inject
    public MainWindowController(XmlParserService xmlParserService, LoggerService loggerService,
                                IModelFactory modelFactory, IProjectRepository projectRepository,
                                DockerService dockerService) {

        this.xmlParserService = xmlParserService;
        this.loggerService = loggerService;
        this.modelFactory = modelFactory;
        this.projectRepository = projectRepository;
        this.dockerService = dockerService;
    }

    public void load(Project project) {
        this.project = project;
        useCaseSelectorEditorController.setUseCaseDiagramSelection(project.getUseCaseDiagram(),
                scenarioSelectorEditorController, scenarioContentEditorController, activityDiagramPanelController,
                resultsPanelController, generateCodePanelController, requirementEditorController,
                verificationEditorController);
        imageViewerController.setProjectSelection(project);
        stage.setTitle("Formal Specification IDE - " + project.getProjectName());

        // set visibility of code generation panel
        codeGenerationPanelVisibilityButton.setSelected(project.isCodeGenerationPanelVisible());
        generateCodePanelController.generateCodePanelRoot.setVisible(project.isCodeGenerationPanelVisible());

        loggerService.logInfo("Project set to MainWindow - " + project.getProjectId());
    }

    @FXML
    private void saveProjectClicked() {
        if (project == null) {
            showWarningMessage("No project is currently loaded!");
        } else {
            projectRepository.save(project);
        }
    }

    public void initialize() {
        stage.setTitle("Formal Specification IDE - No project selected");
        dockerService.initialize();
        chooseProjectOnStart();

        // Dodanie obsługi zdarzenia zamknięcia okna
        stage.setOnCloseRequest(event -> {
            if (alertToSaveProject()) {
                stage.close();
                event.consume();
            } else {
                event.consume();
            }
        });
    }

    private void chooseProjectOnStart() {

        // pomiń dialog, jeśli nie ma żadnego projektu w repository
        if (projectRepository.getAll().isEmpty()) {
            loggerService.logInfo("No projects in repository");
            return;
        }

        var projectChooserDialog = new ChoiceDialog<ProjectNamePresenter>();
        projectChooserDialog.getItems()
                .addAll(projectRepository.getAll().stream().map(ProjectNamePresenter::new).toList());
        projectChooserDialog.setTitle("Formal Specification IDE");
        projectChooserDialog.setHeaderText("Open a project");
        projectChooserDialog.setContentText("Select:");
        projectChooserDialog.showAndWait();

        var result = projectChooserDialog.getResult();
        if (result == null) {
            loggerService.logInfo("No project selected");
            return;
        }

        var project = projectRepository.getById(result.project().getProjectId());
        load(project);
    }

    private boolean alertToSaveProject() {
        // TODO dodać warunek czy projekt został zmodyfikowany względem zapisanego
        if (project != null) {// && project.isModified()) {
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Formal Specification IDE");
            alert.setHeaderText("Do you want to save changes to project '" + project.getProjectName() + "'?");
            alert.setContentText(
                    "Click:\n - YES to save changes,\n - NO to continue without saving,\n - CANCEL to stay in the program.");

            // własne typy button, ponieważ domyślne zachowanie było nieodpowiednie
            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            ButtonType cancelButton = new ButtonType("Cancel");
            alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

            // Show the dialog and wait for a response
            Optional<ButtonType> result = alert.showAndWait();
            // If the user clicked NO --> without saving
            if (result.isPresent() && result.get() == yesButton) {
                // If the user clicked YES, save project
                projectRepository.save(project);
                return true;
            } else return result.isPresent() && result.get() == noButton;
        }
        return true;
    }

    @FXML
    private void openProjectClicked() {

        // Show alert z pytaniem, czy zapisać projekt. Zwróci false, jeśli żądanie zostało anulowane.
        if (!alertToSaveProject()) return;

        var projectChooserDialog = new ChoiceDialog<ProjectNamePresenter>();
        projectChooserDialog.getItems()
                .addAll(projectRepository.getAll().stream().map(ProjectNamePresenter::new).toList());
        projectChooserDialog.setTitle("Open a project");
        projectChooserDialog.setHeaderText("Choose a project");
        projectChooserDialog.setContentText("Select:");
        projectChooserDialog.showAndWait();

        var result = projectChooserDialog.getResult();
        if (result == null) return;

        var selectedProject = projectRepository.getById(result.project().getProjectId());
        if (selectedProject.equals(this.project)) {
            showWarningMessage("Selected project is currently loaded!");
            loggerService.logWarning("Selected project is currently loaded! - " + selectedProject.getProjectId());
        } else {
            load(selectedProject);
        }
    }

    private void showWarningMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Formal Specification IDE");
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void newProjectClicked() {

        // Show alert z pytaniem, czy zapisać projekt. Zwróci false, jeśli żądanie zostało anulowane.
        if (!alertToSaveProject()) return;

        loggerService.logInfo("Creating new project...");

        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select an input XML file");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog, with MainWindow blocked
        var file = fileChooser.showOpenDialog(mainWindowRoot.getScene().getWindow());
        if (file == null) {
            loggerService.logInfo("Abort creating new project... (No file selected)");
            return;
        }

        // Show input project name dialog
        var projectNameInputDialog = new TextInputDialog("project_name");
        projectNameInputDialog.setTitle("Creating project...");
        projectNameInputDialog.setHeaderText("Enter a project name");

        // disable empty project name
        projectNameInputDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(projectNameInputDialog.getEditor().textProperty().isEmpty());

        projectNameInputDialog.showAndWait();

        // get project name from input dialog
        var projectName = projectNameInputDialog.getResult();
        if (projectName == null) {
            loggerService.logInfo("Abort creating new project... (No project name selected)");
            return;
        }

        var project = modelFactory.createProject(projectName);
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID());

        try {
            xmlParserService.parseXml(useCaseDiagram, file);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e.getMessage());
            return;
        }

        load(project);
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error during adding image");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public Project getCurrentProject() {
        return project;
    }

    @FXML
    public void codeGenerationPanelVisibilityButtonClicked() {
        if (project != null) {
            project.setCodeGenerationPanelVisible(codeGenerationPanelVisibilityButton.isSelected());
            generateCodePanelController.generateCodePanelRoot.setVisible(
                    codeGenerationPanelVisibilityButton.isSelected());
        } else {
            codeGenerationPanelVisibilityButton.setSelected(false);
        }
    }

    private record ProjectNamePresenter(Project project) {
        @Override
        public String toString() {
            return project.getProjectName();
        }
    }
}
