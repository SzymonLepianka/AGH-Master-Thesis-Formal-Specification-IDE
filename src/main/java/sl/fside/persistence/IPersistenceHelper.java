package sl.fside.persistence;

import sl.fside.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public interface IPersistenceHelper {

    static String getFileExtension(File file) {
        var fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    static String getFileNameWithoutExtension(File file) {
        var fileName = file.getName();
        var dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    List<File> getAllProjectFiles();

    List<File> getAllImageFiles();

    List<File> getAllAtomicActivityCollectionFiles();

    File getProjectNamesFile();

    File getPatternTemplatesFile();

    void saveProjectFile(Project project);

    void saveAtomicActivityCollectionFile(AtomicActivityCollection atomicActivityCollection);

    void saveProjectNames(ProjectNameList projectNames);

    void savePatternTemplateFile(PatternTemplateCollection patternTemplateCollection);

    /**
     * Saves (copies) image file to our persistence.
     * @param imageFile Image file.
     * @param id Image id.
     * @return New file.
     */
    Optional<File> saveImage(File imageFile, UUID id);

    boolean saveFile(Path path, Object content);

    void removeFile(File file);

    <T> T loadFile(File file, Class<T> type);

    Path generatePathToJson(String directory, String name);
}
