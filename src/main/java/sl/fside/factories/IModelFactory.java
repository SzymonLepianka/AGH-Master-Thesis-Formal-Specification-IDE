package sl.fside.factories;

import org.jetbrains.annotations.*;
import sl.fside.model.*;

import java.util.*;

public interface IModelFactory {
    Project createProject(@NotNull String name);

    UseCaseDiagram createUseCaseDiagram(Project parent, UUID useCaseDiagramId);

    UseCase createUseCase(UseCaseDiagram parent, UUID id, String name, boolean isImported);

    Relation createRelation(UseCaseDiagram parent, UUID relationId, UUID fromId, UUID toId, Relation.RelationType type);

    Scenario createScenario(UseCase parent, UUID id, boolean isMain);

    AtomicActivity createAtomicActivity(Scenario scenario, String atomicActivityContent);

    Code createCode(Scenario parent, UUID id);

    Requirement createRequirement(Scenario parent, UUID id);

    Verification createVerification(Scenario parent, UUID id);
}
