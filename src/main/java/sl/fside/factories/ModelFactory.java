package sl.fside.factories;

import com.google.inject.*;
import org.jetbrains.annotations.*;
import sl.fside.model.*;
import sl.fside.persistence.repositories.*;
import sl.fside.services.*;

import java.io.*;
import java.util.*;

public class ModelFactory implements IModelFactory {
    private final IProjectRepository projectRepository;
    private final IImageRepository imageRepository;
    private final IAtomicActivityRepository atomicActivityRepository;
    private final LoggerService loggerService;

    @Inject
    public ModelFactory(IProjectRepository projectRepository, IImageRepository imageRepository,
                        IAtomicActivityRepository atomicActivityRepository, LoggerService loggerService) {
        this.projectRepository = projectRepository;
        this.imageRepository = imageRepository;
        this.atomicActivityRepository = atomicActivityRepository;
        this.loggerService = loggerService;
    }

    @Override
    public Project createProject(@NotNull String name) {
        var project = new Project(UUID.randomUUID(), name);
        var atomicActivityCollection = createAtomicActivityCollection(project.getProjectId());
        project.setAtomicActivityCollectionId(atomicActivityCollection.getAtomicActivityCollectionId());
        projectRepository.add(project);
        loggerService.logInfo("New project created - " + project.getProjectId());
        return project;
    }

    @Override
    public Image createImage(UUID id, File file) {
        var img = new Image(id, file);
        imageRepository.add(img);
        return img;
    }

//    @Override
//    public AtomicActivity createAtomicActivity(AtomicActivityCollection atomicActivityCollection,
//                                               String atomicActivity) {
//        var newAtomicActivity = new AtomicActivity(UUID.randomUUID(), atomicActivity);
//        atomicActivityCollection.addChild(newAtomicActivity);
//        return newAtomicActivity;
//    }

    @Override
    public PatternTemplate createPatternTemplate(String name, int inputs, int outputs) {
        var patternTemplate = new PatternTemplate(UUID.randomUUID(), name, inputs, outputs);
        return patternTemplate;
    }

    @Override
    public PatternTemplateCollection createPatternTemplateCollection() {
        var patternTemplateCollection = new PatternTemplateCollection(UUID.randomUUID());
        return patternTemplateCollection;
    }

    @Override
    public UseCaseDiagram createUseCaseDiagram(Project parent, UUID useCaseDiagramId) {
        UseCaseDiagram useCaseDiagram = new UseCaseDiagram(useCaseDiagramId);
        parent.addUseCaseDiagram(useCaseDiagram);
        loggerService.logInfo("New UseCaseDiagram created - " + useCaseDiagram.getUseCaseDiagramId());
        return useCaseDiagram;
    }

    @Override
    public UseCase createUseCase(UseCaseDiagram useCaseDiagram, UUID id, String name, boolean isImported) {
        UseCase useCase = new UseCase(id, name, isImported);
        useCaseDiagram.addUseCase(useCase);
        var mainScenario = createScenario(useCase, UUID.randomUUID(), true);
        return useCase;
    }

    @Override
    public Scenario createScenario(UseCase useCase, UUID id, boolean isMain) {
        String scenarioName = isMain ? "Main Scenario for " + useCase.getUseCaseName() :
                "Additional Scenario for " + useCase.getUseCaseName();
        Scenario scenario = new Scenario(id, isMain, scenarioName);
        useCase.addScenario(scenario);
        return scenario;
    }

    @Override
    public Code createCode(Scenario scenario, UUID id) {
        Code code = new Code(id);
        scenario.addCode(code);
        return code;
    }

    @Override
    public Requirement createRequirement(Scenario scenario, UUID id) {
        int newSerialNumber = scenario.getMaxRequirementSerialNumber() + 1;
        Requirement requirement = new Requirement(id, "R" + newSerialNumber);
        scenario.addRequirement(requirement);
        return requirement;
    }

    @Override
    public Verification createVerification(Scenario scenario, UUID id) {
        Verification verification = new Verification(id);
        scenario.addVerification(verification);
        return verification;
    }

    @Override
    public Relation createRelation(UseCaseDiagram useCaseDiagram, UUID relationId, UUID fromId, UUID toId,
                                   Relation.RelationType type) {
        Relation relation = new Relation(relationId, fromId, toId, type);
        useCaseDiagram.addRelation(relation);
        return relation;
    }

    @Override
    public AtomicActivity createAtomicActivity(Scenario scenario, String atomicActivityContent) {
        var newAtomicActivity = new AtomicActivity(UUID.randomUUID(), atomicActivityContent);
        scenario.addAtomicActivity(newAtomicActivity);
        return newAtomicActivity;
    }

    private AtomicActivityCollection createAtomicActivityCollection(UUID projectId) {
        var atomicActivityCollection = new AtomicActivityCollection(UUID.randomUUID(), projectId);
//        atomicActivityRepository.add(atomicActivityCollection);
        return atomicActivityCollection;
    }
}
