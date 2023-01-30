package sl.fside.factories;

import sl.fside.model.*;
import sl.fside.persistence.repositories.*;
import sl.fside.services.*;
import com.google.inject.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

public class ModelFactory implements IModelFactory {

    private final ModelTrackerService modelTrackerService;
    private final IProjectRepository projectRepository;
//    private final IProjectNameRepository projectNameRepository;
    private final IImageRepository imageRepository;
    private final IAtomicActivityRepository atomicActivityRepository;
    private final LoggerService loggerService;

    @Inject
    public ModelFactory(ModelTrackerService modelTrackerService, IProjectRepository projectRepository,
//                        IProjectNameRepository projectNameRepository,
                        IImageRepository imageRepository, IAtomicActivityRepository atomicActivityRepository, LoggerService loggerService) {
        this.modelTrackerService = modelTrackerService;
        this.projectRepository = projectRepository;
//        this.projectNameRepository = projectNameRepository;
        this.imageRepository = imageRepository;
        this.atomicActivityRepository = atomicActivityRepository;
        this.loggerService = loggerService;
    }

    @Override
    public Project createProject(@NotNull String name) {

        System.out.println("begin create project");

        var project = new Project(UUID.randomUUID(), name);
//        project.setDirty();

//        createProjectName(project.getProjectId(), name);

        var atomicActivityCollection = createAtomicActivityCollection(project.getProjectId());
//        atomicActivityCollection.setDirty();
        project.setAtomicActivityCollectionId(atomicActivityCollection.getAtomicActivityCollectionId());
//        registerInModelTracker(project);
        projectRepository.add(project);
        return project;
    }

    @Override
    public Image createImage(UUID id, File file) {
        var img = new Image(id, file);
        imageRepository.add(img);
        return img;
    }

    @Override
    public AtomicActivity createAtomicActivity(Project project, String atomicActivity) {
        //noinspection OptionalGetWithoutIsPresent
//        return createAtomicActivity(atomicActivityRepository.getById(project.getAtomicActivityCollectionId()).get(), atomicActivity);
        return null;
    }

    @Override
    public AtomicActivity createAtomicActivity(AtomicActivityCollection atomicActivityCollection, String atomicActivity) {
        var newAtomicActivity = new AtomicActivity(UUID.randomUUID(), atomicActivity);
//        registerInModelTracker(newAtomicActivity);
//        atomicActivityCollection.addChild(newAtomicActivity);
        return newAtomicActivity;
    }

    @Override
    public PatternTemplate createPatternTemplate(String name, int inputs, int outputs) {
        var patternTemplate = new PatternTemplate(UUID.randomUUID(), name, inputs, outputs);
//        registerInModelTracker(patternTemplate);
        return patternTemplate;
    }

    @Override
    public PatternTemplateCollection createPatternTemplateCollection() {
        var patternTemplateCollection = new PatternTemplateCollection(UUID.randomUUID());
//        registerInModelTracker(patternTemplateCollection);
        return patternTemplateCollection;
    }

    @Override
    public UseCaseDiagram createUseCaseDiagram(Project parent, UUID id, UUID imageID){
        UseCaseDiagram useCaseDiagram = new UseCaseDiagram(id, imageID);
//        parent.addChild(useCaseDiagram);

//        registerInModelTracker(useCaseDiagram);
        return useCaseDiagram;
    }

    @Override
    public UseCase createUseCase(UseCaseDiagram useCaseDiagram, UUID id, String name, boolean isImported){
        UseCase useCase = new UseCase(id, name, isImported);
        useCaseDiagram.addUseCase(useCase);

        var mainScenario = createScenario(useCase, UUID.randomUUID(), true);
        useCase.addScenario(mainScenario);

//        registerInModelTracker(useCase);
        return useCase;
    }

    @Override
    public Scenario createScenario(UseCase parent, UUID id, boolean isMain){
        Scenario scenario = new Scenario(id, isMain);
//        parent.addChild(scenario);

//        registerInModelTracker(scenario);
        return scenario;
    }

    @Override
    public ActivityDiagram createActivityDiagram(Scenario parent, UUID id){
        ActivityDiagram activityDiagram = new ActivityDiagram(id);
//        parent.addChild(activityDiagram);

//        registerInModelTracker(activityDiagram);
        return activityDiagram;
    }

    @Override
    public Pattern createPattern(ActivityDiagram parent, UUID id, String name, UUID patternTemplateId){
        Pattern pattern = new Pattern(id, name, patternTemplateId);
//        parent.addChild(parent);

//        registerInModelTracker(pattern);
        return pattern;
    }

//    @Override
//    public void registerInModelTracker(@NotNull ModelBase item) {
//        item.subscribe(modelTrackerService);
//        loggerService.logDebug("Registered %s in ModelTrackerService.".formatted(item.toString()));
//    }

    private AtomicActivityCollection createAtomicActivityCollection(UUID projectId) {
        var atomicActivityCollection = new AtomicActivityCollection(UUID.randomUUID(), projectId);
//        registerInModelTracker(atomicActivityCollection);
//        atomicActivityRepository.add(atomicActivityCollection);
        return atomicActivityCollection;
    }

//    private ProjectName createProjectName(UUID projectId, String name) {
//        var projectName = new ProjectName(UUID.randomUUID(), projectId, name);
//        registerInModelTracker(projectName);
//        projectNameRepository.add(projectName);
//        return projectName;
//    }

}
