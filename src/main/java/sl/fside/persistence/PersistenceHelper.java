package sl.fside.persistence;

import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.services.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.*;
import com.google.inject.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

class PersistenceHelper implements IPersistenceHelper {

    private final Path persistenceDirectory = Paths.get("persistence").toAbsolutePath();

    private final Path projectDirectory = persistenceDirectory.resolve("projects").toAbsolutePath();

    private final Path imageDirectory = persistenceDirectory.resolve("images").toAbsolutePath();

    private final Path atomicActivityCollectionsDirectory = persistenceDirectory.resolve("atomicActivityCollections").toAbsolutePath();

    private final Path projectNamesFile = persistenceDirectory.resolve("projectNames.json").toAbsolutePath();

    private final Path patternTemplatesFile = persistenceDirectory.resolve("patternTemplates.json").toAbsolutePath();

    private final LoggerService loggerService;

    private final ObjectMapper objectMapper;

    @Inject
    public PersistenceHelper(LoggerService loggerService, IModelFactory modelFactory) {
        this.loggerService = loggerService;

        // TODO czy na pewno tu git
        var polymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
//                .allowIfBaseType(List.class)
//                .allowIfBaseType(Map.class)
                .allowIfSubType(Object.class)
                .build();


        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        objectMapper.registerModule(new JsonModule(modelFactory));

        setupDirectories();
    }

    @Override
    public List<File> getAllProjectFiles() {
        return getAllFilesInDirectory(projectDirectory, "json").toList();
    }

    @Override
    public List<File> getAllImageFiles() {
        return getAllFilesInDirectory(imageDirectory, "png", "jpg").toList();
    }

    @Override
    public List<File> getAllAtomicActivityCollectionFiles() {
        return getAllFilesInDirectory(atomicActivityCollectionsDirectory, "json").toList();
    }

    @Override
    public File getProjectNamesFile() {
        return projectNamesFile.toFile();
    }

    @Override
    public File getPatternTemplatesFile() {
        return patternTemplatesFile.toFile();
    }

    @Override
    public void saveProjectFile(Project project) {
        if (saveFile(generatePathToJson(projectDirectory.toString(), project.getProjectId().toString()), project)){
            loggerService.logInfo("Saved project %s".formatted(project.getProjectId()));
        } else {
            loggerService.logInfo("Project not saved %s".formatted(project.getProjectId()));
        }
    }

    @Override
    public void saveAtomicActivityCollectionFile(AtomicActivityCollection atomicActivityCollection) {
        if (saveFile(generatePathToJson(atomicActivityCollectionsDirectory.toString(), atomicActivityCollection.getAtomicActivityCollectionId().toString()), atomicActivityCollection))
            loggerService.logInfo("Saved Atomic Activity Collection %s".formatted(atomicActivityCollection.getAtomicActivityCollectionId().toString()));
    }

    @Override
    public void saveProjectNames(ProjectNameList projectNames) {
        if (saveFile(projectNamesFile, projectNames))
            loggerService.logInfo("Saved Project Names");
    }

    @Override
    public void savePatternTemplateFile(PatternTemplateCollection patternTemplateCollection) {
        if (saveFile(patternTemplatesFile, patternTemplateCollection))
            loggerService.logInfo("Saved Pattern Templates");
    }

    @Override
    public Optional<File> saveImage(File imageFile, UUID id) {
        File newFile;
        try {
            var newPath = Files.copy(imageFile.toPath(), Path.of(imageDirectory.toString(), "%s.%s".formatted(id.toString(), IPersistenceHelper.getFileExtension(imageFile))));
            newFile = newPath.toFile();
        } catch (IOException e) {
            loggerService.logError("Couldn't copy image.");
            return Optional.empty();
        }
        loggerService.logInfo("Successfully copied image.");
        return Optional.of(newFile);
    }

    @Override
    public boolean saveFile(Path path, Object content) {
        try {
            objectMapper.writeValue(path.toAbsolutePath().toFile(), content);//new JsonWrapper<>(content));
            return true;
        } catch (IOException e) {
            loggerService.logError("Couldn't save to file.");
            return false;
        }
    }

    @Override
    public void removeFile(File file) {
        // TODO Add check for testing whether we are trying to delete file from our persistence and not some random file from drive.

        try {
            Files.delete(file.toPath());
            loggerService.logInfo("Deleted file %s".formatted(file.getName()));
        } catch (IOException e) {
            loggerService.logError("Couldn't delete a file.");
        }
    }

    /**
     * Reads file and converts it to Java Object.
     * @param file File.
     * @param <T> Type to be returned.
     * @return Returns the new Java Object or null in case of failure.
     */
    @Override
    public <T> T loadFile(File file, Class<T> type) {
        try {
            return objectMapper.readValue(file, type);
        } catch (IOException e) {
            e.printStackTrace();
            loggerService.logError("Couldn't read a file. " + e.getMessage());
            return null;
        }
    }

    @Override
    public Path generatePathToJson(String directory, String name) {
        return Paths.get(String.format("%s/%s.json", directory, name));
    }

    /**
     * Checks and creates directories used by the persistence layer.
     */
    private void setupDirectories() {
        var paths = Arrays.asList(persistenceDirectory, projectDirectory, imageDirectory, atomicActivityCollectionsDirectory);

        for (var path : paths) {
            if (!Files.isDirectory(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    loggerService.logError("Couldn't create persistence directories.");
                }
            }
        }
    }

    private Stream<File> getAllFilesInDirectory(Path directory) {
        return Stream.of(Objects.requireNonNull(new File(directory.toAbsolutePath().toString()).listFiles()));
    }

    private Stream<File> getAllFilesInDirectory(Path directory, String... extensions) {
        return getAllFilesInDirectory(directory).filter(file -> file.isFile() && Arrays.stream(extensions).anyMatch(x -> IPersistenceHelper.getFileExtension(file).equals(x)));
    }
}
