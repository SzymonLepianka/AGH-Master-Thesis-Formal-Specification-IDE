package sl.fside.events;

/**
 * Base event class.
 */
public abstract class Event<T> {

    private final T publisher;

    public Event(T publisher) {
        this.publisher = publisher;
    }

    public T getPublisher() {
        return publisher;
    }

    @Override
    public String toString() {
        return "Event: %s published by %s".formatted(this.getClass().getName(), publisher.toString());
    }
}