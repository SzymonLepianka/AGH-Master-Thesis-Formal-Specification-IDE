package sl.fside;

import sl.fside.factories.*;
import sl.fside.repositories.*;
import sl.fside.services.*;
import sl.fside.ui.*;
import com.google.inject.*;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();

        // Services
        bind(IResourceService.class).to(ResourceService.class).in(Scopes.SINGLETON);
        bind(EventAggregatorService.class).in(Scopes.SINGLETON);
        bind(ModelTrackerService.class).in(Scopes.SINGLETON);
        bind(LoggerService.class).in(Scopes.SINGLETON);
        bind(XmlParserService.class).in(Scopes.SINGLETON);

        // Factories
        bind(IModelFactory.class).to(ModelFactory.class).in(Scopes.SINGLETON);

        // Submodules
        install(new RepositoriesModule());
        install(new UIModule());
    }
}
