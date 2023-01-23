package sl.fside.ui.editors.activityDiagramEditor;


import io.github.eckig.grapheditor.*;
import io.github.eckig.grapheditor.core.skins.defaults.connection.*;
import io.github.eckig.grapheditor.core.view.*;
import io.github.eckig.grapheditor.model.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.eclipse.emf.ecore.*;
import sl.fside.ui.editors.activityDiagramEditor.customskin.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;
import sl.fside.ui.editors.activityDiagramEditor.ownImpl.*;
import sl.fside.ui.editors.activityDiagramEditor.resultsEditor.*;
import sl.fside.ui.editors.activityDiagramEditor.selections.*;
import sl.fside.ui.editors.activityDiagramEditor.utils.*;

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
    private final SelectionCopier selectionCopier =
            new SelectionCopier(graphEditor.getSkinLookup(), graphEditor.getSelectionManager());
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
    public MenuItem menuItemAddSeq;
    @FXML
    public MenuItem menuItemAddBranch;
    @FXML
    public MenuItem menuItemAddBranchRe;
    @FXML
    public MenuItem menuItemAddConcur;
    @FXML
    public MenuItem menuItemAddConcurRe;
    @FXML
    public MenuItem menuItemAddCond;
    @FXML
    public MenuItem menuItemAddPara;
    @FXML
    public MenuItem menuItemAddLoop;
    @FXML
    private Button generateSpecification;
    @FXML
    private AnchorPane root;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuEdit;
    @FXML
    private Menu menuActions;
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

        generateSpecification.setOnAction(event -> {

            try {
                generateSpecification();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorMessage(e.getMessage());
                return;
            }

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
        if (graphEditor.getModel().getNodes().size() == 0) {
            NodesManager.getInstance().setMain(null);
            NodesManager.getInstance().setMainName(null);
            disableAllOptions(false);
        } else {
            System.out.println("Pozostało " + graphEditor.getModel().getNodes().size() +
                    " node'ów! Sytuacja nie powinna nigdy nastąpić");
        }
    }

    @FXML
    public void addSeq() {
        NodesManager.getInstance().setCurrentNodeType("Seq");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
        disableAllOptions(true);
    }

    @FXML
    public void addBranch() {
        NodesManager.getInstance().setCurrentNodeType("Branch");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
        disableAllOptions(true);
    }

    @FXML
    public void addBranchRe() {
        NodesManager.getInstance().setCurrentNodeType("BranchRe");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
        disableAllOptions(true);
    }

    @FXML
    public void addConcur() {
        NodesManager.getInstance().setCurrentNodeType("Concur");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
        disableAllOptions(true);
    }

    @FXML
    public void addConcurRe() {
        NodesManager.getInstance().setCurrentNodeType("ConcurRe");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
        disableAllOptions(true);
    }

    @FXML
    public void addCond() {
        NodesManager.getInstance().setCurrentNodeType("Cond");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
        disableAllOptions(true);
    }

    @FXML
    public void addPara() {
        NodesManager.getInstance().setCurrentNodeType("Para");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
        disableAllOptions(true);
    }

    @FXML
    public void addLoop() {
        NodesManager.getInstance().setCurrentNodeType("Loop");
        activeSkinController.get().addNode(graphEditor.getView().getLocalToSceneTransform().getMxx());
        disableAllOptions(true);
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

    private void disableAllOptions(boolean disable) {
        menuItemAddSeq.setDisable(disable);
        menuItemAddBranch.setDisable(disable);
        menuItemAddBranchRe.setDisable(disable);
        menuItemAddConcur.setDisable(disable);
        menuItemAddConcurRe.setDisable(disable);
        menuItemAddCond.setDisable(disable);
        menuItemAddPara.setDisable(disable);
        menuItemAddLoop.setDisable(disable);
    }

    /**
     * Initializes the menu bar.
     */
    private void initializeMenuBar() {
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

        // hide File Menu
        menuFile.setVisible(false);
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
    public void generateSpecification() throws Exception {
        VBox main = (VBox) NodesManager.getInstance().getMain();
        String mainName = NodesManager.getInstance().getMainName();
        if (main == null) {
            throw new Exception("Nie został dodany wzorzec!");
        }

        StringBuilder patternExpression = new StringBuilder();
        for (var child : main.getChildren()) {
            if (child instanceof Text) {
                String text = ((Text) child).getText();
                if (text.equals(mainName)) {
                    patternExpression.append(mainName);
                    patternExpression.append("(");
                }
            } else if (child instanceof VBox || child instanceof HBox) {
                patternExpression.append(getNestedPatternFromVBox((Pane) child));
            } else if (child instanceof MyArrow) {
                // pomiń strzałki na grafie
            } else {
                System.out.println("Nieobsłużone(1): " + child);
            }
        }
        patternExpression.deleteCharAt(patternExpression.length() - 1);
        patternExpression.append(")");
        System.out.println("Wyrażenie: " + patternExpression);
        NodesManager.getInstance().setPatternExpression(patternExpression.toString());

//        System.out.println(graphEditor.getModel().getNodes());
//        System.out.println(graphEditor.getModel().getConnections().get(0).getSource());
    }

    private String getNestedPatternFromVBox(Pane vBoxOrHBox) throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean closeStatement = false;
        for (var child2 : vBoxOrHBox.getChildren()) {
            if (child2 instanceof ComboBox) {
                String value = (String) ((ComboBox<?>) child2).getValue();
                if (value == null) {
                    throw new Exception("Nie wszystkie elementy są wypełnione!");
                }
                if (value.equals("Seq") || value.equals("Branch") || value.equals("BranchRe") ||
                        value.equals("Concur") || value.equals("ConcurRe") || value.equals("Cond") ||
                        value.equals("Para") || value.equals("Loop")) {
                    sb.append(value);
                    sb.append("(");
                    closeStatement = true;
                } else {
                    sb.append(value);
                    sb.append(",");
                }
            } else if (child2 instanceof VBox || child2 instanceof HBox) {
                sb.append(getNestedPatternFromVBox((Pane) child2));
            } else if (child2 instanceof Text || child2 instanceof MyArrow) {
//                System.out.println("Text do olania: " + ((Text) child2).getText());
            } else {
                System.out.println("Nieobsłużone3");
            }
        }
        if (closeStatement) {
            sb.deleteCharAt(sb.length() - 1);
            sb.append("),");
        }
        return sb.toString();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error during generating specification");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
