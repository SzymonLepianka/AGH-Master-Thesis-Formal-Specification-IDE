package sl.fside.persistence.repositories;

import com.google.inject.*;
import org.jetbrains.annotations.*;
import sl.fside.model.*;
import sl.fside.persistence.*;
import sl.fside.services.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

class ProjectRepository implements IProjectRepository {

    private final IPersistenceHelper persistenceHelper;
    private final LoggerService loggerService;

    private final Set<Project> projects;

    @Inject
    public ProjectRepository(IPersistenceHelper persistenceHelper, LoggerService loggerService) {
        this.persistenceHelper = persistenceHelper;
        this.loggerService = loggerService;
        projects = new HashSet<>();
    }

    @Override
    public void add(@NotNull Project item) {
        loggerService.logInfo("Project added - " + item.getProjectId());
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

    @Override
    public void remove(@NotNull Project item) {
        // TODO implement removing
        loggerService.logError("Not implemented! (remove)");
//        var projectId = item.getId().toString();
//        var file = persistenceHelper.getAllProjectFiles().stream().filter(x -> projectId.equals(IPersistenceHelper.getFileNameWithoutExtension(x))).findFirst();
//        file.ifPresent(persistenceHelper::removeFile);
        projects.remove(item);
    }

    @Override
    public void save(@NotNull Project project) {
        persistenceHelper.saveProjectFile(project);
    }

    private void loadProject(File file) {
        var projectId = IPersistenceHelper.getFileNameWithoutExtension(file);
        if (projects.stream().anyMatch(x -> x.getProjectId().toString().equals(projectId))) {
            loggerService.logWarning("Project already loaded - " + projectId);
            return;
        }
        var newProject = persistenceHelper.loadFile(file, Project.class); // odczyt projektu z pliku
        projects.add(newProject);
        loggerService.logInfo("Successfully loaded a project - " + projectId);
    }

    @Override
    public Project getById(UUID projectId) {
        return getAll().stream().filter(x -> x.getProjectId().equals(projectId)).findFirst().orElseThrow();
    }
}
