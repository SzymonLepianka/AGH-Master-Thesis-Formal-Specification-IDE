package sl.fside.persistence.repositories;

import bgs.formalspecificationide.factories.*;
import bgs.formalspecificationide.model.*;
import bgs.formalspecificationide.persistence.*;
import com.google.inject.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class PatternTemplateRepository implements IPatternTemplateRepository{

    private final IPersistenceHelper persistenceHelper;

    private final IModelFactory modelFactory;

    private PatternTemplateCollection patternTemplateCollection;

    @Inject
    public PatternTemplateRepository(IPersistenceHelper persistenceHelper, IModelFactory modelFactory) {
        this.persistenceHelper = persistenceHelper;
        this.modelFactory = modelFactory;
        patternTemplateCollection = loadPatternTemplateFile();
        if (patternTemplateCollection == null)
            patternTemplateCollection = modelFactory.createPatternTemplateCollection();
    }

    @Override
    public Optional<PatternTemplateCollection> getById(UUID id) {
        return Optional.of(patternTemplateCollection);
    }

    @Override
    public void add(@NotNull PatternTemplateCollection item) {
        throw new IllegalArgumentException("Only one pattern template collection can exist.");
    }

    @Override
    public List<PatternTemplateCollection> getAll() {
        return Arrays.stream((new PatternTemplateCollection[] {patternTemplateCollection})).toList();
    }

    @Override
    public List<PatternTemplateCollection> get(Predicate<PatternTemplateCollection> predicate) {
        return getAll().stream().filter(predicate).toList();
    }

    @Override
    public void remove(@NotNull PatternTemplateCollection item) {
        //persistenceHelper.removeFile(persistenceHelper.getPatternTemplatesFile());
        //patternTemplateCollections.remove(item);
        // For now only one collection exists, so it's not removed - only reset
        if (item == patternTemplateCollection)
            patternTemplateCollection = modelFactory.createPatternTemplateCollection();
    }

    @Override
    public void saveAll() {
        // For now only one collection exists, so only one is saved
        save(patternTemplateCollection);
    }

    @Override
    public void save(@NotNull PatternTemplateCollection item) {
        if (item == patternTemplateCollection)
            persistenceHelper.saveFile(persistenceHelper.getPatternTemplatesFile().toPath(), patternTemplateCollection);
        else
            throw new IllegalArgumentException("Cannot save Pattern Template Collection from outside of a repository.");
    }

    private PatternTemplateCollection loadPatternTemplateFile() {
        return persistenceHelper.loadFile(persistenceHelper.getPatternTemplatesFile(), PatternTemplateCollection.class);
    }
}
