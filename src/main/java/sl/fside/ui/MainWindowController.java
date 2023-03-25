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
import sl.fside.ui.editors.actionEditor.*;
import sl.fside.ui.editors.activityDiagramPanel.*;
import sl.fside.ui.editors.imageViewer.*;
import sl.fside.ui.editors.resultsPanel.*;
import sl.fside.ui.editors.scenarioSelector.*;
import sl.fside.ui.editors.useCaseSelector.*;

import java.util.*;

public class MainWindowController {
    private final XmlParserService xmlParserService;
    private final LoggerService loggerService;
    private final IModelFactory modelFactory;
    private final IProjectRepository projectRepository;
    private final EventAggregatorService eventAggregatorService;
    @FXML
    public AnchorPane mainWindowRoot;
    @FXML
    public ImageViewerController imageViewerController;
    @FXML
    public ScenarioSelectorEditorController scenarioSelectorEditorController;
    @FXML
    public ActionEditorController actionEditorController;
    @FXML
    public ActivityDiagramPanelController activityDiagramPanelController;
    @FXML
    public ResultsPanelController resultsPanelController;
    @FXML
    private UseCaseSelectorEditorController useCaseSelectorEditorController;
    private Project project;
    private Stage stage;


    @Inject
    public MainWindowController(XmlParserService xmlParserService, LoggerService loggerService,
                                IModelFactory modelFactory, IProjectRepository projectRepository,
                                EventAggregatorService eventAggregatorService) {

        this.xmlParserService = xmlParserService;
        this.loggerService = loggerService;
        this.modelFactory = modelFactory;
        this.projectRepository = projectRepository;
        this.eventAggregatorService = eventAggregatorService;
    }

    public void load(Project project) {
        this.project = project;
        useCaseSelectorEditorController.setUseCaseDiagramSelection(project.getUseCaseDiagram(),
                scenarioSelectorEditorController, actionEditorController, activityDiagramPanelController,
                resultsPanelController);
        imageViewerController.setProjectSelection(project);
        stage.setTitle("Formal Specification IDE - " + project.getProjectName());
        loggerService.logInfo("Project set to MainWindow - " + project.getProjectId());
    }

    @FXML
    private void saveProjectClicked() {
        projectRepository.save(project);
    }

    public void initialize() {
        stage.setTitle("Formal Specification IDE - No project selected");
        chooseProjectOnStart();
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

    @FXML
    private void openProjectClicked() {
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
        xmlParserService.parseXml(useCaseDiagram, file);
        load(project);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    private record ProjectNamePresenter(Project project) {
        @Override
        public String toString() {
            return project.getProjectName();
        }
    }
}
