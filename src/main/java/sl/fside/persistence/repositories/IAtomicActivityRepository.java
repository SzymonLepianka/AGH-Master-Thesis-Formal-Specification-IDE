package sl.fside.persistence.repositories;

import sl.fside.model.*;

import java.util.*;

public interface IAtomicActivityRepository extends IAggregateRepository<AtomicActivityCollection> {

    void saveByProject(Project project);

    void saveByProjectId(UUID projectId);

    List<AtomicActivity> getAllAtomicActivitiesInProject(Project project);

    List<AtomicActivity> getAllAtomicActivitiesInProject(UUID projectId);

    Optional<AtomicActivity> getAtomicActivityById(Project project, UUID atomicActivityId);

    Optional<AtomicActivity> getAtomicActivityById(UUID projectId, UUID atomicActivityId);

    void removeAtomicActivity(Project project, AtomicActivity atomicActivity);

    void removeAtomicActivity(UUID projectId, AtomicActivity atomicActivity);
}
