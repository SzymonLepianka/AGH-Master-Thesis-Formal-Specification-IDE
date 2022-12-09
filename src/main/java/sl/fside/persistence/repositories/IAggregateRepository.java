package sl.fside.persistence.repositories;

import bgs.formalspecificationide.utilities.*;

import java.util.*;

public interface IAggregateRepository<T extends IAggregateRoot<?>> extends IRepository<T> {

    Optional<T> getById(UUID id);

}
