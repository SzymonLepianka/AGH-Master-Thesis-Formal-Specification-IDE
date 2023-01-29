package sl.fside.persistence.repositories;

import sl.fside.model.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public interface IProjectRepository {
//    List<String> getProjectNames();

//    @Override
    void add(@NotNull Project item);

//    @Override
    List<Project> getAll();

//    @Override
    List<Project> get(Predicate<Project> predicate);

    Optional<Project> getByName(String name);

//    @Override
    void remove(@NotNull Project item);

//    @Override
    void saveAll();

//    @Override
    void save(@NotNull Project item);
}
