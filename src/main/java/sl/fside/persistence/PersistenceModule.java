package sl.fside.persistence;

import com.google.inject.*;

public class PersistenceModule extends AbstractModule {

    @Override
    protected void configure() {

        // Services
        bind(IPersistenceHelper.class).to(PersistenceHelper.class).in(Scopes.SINGLETON);
    }

}
