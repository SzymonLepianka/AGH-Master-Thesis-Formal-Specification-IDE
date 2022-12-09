package sl.fside.ui.editors.activityDiagramEditor.ownImpl;

import io.github.eckig.grapheditor.*;
import io.github.eckig.grapheditor.core.*;
import io.github.eckig.grapheditor.core.connections.*;
import io.github.eckig.grapheditor.core.view.*;
import io.github.eckig.grapheditor.model.*;
import io.github.eckig.grapheditor.utils.*;
import javafx.beans.property.*;
import javafx.scene.layout.*;
import javafx.util.*;
import org.eclipse.emf.common.command.*;
import org.eclipse.emf.ecore.*;

import java.util.*;
import java.util.function.*;

/**
 * Default implementation of the {@link GraphEditor}.
 */
public class OwnDefaultGraphEditor implements GraphEditor {

    private final GraphEditorProperties mProperties;
    private final GraphEditorView mView;
    private final ConnectionEventManager mConnectionEventManager = new ConnectionEventManager();
    private final OwnGraphEditorSkinManager mSkinManager;
    private final GraphEditorController<OwnDefaultGraphEditor> mController;

    private final ObjectProperty<GModel> mModelProperty = new ObjectPropertyBase<GModel>() {

        @Override
        public Object getBean() {
            return OwnDefaultGraphEditor.this;
        }

        @Override
        public String getName() {
            return "model";
        }

    };

    /**
     * Creates a new default implementation of the {@link GraphEditor}.
     */
    public OwnDefaultGraphEditor() {
        this(null);
    }

    /**
     * Creates a new default implementation of the {@link GraphEditor}.
     */
    public OwnDefaultGraphEditor(final GraphEditorProperties pProperties) {
        mProperties = pProperties == null ? new GraphEditorProperties() : pProperties;
        mView = new GraphEditorView(mProperties);
        mSkinManager = new OwnGraphEditorSkinManager(this, mView);
        mController = new GraphEditorController<>(this, mSkinManager, mView, mConnectionEventManager, mProperties);

        final ConnectionLayouter connectionLayouter = mController.getConnectionLayouter();
        mView.setConnectionLayouter(connectionLayouter);
        mSkinManager.setConnectionLayouter(connectionLayouter);
    }

    @Override
    public void setNodeSkinFactory(final Callback<GNode, GNodeSkin> pSkinFactory) {
        mSkinManager.setNodeSkinFactory(pSkinFactory);
    }

    @Override
    public void setConnectorSkinFactory(final Callback<GConnector, GConnectorSkin> pConnectorSkinFactory) {
        mSkinManager.setConnectorSkinFactory(pConnectorSkinFactory);
    }

    @Override
    public void setConnectionSkinFactory(final Callback<GConnection, GConnectionSkin> pConnectionSkinFactory) {
        mSkinManager.setConnectionSkinFactory(pConnectionSkinFactory);
    }

    @Override
    public void setJointSkinFactory(final Callback<GJoint, GJointSkin> pJointSkinFactory) {
        mSkinManager.setJointSkinFactory(pJointSkinFactory);
    }

    @Override
    public void setTailSkinFactory(final Callback<GConnector, GTailSkin> pTailSkinFactory) {
        mSkinManager.setTailSkinFactory(pTailSkinFactory);
    }

    @Override
    public void setConnectorValidator(final GConnectorValidator pValidator) {
        mController.setConnectorValidator(pValidator);
    }

    @Override
    public GModel getModel() {
        return mModelProperty.get();
    }

    @Override
    public void setModel(final GModel pModel) {
        mModelProperty.set(pModel);
    }

    @Override
    public void reload() {
        mController.process();
    }

    @Override
    public ObjectProperty<GModel> modelProperty() {
        return mModelProperty;
    }

    @Override
    public Region getView() {
        return mView;
    }

    @Override
    public GraphEditorProperties getProperties() {
        return mProperties;
    }

    @Override
    public SkinLookup getSkinLookup() {
        return mSkinManager;
    }

    @Override
    public SelectionManager getSelectionManager() {
        return mController.getSelectionManager();
    }

    @Override
    public void setOnConnectionCreated(final Function<GConnection, Command> pConsumer) {
        mConnectionEventManager.setOnConnectionCreated(pConsumer);
    }

    @Override
    public void setOnConnectionRemoved(final BiFunction<RemoveContext, GConnection, Command> pOnConnectionRemoved) {
        mConnectionEventManager.setOnConnectionRemoved(pOnConnectionRemoved);
        getModelEditingManager().setOnConnectionRemoved(pOnConnectionRemoved);
    }

    @Override
    public void setOnNodeRemoved(final BiFunction<RemoveContext, GNode, Command> pOnNodeRemoved) {
        getModelEditingManager().setOnNodeRemoved(pOnNodeRemoved);
    }

    @Override
    public void delete(Collection<EObject> pItems) {
        getModelEditingManager().remove(pItems);
    }

    private ModelEditingManager getModelEditingManager() {
        return mController.getModelEditingManager();
    }
}

