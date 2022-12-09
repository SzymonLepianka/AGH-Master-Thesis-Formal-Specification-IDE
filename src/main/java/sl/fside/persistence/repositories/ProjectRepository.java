package sl.fside.persistence.repositories;

import sl.fside.model.*;
import sl.fside.persistence.*;
import sl.fside.services.*;
import com.google.inject.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

class ProjectRepository implements IProjectRepository {

    private final IPersistenceHelper persistenceHelper;
    private final IProjectNameRepository projectNameRepository;
    private final LoggerService loggerService;

    private final Set<Project> projects;

    @Inject
    public ProjectRepository(IPersistenceHelper persistenceHelper, IProjectNameRepository projectNameRepository, LoggerService loggerService) {
        this.persistenceHelper = persistenceHelper;
        this.projectNameRepository = projectNameRepository;
        this.loggerService = loggerService;
        projects = new HashSet<>();
    }

    @Override
    public List<String> getProjectNames() {
        return projectNameRepository.getAll().stream().map(ProjectName::getProjectName).toList();
    }

    @Override
    public void add(@NotNull Project item) {
        projects.add(item);
    }

    @Override
    public List<Project> getAll() {
        for (var file : persistenceHelper.getAllProjectFiles()) {
            loadProject(file);
        }

        return projects.stream().toList();
    }

    @Override
    public List<Project> get(Predicate<Project> predicate) {
        return getAll().stream().filter(predicate).toList();
    }

    /**
     * Gets a project by name.
     * @param name The project name.
     * @return The project or null if not found.
     */
    @Override
    public Optional<Project> getByName(String name) {
        var projectId = projectNameRepository.get(x -> x.getProjectName().equals(name)).stream().findFirst();
        if (projectId.isEmpty())
            return Optional.empty();

        var projectFiles = persistenceHelper.getAllProjectFiles();
        var foundProjectFile = projectFiles.stream().filter(x -> x.getName().equals(projectId.get().toString())).findFirst();
        return foundProjectFile.map(this::loadProject);
    }

    @Override
    public void remove(@NotNull Project item) {
        var projectId = item.getId().toString();
        var file = persistenceHelper.getAllProjectFiles().stream().filter(x -> projectId.equals(IPersistenceHelper.getFileNameWithoutExtension(x))).findFirst();
        file.ifPresent(persistenceHelper::removeFile);
        projects.remove(item);
    }

    @Override
    public void saveAll() {
        for (var project : projects) {
            save(project);
        }
    }

    @Override
    public void save(@NotNull Project item) {
        if (item.isDirty()) {
            persistenceHelper.saveProjectFile(item);
            item.clearIsDirty();
        }
    }

    private Project loadProject(File file) {
        var projectId = IPersistenceHelper.getFileNameWithoutExtension(file);
        if (projects.stream().anyMatch(x -> x.getId().toString().equals(projectId))) {
            //noinspection OptionalGetWithoutIsPresent
            return projects.stream().filter(x -> x.getId().toString().equals(projectId)).findFirst().get();
        }
        var newProject = persistenceHelper.loadFile(file, Project.class);

        loggerService.logInfo("Successfully loaded a project.");

        projects.add(newProject);
        return newProject;
    }

    @Override
    public Optional<Project> getById(UUID id) {
        return getAll().stream().filter(x -> x.getId().equals(id)).findFirst();
    }
}
