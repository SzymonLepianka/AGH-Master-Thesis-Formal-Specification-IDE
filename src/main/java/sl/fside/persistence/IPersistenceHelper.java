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

    void saveProjectFile(Project project);

    boolean saveFile(Path path, Object content);

    void removeFile(File file);

    <T> T loadFile(File file, Class<T> type);

    Path generatePathToJson(String directory, String name);
}
