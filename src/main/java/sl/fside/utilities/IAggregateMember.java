package sl.fside.utilities;

import java.util.*;

public interface IAggregateMember<T extends IAggregate<?>> {

    Optional<T> getParent();

    void setParent(T parent);

    default Optional<IAggregateRoot<?>> getRoot() {
        var parent = getParent();
        if (parent.isPresent()) {
            if (parent.get() instanceof IAggregateMember<?> parentAsAggregateMember)
                return parentAsAggregateMember.getRoot();
            else if (parent.get() instanceof IAggregateRoot<?> parentAsRoot)
                return Optional.of(parentAsRoot);
        }
        else if (this instanceof IAggregateRoot<?> thisAsAggregateRoot)
            return Optional.of(thisAsAggregateRoot);

        return Optional.empty();
    }
}
