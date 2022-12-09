package sl.fside.ui.editors.activityDiagramEditor;


import sl.fside.ui.editors.activityDiagramEditor.customskin.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;
import sl.fside.ui.editors.activityDiagramEditor.ownImpl.*;
import sl.fside.ui.editors.activityDiagramEditor.resultsEditor.*;
import sl.fside.ui.editors.activityDiagramEditor.selections.*;
import sl.fside.ui.editors.activityDiagramEditor.utils.*;
import io.github.eckig.grapheditor.*;
import io.github.eckig.grapheditor.core.skins.defaults.connection.*;
import io.github.eckig.grapheditor.core.view.*;
import io.github.eckig.grapheditor.model.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.eclipse.emf.ecore.*;

import java.io.*;
import java.util.*;

/**
 * Controller for the {@link ActivityDiagramEditor} application.
 */
public class ActivityDiagramEditorController {

    private static final String APPLICATION_TITLE = "Results";
    private static final String DEMO_STYLESHEET = "demo.css";
    private static final String FONT_AWESOME = "fontawesome.ttf";

    private static final Stage resultsStage = new Stage();
    private final GraphEditor graphEditor = new OwnDefaultGraphEditor();
    private final SelectionCopier selectionCopier = new SelectionCopier(graphEditor.getSkinLookup(), graphEditor.getSelectionManager());
    private final ActivityDiagramEditorPersistence graphEditorPersistence = new ActivityDiagramEditorPersistence();
    private final ObjectProperty<DefaultSkinController> activeSkinController = new SimpleObjectProperty<>() {

        @Override
        protected void invalidated() {
            super.invalidated();
            if (get() != null) {
                get().activate();
            }
        }

    };
    @FXML
    private Button generateSpecification;
    @FXML
    private AnchorPane root;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem addConnectorButton;
    @FXML
    private MenuItem clearConnectorsButton;
    @FXML
    private Menu connectorTypeMenu;
    @FXML
    private Menu connectorPositionMenu;
    @FXML
    private RadioMenuItem inputConnectorTypeButton;
    @FXML
    private RadioMenuItem outputConnectorTypeButton;
    @FXML
    private RadioMenuItem leftConnectorPositionButton;
    @FXML
    private RadioMenuItem rightConnectorPositionButton;
    @FXML
    private RadioMenuItem topConnectorPositionButton;
    @FXML
    private RadioMenuItem bottomConnectorPositionButton;
    @FXML
    private RadioMenuItem showGridButton;
    @FXML
    private RadioMenuItem snapToGridButton;
    @FXML
    private Menu readOnlyMenu;
    @FXML
    private RadioMenuItem defaultSkinButton;
    @FXML
    private Menu intersectionStyle;
    @FXML
    private RadioMenuItem gappedStyleButton;
    @FXML
    private RadioMenuItem detouredStyleButton;
    @FXML
    private ToggleButton minimapButton;
    @FXML
    private GraphEditorContainer graphEditorContainer;
    private DefaultSkinController defaultSkinController;

    /**
     * Called by JavaFX when FXML is loaded.
     */
    public void initialize() {

        final GModel model = GraphFactory.eINSTANCE.createGModel();

        graphEditor.setModel(model);
        graphEditorContainer.setGraphEditor(graphEditor);

        setDetouredStyle();

        defaultSkinController = new DefaultSkinController(graphEditor, graphEditorContainer);

        activeSkinController.set(defaultSkinController);

        graphEditor.modelProperty().addListener((w, o, n) -> selectionCopier.initialize(n));
        selectionCopier.initialize(model);

        initializeMenuBar();

        generateSpecification.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
//                final URL location = getClass().getClassLoader().getResource("ResultsEditor.fxml");
                final FXMLLoader loader = new FXMLLoader(ResultsEditorController.class.getResource("ResultsEditor.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final Scene scene = new Scene(root, 600, 800);
                scene.getStylesheets().add(ResultsEditorController.class.getResource(DEMO_STYLESHEET).toExternalForm());
                Font.loadFont(ResultsEditorController.class.getResource(FONT_AWESOME).toExternalForm(), 12);

                resultsStage.setScene(scene);
                resultsStage.setTitle(APPLICATION_TITLE);
                resultsStage.show();

                // close current window
//                ((Node)(event.getSource())).getScene().getWindow().hide();
            }
        });
    }

    /**
     * Pans the graph editor container to place the window over the center of the
     * content.
     *
     * <p>
     * Only works after the scene has been drawn, when getWidth() and getHeight()
     * return non-zero values.
     * </p>
     */
    public void panToCenter() {
        graphEditorContainer.panTo(Pos.CENTER);
    }

    @FXML
    public void load() {
        graphEditorPersistence.loadFromFile(graphEditor);
        checkSkinType();
    }

    @FXML
    public void loadSample() {
        defaultSkinButton.setSelected(true);
        setDefaultSkin();
        graphEditorPersistence.loadSample(graphEditor);
    }

    @FXML
    public void loadSampleLarge() {
        defaultSkinButton.setSelected(true);
        setDefaultSkin();
        graphEditorPersistence.loadSampleLarge(graphEditor);
    }

    @FXML
    public void save() {
        graphEditorPersistence.saveToFile(graphEditor);
    }

    @FXML
    public void clearAll() {
        Commands.clear(graphEditor.getModel());
    }

    @FXML
    public void exit() {
        Platform.exit();
    }

    @FXML
    public void undo() {
        Commands.undo(graphEditor.getModel());
    }

    @FXML
    public void redo() {
        Commands.redo(graphEditor.getModel());
    }

    @FXML
    public void copy() {
        selectionCopier.copy();
    }

    @FXML
    public void paste() {
        activeSkinController.get().handlePaste(selectionCopier);
    }

    @FXML
    public void selectAll() {
        activeSkinController.get().handleSelectAll();
    }

    @FXML
    public void deleteSelection() {
        final List<EObject> selection = new ArrayList<>(graphEditor.getSelectionManager().getSelectedItems());
        graphEditor.delete(selection);
    }

    @FXML
    public void addSeq() {
        NodesManager.getInstance().setCurrentNodeType("SEQ");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addBranch() {
        NodesManager.getInstance().setCurrentNodeType("BRANCH");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addBranchRe() {
        NodesManager.getInstance().setCurrentNodeType("BRANCHRE");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addConcur() {
        NodesManager.getInstance().setCurrentNodeType("CONCUR");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addConcurRe() {
        NodesManager.getInstance().setCurrentNodeType("CONCURRE");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addCond() {
        NodesManager.getInstance().setCurrentNodeType("COND");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addPara() {
        NodesManager.getInstance().setCurrentNodeType("PARA");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void addLoop() {
        NodesManager.getInstance().setCurrentNodeType("LOOP");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
    }

    @FXML
    public void setDefaultSkin() {
        activeSkinController.set(defaultSkinController);
    }

    @FXML
    public void setGappedStyle() {

        graphEditor.getProperties().getCustomProperties().remove(SimpleConnectionSkin.SHOW_DETOURS_KEY);
        graphEditor.reload();
    }

    @FXML
    public void setDetouredStyle() {

        final Map<String, String> customProperties = graphEditor.getProperties().getCustomProperties();
        customProperties.put(SimpleConnectionSkin.SHOW_DETOURS_KEY, Boolean.toString(true));
        graphEditor.reload();
    }

    @FXML
    public void toggleMinimap() {
        graphEditorContainer.getMinimap().visibleProperty().bind(minimapButton.selectedProperty());
    }

    /**
     * Initializes the menu bar.
     */
    private void initializeMenuBar() {

        final ToggleGroup connectionStyleGroup = new ToggleGroup();
        connectionStyleGroup.getToggles().addAll(gappedStyleButton, detouredStyleButton);

        graphEditor.getProperties().gridVisibleProperty().bind(showGridButton.selectedProperty());
        graphEditor.getProperties().snapToGridProperty().bind(snapToGridButton.selectedProperty());

        for (final EditorElement type : EditorElement.values()) {
            final CheckMenuItem readOnly = new CheckMenuItem(type.name());
            graphEditor.getProperties().readOnlyProperty(type).bind(readOnly.selectedProperty());
        }

        minimapButton.setGraphic(AwesomeIcon.MAP.node());

        final SetChangeListener<? super EObject> selectedNodesListener = change -> checkConnectorButtonsToDisable();
        graphEditor.getSelectionManager().getSelectedItems().addListener(selectedNodesListener);
        checkConnectorButtonsToDisable();
    }

    /**
     * Crudely inspects the model's first node and sets the new skin type accordingly.
     */
    private void checkSkinType() {
        if (!graphEditor.getModel().getNodes().isEmpty()) {
            activeSkinController.set(defaultSkinController);
        }
    }

    /**
     * Checks if the connector buttons need disabling (e.g. because no nodes are selected).
     */
    private void checkConnectorButtonsToDisable() {
    }

    @FXML
    public void generateSpecification() {
        System.out.println(graphEditor.getModel().getNodes());
        System.out.println(graphEditor.getModel().getConnections().get(0).getSource());
    }
}
