package sl.fside.persistence.repositories;

import org.jetbrains.annotations.*;
import sl.fside.model.*;

import java.util.*;
import java.util.function.*;

public interface IProjectRepository {

    void add(@NotNull Project item);

    List<Project> getAll();

    List<Project> get(Predicate<Project> predicate);

    void remove(@NotNull Project item);

    void save(@NotNull Project item);

    Project getById(UUID projectId);
}
