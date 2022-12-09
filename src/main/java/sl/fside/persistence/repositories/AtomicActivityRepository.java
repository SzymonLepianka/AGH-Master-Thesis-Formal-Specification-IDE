package sl.fside.persistence.repositories;

import sl.fside.model.*;
import sl.fside.persistence.*;
import sl.fside.services.*;
import com.google.inject.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

class AtomicActivityRepository implements IAtomicActivityRepository {

    private final IPersistenceHelper persistenceHelper;
    private final LoggerService loggerService;
    private final IProjectRepository projectRepository;
    private final List<AtomicActivityCollection> atomicActivityCollections;

    @Inject
    public AtomicActivityRepository(IPersistenceHelper persistenceHelper, LoggerService loggerService, IProjectRepository projectRepository) {
        this.loggerService = loggerService;
        this.projectRepository = projectRepository;
        atomicActivityCollections = new ArrayList<>();
        this.persistenceHelper = persistenceHelper;
    }

    @Override
    public Optional<AtomicActivityCollection> getById(UUID id) {
        var loaded = atomicActivityCollections.stream().filter(x -> x.getId().equals(id)).findFirst();
        if (loaded.isPresent())
            return loaded;
        return loadAtomicActivitiesCollection(id);
    }

    @Override
    public void saveByProject(Project project) {
        saveByProjectId(project.getId());
    }

    @Override
    public void saveByProjectId(UUID projectId) {
        var atomicActivitiesCollection = atomicActivityCollections.stream().filter(x -> x.getProjectId().equals(projectId)).findFirst();
        atomicActivitiesCollection.ifPresent(this::save);
    }

    @Override
    public List<AtomicActivity> getAllAtomicActivitiesInProject(Project project) {
        return getById(project.getAtomicActivityCollectionId()).map(AtomicActivityCollection::getAtomicActivities).orElse(new ArrayList<>());
    }

    @Override
    public List<AtomicActivity> getAllAtomicActivitiesInProject(UUID projectId) {
        var project = projectRepository.getById(projectId);
        return project.map(this::getAllAtomicActivitiesInProject).orElse(new ArrayList<>());
    }

    @Override
    public Optional<AtomicActivity> getAtomicActivityById(Project project, UUID atomicActivityId) {
        var collection = getById(project.getAtomicActivityCollectionId());
        if (collection.isPresent())
            return collection.get().getAtomicActivityById(atomicActivityId);
        return Optional.empty();
    }

    @Override
    public Optional<AtomicActivity> getAtomicActivityById(UUID projectId, UUID atomicActivityId) {
        var project = projectRepository.getById(projectId);
        if (project.isPresent())
            return getAtomicActivityById(project.get(), atomicActivityId);
        return Optional.empty();
    }

    @Override
    public void removeAtomicActivity(Project project, AtomicActivity atomicActivity) {
        var collection = getById(project.getAtomicActivityCollectionId());
        collection.ifPresent(atomicActivityCollection -> atomicActivityCollection.removeChild(atomicActivity));
    }

    @Override
    public void removeAtomicActivity(UUID projectId, AtomicActivity atomicActivity) {
        var project = projectRepository.getById(projectId);
        project.ifPresent(value -> removeAtomicActivity(value, atomicActivity));
    }

    @Override
    public void add(@NotNull AtomicActivityCollection item) {
        atomicActivityCollections.add(item);
    }

    @Override
    public List<AtomicActivityCollection> getAll() {
        for (var file : persistenceHelper.getAllAtomicActivityCollectionFiles()) {
            loadAtomicActivitiesCollection(file);
        }

        return atomicActivityCollections.stream().toList();
    }

    @Override
    public List<AtomicActivityCollection> get(Predicate<AtomicActivityCollection> predicate) {
        return getAll().stream().filter(predicate).toList();
    }

    @Override
    public void remove(@NotNull AtomicActivityCollection item) {
        var id = item.getId();
        var file = persistenceHelper.getAllAtomicActivityCollectionFiles().stream().filter(x -> id.equals(UUID.fromString(IPersistenceHelper.getFileNameWithoutExtension(x)))).findFirst();
        file.ifPresent(persistenceHelper::removeFile);
        atomicActivityCollections.remove(item);
    }

    @Override
    public void saveAll() {
        for (var atomicActivitiesCollection : atomicActivityCollections)
            save(atomicActivitiesCollection);
    }

    @Override
    public void save(@NotNull AtomicActivityCollection item) {
        if (item.isDirty()) {
            persistenceHelper.saveAtomicActivityCollectionFile(item);
            item.clearIsDirty();
        }
    }

    private Optional<AtomicActivityCollection> loadAtomicActivitiesCollection(UUID id) {
        var file = persistenceHelper.getAllAtomicActivityCollectionFiles().stream().filter(x -> UUID.fromString(IPersistenceHelper.getFileNameWithoutExtension(x)).equals(id)).findFirst();
        return file.map(this::loadAtomicActivitiesCollection).or(Optional::empty);
    }

    private AtomicActivityCollection loadAtomicActivitiesCollection(File file) {
        var collectionId = UUID.fromString(IPersistenceHelper.getFileNameWithoutExtension(file));
        var collection = atomicActivityCollections.stream().filter(x -> x.getId().equals(collectionId)).findFirst();
        if (collection.isPresent()) {
            return collection.get();
        }

        var newCollection = persistenceHelper.loadFile(file, AtomicActivityCollection.class);

        loggerService.logInfo("Successfully loaded an Atomic Activity Collection");
        atomicActivityCollections.add(newCollection);
        return newCollection;
    }
}
