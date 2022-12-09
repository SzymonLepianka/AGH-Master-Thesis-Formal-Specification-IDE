package sl.fside.services;

import bgs.formalspecificationide.events.*;
import bgs.formalspecificationide.utilities.*;
import com.google.inject.*;

public class ModelTrackerService implements IObserver {

    private final EventAggregatorService eventAggregatorService;

    @Inject
    public ModelTrackerService(EventAggregatorService eventAggregatorService) {

        this.eventAggregatorService = eventAggregatorService;
    }

    @Override
    public void notify(Event<?> event) {
        eventAggregatorService.publish(event);
    }

}
