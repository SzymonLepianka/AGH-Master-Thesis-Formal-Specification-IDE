package sl.fside.ui.editors.activityDiagramEditor;


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
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;
import org.eclipse.emf.ecore.*;
import sl.fside.model.*;
import sl.fside.ui.editors.activityDiagramEditor.customskin.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;
import sl.fside.ui.editors.activityDiagramEditor.ownImpl.*;
import sl.fside.ui.editors.activityDiagramEditor.selections.*;
import sl.fside.ui.editors.activityDiagramEditor.utils.*;

import java.util.*;

/**
 * Controller for the {@link ActivityDiagramEditor} application.
 */
public class ActivityDiagramEditorController {
    private final OwnDefaultGraphEditor graphEditor = new OwnDefaultGraphEditor();
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
    public MenuItem menuItemAddSeqSeq;
    @FXML
    private Button generateSpecificationButton;
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
    private RadioMenuItem showColorsOnDiagramButton;
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

    public void checkExistingActivityDiagram() {
        PatternExpression patternExpression = NodesManager.getInstance().getPatternExpression();
        if (patternExpression != null) {
            String mainPatternName = patternExpression.toString().split("\\(")[0];
            switch (mainPatternName) {
                case "Seq" -> addSeq();
                case "Branch" -> addBranch();
                case "BranchRe" -> addBranchRe();
                case "Concur" -> addConcur();
                case "ConcurRe" -> addConcurRe();
                case "Cond" -> addCond();
                case "Para" -> addPara();
                case "Loop" -> addLoop();
                case "SeqSeq" -> addSeqSeq();
                default -> System.out.println("Inny mainPatternName! - " + mainPatternName);
            }
        }
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
            NodesManager.getInstance().removePatternExpression();
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
    public void addSeqSeq() {
        NodesManager.getInstance().setCurrentNodeType("SeqSeq");
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
        menuItemAddSeqSeq.setDisable(disable);
    }

    /**
     * Initializes the menu bar.
     */
    private void initializeMenuBar() {
        graphEditor.getProperties().gridVisibleProperty().bind(showGridButton.selectedProperty());
        graphEditor.getProperties().snapToGridProperty().bind(snapToGridButton.selectedProperty());

        // showing colors on activity diagram
        showColorsOnDiagramButton.setOnAction(event -> {
            Map<Pane, Border> bordersOnActivityDiagram = NodesManager.getInstance().getBordersOnActivityDiagram();
            Map<Rectangle, Color> colorsOnActivityDiagram = NodesManager.getInstance().getColorsOnActivityDiagram();
            NodesManager.getInstance().setShowColorsOnDiagram(showColorsOnDiagramButton.isSelected());
            if (showColorsOnDiagramButton.isSelected()) {
                bordersOnActivityDiagram.forEach(Region::setBorder);
                colorsOnActivityDiagram.forEach(Shape::setFill);
                colorsOnActivityDiagram.forEach((k, v) -> k.setWidth(10));
                colorsOnActivityDiagram.forEach((k, v) -> k.setHeight(10));
            } else {
                bordersOnActivityDiagram.forEach((pane, border) -> {
                    pane.setBorder(null);
                    colorsOnActivityDiagram.forEach((k, v) -> k.setFill(null));
                    colorsOnActivityDiagram.forEach((k, v) -> k.setWidth(0));
                    colorsOnActivityDiagram.forEach((k, v) -> k.setHeight(0));
                });
            }
        });

        for (final EditorElement type : EditorElement.values()) {
            final CheckMenuItem readOnly = new CheckMenuItem(type.name());
            graphEditor.getProperties().readOnlyProperty(type).bind(readOnly.selectedProperty());
        }

        minimapButton.setGraphic(AwesomeIcon.MAP.node());

        final SetChangeListener<? super EObject> selectedNodesListener = change -> checkConnectorButtonsToDisable();
        graphEditor.getSelectionManager().getSelectedItems().addListener(selectedNodesListener);
        checkConnectorButtonsToDisable();

        // hide File Menu and Edit Menu
        menuFile.setVisible(false);
        menuEdit.setVisible(false);
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
    public void generateSpecificationButtonClicked(ActionEvent actionEvent) {
        try {
            generateSpecification();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e.getMessage());
            return;
        }

        // Close the current window (with ActivityDiagramEditor)
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void generateSpecification() throws Exception {
        VBox main = (VBox) NodesManager.getInstance().getMain();
        if (main == null) {
            throw new Exception("Nie został dodany wzorzec!");
        }

        StringBuilder patternExpression = new StringBuilder();
        PatternExpression pe = null;
        for (var child : main.getChildren()) {

            // nazwa wzorca znajduje się wewnątrz pierwszego HBox
            if (child instanceof HBox && main.getChildren().indexOf(child) == 0) {
                String mainPatternName = ((Text) (((HBox) child).getChildren().get(0))).getText().replaceAll("\\s", "");
                patternExpression.append(mainPatternName);
                patternExpression.append("(");
                pe = new PatternExpression(PatternExpression.MainPatternName.valueOf(mainPatternName));

                // elementy grafu w zagnieżdżonych elementach VBox i HBox
            } else if (child instanceof VBox || (child instanceof HBox && main.getChildren().indexOf(child) != 0)) {
                patternExpression.append(getNestedPatternFromVBox((Pane) child, pe));

                // pomiń strzałki na grafie
            } else if (child instanceof MyArrow) {
            } else {
                System.out.println("Nieobsłużone(1): " + child);
            }
        }

        // usuwa ostatni znak (przecinek) i zamyka wyrażenie wzorcowe
        patternExpression.deleteCharAt(patternExpression.length() - 1);
        patternExpression.append(")");
        System.out.println("Wyrażenie: " + patternExpression);
        NodesManager.getInstance().setPatternExpression(pe);
        NodesManager.getInstance().setWasSpecificationGenerated(true);
    }

    private String getNestedPatternFromVBox(Pane vBoxOrHBox, PatternExpression pe) throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean closeStatement = false;
        PatternExpression nestedPatternExpression = null;
        for (var child2 : vBoxOrHBox.getChildren()) {

            // Combobox zawiera atomiczne aktywności
            if (child2 instanceof ComboBox) {
                String value = (String) ((ComboBox<?>) child2).getValue();
                if (value == null) {
                    throw new Exception("Nie wszystkie elementy są wypełnione!");
                }
                sb.append(value);
                sb.append(",");
                Pair<Boolean, Relation> booleanRelationPair = checkIfRelation(value);
                pe.add(new PatternElement(value, null, booleanRelationPair.getKey(), booleanRelationPair.getValue()));

                // nazwy zagnieżdżonych (wewnętrznych) wzorców
            } else if (child2 instanceof HBox && ((HBox) child2).getChildren().get(0) instanceof Text) {
                String text = ((Text) (((HBox) child2).getChildren().get(0))).getText().replaceAll("\\s", "");
                if (!text.equals("+")) { // fixes nested Branch
                    sb.append(text);
                    sb.append("(");

                    nestedPatternExpression = new PatternExpression(PatternExpression.MainPatternName.valueOf(text));
                    pe.add(new PatternElement(null, nestedPatternExpression, false, null));

                    closeStatement = true;
                }

                // elementy grafu w zagnieżdżonych elementach VBox i HBox
            } else if (child2 instanceof VBox || child2 instanceof HBox) {

                if (closeStatement) {
                    sb.append(getNestedPatternFromVBox((Pane) child2, nestedPatternExpression));
                } else {
                    sb.append(getNestedPatternFromVBox((Pane) child2, pe));
                }

                // elementy pomijane
            } else if (child2 instanceof Text || child2 instanceof MyArrow || child2 instanceof Line) {
            } else {
                System.out.println("Nieobsłużone(2): " + child2);
            }
        }

        // domknięcie zagnieżdżonego wzorca
        if (closeStatement) {
            sb.deleteCharAt(sb.length() - 1);
            sb.append("),");
        }
        return sb.toString();
    }

    private Pair<Boolean, Relation> checkIfRelation(String aa) {

        UseCaseDiagram currentUseCaseDiagram = NodesManager.getInstance().getCurrentUseCaseDiagram();
        UseCase currentUseCase = NodesManager.getInstance().getCurrentUseCase();

        if (aa.startsWith("<<include>>")) {

            String useCaseToName = aa.substring(11);
            UUID useCaseToId = currentUseCaseDiagram.getUseCaseList().stream()
                    .filter(uc -> uc.getUseCaseName().equals(useCaseToName)).findFirst().orElseThrow().getId();

            List<Relation> includeRelations = currentUseCaseDiagram.getRelations().stream()
                    .filter(r -> r.getType() == Relation.RelationType.INCLUDE &&
                            r.getFromId().equals(currentUseCase.getId()) && r.getToId().equals(useCaseToId)).toList();

            if (!includeRelations.isEmpty()) {
                return new Pair<>(Boolean.TRUE, includeRelations.get(0));
            } else {
                return new Pair<>(Boolean.FALSE, null);
            }
        } else if (aa.startsWith("<<extend>>")) {

            UUID targetUseCaseId =
                    currentUseCaseDiagram.getUseCaseList().stream().filter(uc -> aa.endsWith(uc.getUseCaseName()))
                            .findFirst().orElseThrow().getId();
            List<Relation> extendRelations = currentUseCaseDiagram.getRelations().stream()
                    .filter(r -> r.getType() == Relation.RelationType.EXTEND &&
                            r.getToId().equals(currentUseCase.getId()) && r.getFromId().equals(targetUseCaseId))
                    .toList();

            if (!extendRelations.isEmpty()) {
                return new Pair<>(Boolean.TRUE, extendRelations.get(0));
            } else {
                return new Pair<>(Boolean.FALSE, null);
            }
        } else if (aa.startsWith("<<inherit>>")) {
            // TODO
            System.err.println("Not implemented!");
        }
        return new Pair<>(Boolean.FALSE, null);
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error during generating specification");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
