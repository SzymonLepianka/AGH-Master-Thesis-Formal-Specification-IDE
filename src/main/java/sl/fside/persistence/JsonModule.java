package sl.fside.persistence;

import sl.fside.factories.*;
import sl.fside.model.*;
import sl.fside.utilities.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.*;
import com.fasterxml.jackson.databind.module.*;
import com.google.inject.*;

import java.io.*;

class JsonModule extends SimpleModule {

    private final IModelFactory modelFactory;

    private static class BaseDeserializer<T extends ModelBase> extends JsonDeserializer<T> implements ResolvableDeserializer {

        private final JsonDeserializer<?> defaultDeserializer;

        private final IModelFactory modelFactory;

        protected BaseDeserializer(JsonDeserializer<?> defaultDeserializer, IModelFactory modelFactory) {
            this.defaultDeserializer = defaultDeserializer;
            this.modelFactory = modelFactory;
        }

        protected JsonDeserializer<?> getDefaultDeserializer() {
            return defaultDeserializer;
        }

        protected IModelFactory getModelFactory() {
            return modelFactory;
        }

        @Override
        public void resolve(DeserializationContext context) throws JsonMappingException
        {
            if (defaultDeserializer instanceof ResolvableDeserializer resolvableDeserializer) {
                resolvableDeserializer.resolve(context);
            }
        }

        @Override
        public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            @SuppressWarnings("unchecked") var deserialized =  (T) getDefaultDeserializer().deserialize(jsonParser, deserializationContext);
            modelFactory.registerInModelTracker(deserialized);
            return deserialized;
        }
    }

    private static class AggregateRootDeserializer<T extends ModelAggregate & IAggregateRoot<ModelBase>> extends BaseDeserializer<T> {

        protected AggregateRootDeserializer(JsonDeserializer<?> defaultDeserializer, IModelFactory modelFactory) {
            super(defaultDeserializer, modelFactory);
        }

        private void observeChildren(ModelBase modelBase) {
            if (modelBase instanceof ModelAggregate modelAggregate) {
                for (var child : modelAggregate.getChildren()) {
                    child.subscribe(modelAggregate);

                    observeChildren(child);
                }
            }
        }

        private void setParents(ModelAggregate root) {
            for (var child : root.getChildren()) {
                child.setParent(root);

                if (child instanceof ModelAggregate childModelAggregate)
                    setParents(childModelAggregate);
            }
        }

        @Override
        public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            var aggregateRoot = super.deserialize(jsonParser, deserializationContext);
            observeChildren(aggregateRoot);
            setParents(aggregateRoot);
            return aggregateRoot;
        }
    }

    /*private static class ProjectDeserializer extends AggregateRootDeserializer<Project> {

        public ProjectDeserializer(JsonDeserializer<?> defaultDeserializer, IModelFactory modelFactory) {
            super(defaultDeserializer, modelFactory);
        }

        @Override
        public Project deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            var deserialized = getDefaultDeserializer().deserialize(jsonParser, deserializationContext);

            if (deserialized instanceof Project project) {
                getModelFactory().registerProject(project);
                observeChildren(project);
                return project;
            }

            return null;
        }
    }*/

    @Inject
    public JsonModule(IModelFactory modelFactory) {
        this.modelFactory = modelFactory;

        setDeserializerModifier(new DeserializerModifier(this.modelFactory));
    }

    private static class DeserializerModifier extends BeanDeserializerModifier {

        private final IModelFactory modelFactory;

        public DeserializerModifier(IModelFactory modelFactory) {

            this.modelFactory = modelFactory;
        }

        @Override
        public  JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            var beanClass = beanDesc.getBeanClass();
            /*if (beanClass == Project.class) {
                return new ProjectDeserializer(deserializer, modelFactory);
            }*/
            if (IAggregateRoot.class.isAssignableFrom(beanClass)) {
               return new AggregateRootDeserializer<>(deserializer, modelFactory);
            } else if (ModelBase.class.isAssignableFrom(beanClass)) {
                return new BaseDeserializer<>(deserializer, modelFactory);
            }

            return deserializer;
        }
    }

}
