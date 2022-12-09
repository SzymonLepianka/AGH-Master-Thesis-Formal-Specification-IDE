package sl.fside.ui;

import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.persistence.repositories.*;
import sl.fside.services.*;
import sl.fside.ui.editors.actionEditor.*;
import sl.fside.ui.editors.activityDiagramEditor.*;
import sl.fside.ui.editors.imageViewer.*;
import sl.fside.ui.editors.scenarioSelector.*;
import sl.fside.ui.editors.useCaseSelector.*;
import sl.fside.ui.events.*;
import com.google.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.*;

import java.io.*;
import java.util.*;

public class MainWindowController implements IController {
    private final XmlParserService xmlParserService;
    private final IModelFactory modelFactory;
    private final IProjectRepository projectRepository;
    private final IProjectNameRepository projectNameRepository;
    private final EventAggregatorService eventAggregatorService;

    private Project project;
    
    @FXML
    private UseCaseSelectorEditorController useCaseSelectorEditorController;

    @FXML
    public ScenarioSelectorEditorController scenarioSelectorEditorController;
    
    @FXML
    public ActionEditorController actionEditorController;

    @FXML
    public ImageViewerController imageViewerController;

    @Inject
    public MainWindowController(XmlParserService xmlParserService,
                                IModelFactory modelFactory,
                                IProjectRepository projectRepository,
                                IProjectNameRepository projectNameRepository,
                                EventAggregatorService eventAggregatorService) {

        this.xmlParserService = xmlParserService;
        this.modelFactory = modelFactory;
        this.projectRepository = projectRepository;
        this.projectNameRepository = projectNameRepository;
        this.eventAggregatorService = eventAggregatorService;
    }

    @Override
    public void load(ModelBase object) {
        if (object instanceof Project project) {
            this.project = project;
            eventAggregatorService.publish(new ProjectLoadedEvent(this, project));
        }
        else 
            throw new UnsupportedOperationException();
    }

    @Override
    public void unload() {
        project = null;
        // TODO Publish new event - ProjectUnloaded
    }
    
    @FXML
    private void saveClicked() {
        projectRepository.save(project);
        projectNameRepository.saveAll();
    }

    @FXML
    private void loadClicked() {


        
        var projectChooserDialog = new ChoiceDialog<ProjectNamePresenter>();
        projectChooserDialog.getItems().addAll(projectNameRepository.getAll().stream().map(ProjectNamePresenter::new).toList());
        projectChooserDialog.setTitle("Load a project");
        projectChooserDialog.setContentText("Select:");
        projectChooserDialog.showAndWait();
        
        var result = projectChooserDialog.getResult();
        if (result == null)
            return;
        
        var project = projectRepository.getById(result.projectName().getProjectId());

        project.ifPresent(this::load);
    }
    
    @FXML
    private void importXml() {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select an input file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML Filter", "xml"));
        var file = fileChooser.showOpenDialog(null);
        if (file == null)
            return;
        
        var textInputDialog = new TextInputDialog("Enter a project name");
        textInputDialog.showAndWait();
        var projectName = textInputDialog.getResult();
        if (projectName == null)
            return;
        
        var project = modelFactory.createProject(projectName);
        var useCaseDiagram = modelFactory.createUseCaseDiagram(project, UUID.randomUUID(), null);
        xmlParserService.parseXml(useCaseDiagram, file);

        load(project);
    }
    
    @FXML
    private void startActivityDiagramClicked() {
        var stage = new Stage();
//        final var location = getClass().getClassLoader().getResource("bgs/formalspecificationide/ui/editors/activityDiagramEditor/ActivityDiagramEditor.fxml");
        final var loader = new FXMLLoader(ActivityDiagramEditorController.class.getResource("ActivityDiagramEditor.fxml"));
        final Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Scene scene = new Scene(root, 830, 630);

        scene.getStylesheets().add(ActivityDiagramEditorController.class.getResource("demo.css").toExternalForm());
        Font.loadFont(ActivityDiagramEditorController.class.getResource("fontawesome.ttf").toExternalForm(), 12);

        stage.setScene(scene);
        stage.setTitle("Results");

        stage.show();

        final ActivityDiagramEditorController controller = loader.getController();
        controller.panToCenter();
    }

    private record ProjectNamePresenter(ProjectName projectName) {
        @Override
        public String toString() {
            return projectName.getProjectName();
        }
    }
}
