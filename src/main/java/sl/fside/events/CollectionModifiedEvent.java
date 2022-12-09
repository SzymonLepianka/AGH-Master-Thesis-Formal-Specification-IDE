package sl.fside.events;

import java.util.*;

public class CollectionModifiedEvent<PublisherType, ItemType> extends Event<PublisherType>{

    public Optional<String> getCollectionName() {
        return Optional.ofNullable(collectionName);
    }

    public enum ModificationEnum {
        ADDED,
        REMOVED,
    }

    private final ItemType item;

    private final ModificationEnum modification;

    private String collectionName;

    public CollectionModifiedEvent(PublisherType publisher, ItemType item, ModificationEnum modification, String collectionName) {
        this(publisher, item, modification);
        this.collectionName = collectionName;
    }

    public CollectionModifiedEvent(PublisherType publisher, ItemType item, ModificationEnum modification) {
        super(publisher);
        this.item = item;
        this.modification = modification;
    }

    public ItemType getItem() {
        return item;
    }

    public ModificationEnum getModification() {
        return modification;
    }
}
