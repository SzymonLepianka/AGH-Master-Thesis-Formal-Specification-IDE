package sl.fside.utilities;

import java.util.*;

public interface IAggregate<T extends IAggregateMember<?>> {

    List<T> getChildren();

    void addChild(T child);

    void addChildren(List<T> children);

    void removeChild(T child);

    void removeChildren(List<T> children);

    <Z extends T> List<Z> getChildrenOfType(Class<Z> type);

}
