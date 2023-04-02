package sl.fside.factories;

import sl.fside.model.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

public interface IModelFactory {
    Project createProject(@NotNull String name);

    Image createImage(UUID id, File file);

    UseCaseDiagram createUseCaseDiagram(Project parent, UUID useCaseDiagramId);

    UseCase createUseCase(UseCaseDiagram parent, UUID id, String name, boolean isImported);

    Scenario createScenario(UseCase parent, UUID id, boolean isMain);

//    Action createAction(Scenario parent, UUID id, String actionContent);

//    ActivityDiagram createActivityDiagram(Scenario parent, UUID id);

//    Pattern createPattern(ActivityDiagram parent, UUID id, String name, UUID patternTemplateId);

//    AtomicActivity createAtomicActivity(Project project, String atomicActivity);
    AtomicActivity createAtomicActivity(Scenario scenario, String atomicActivityContent);

//    AtomicActivity createAtomicActivity(AtomicActivityCollection atomicActivityCollection, String atomicActivity);

    PatternTemplate createPatternTemplate(String name, int inputs, int outputs);

    PatternTemplateCollection createPatternTemplateCollection();

//    void registerInModelTracker(@NotNull ModelBase item);
}
