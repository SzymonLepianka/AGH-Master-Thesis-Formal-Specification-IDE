package sl.fside.factories;

import com.google.inject.*;
import org.jetbrains.annotations.*;
import sl.fside.model.*;
import sl.fside.persistence.repositories.*;
import sl.fside.services.*;

import java.util.*;

public class ModelFactory implements IModelFactory {
    private final IProjectRepository projectRepository;
    private final LoggerService loggerService;

    @Inject
    public ModelFactory(IProjectRepository projectRepository, LoggerService loggerService) {
        this.projectRepository = projectRepository;
        this.loggerService = loggerService;
    }

    @Override
    public Project createProject(@NotNull String name) {
        var project = new Project(UUID.randomUUID(), name);
        projectRepository.add(project);
        loggerService.logInfo("New project created - " + project.getProjectId());
        return project;
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
        createScenario(useCase, UUID.randomUUID(), true);
        return useCase;
    }

    @Override
    public Relation createRelation(UseCaseDiagram useCaseDiagram, UUID relationId, UUID fromId, UUID toId,
                                   Relation.RelationType type) {
        Relation relation = new Relation(relationId, fromId, toId, type);
        useCaseDiagram.addRelation(relation);
        return relation;
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
    public AtomicActivity createAtomicActivity(Scenario scenario, String atomicActivityContent) {
        var newAtomicActivity = new AtomicActivity(UUID.randomUUID(), atomicActivityContent);
        scenario.addAtomicActivity(newAtomicActivity);
        return newAtomicActivity;
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
}
