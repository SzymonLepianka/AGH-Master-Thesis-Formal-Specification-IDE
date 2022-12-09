package sl.fside.services;

import sl.fside.events.*;
import com.google.inject.*;

import java.util.*;

/**
 * Event Aggregator Service.
 */
public class EventAggregatorService {

    /**
     * Command interface.
     */
    public interface Command<T extends Event<?>> {
        void execute(T event);
    }

    private final Hashtable<Class<? extends Event<?>>, List<Command<? extends Event<?>>>> eventSubscribers = new Hashtable<>();

    @Inject
    public EventAggregatorService() { }

    /**
     * Used to publish an event.
     * @param event An event.
     * @param <T> An event type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Event<?>> void publish(T event) {
        if (eventSubscribers.containsKey(event.getClass())) {
            var commands = eventSubscribers.get(event.getClass());
            for (var command :
                    commands.stream().map(command -> {
                        try {
                            return (Command<T>) command;
                        }
                        catch (ClassCastException e) {
                            return null;
                        }
                    }).filter(Objects::nonNull).toList()) {
                command.execute(event);
            }
        }
    }

    /**
     * Subscribes to a given event.
     * @param eventType An event type.
     * @param command A command to be executed on event.
     * @param <T> An event type.
     */
    public <T extends Event<?>> void subscribe(Class<T> eventType, Command<T> command) {
        // TODO Maybe prevent duplicate subscriptions?
        if (!eventSubscribers.contains(eventType.getTypeName())) {
            eventSubscribers.put(eventType, new ArrayList<>());
        }

        eventSubscribers.get(eventType).add(command);
    }

    /**
     *
     * @param eventType An event type.
     * @param command A command to be unsubscribed.
     * @param <T> An event type.
     */
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Command<T> command) {
        var commands = eventSubscribers.get(eventType);
        if (commands != null) {
            commands.removeIf(current -> current == command);
        }
    }
}
