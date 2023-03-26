package sl.fside.ui.editors.activityDiagramEditor.ownImpl;

import de.jensd.fx.glyphs.*;
import de.jensd.fx.glyphs.fontawesome.*;
import io.github.eckig.grapheditor.*;
import io.github.eckig.grapheditor.core.connectors.*;
import io.github.eckig.grapheditor.model.*;
import io.github.eckig.grapheditor.utils.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;

import java.util.*;

/**
 * The default node skin. Uses a {@link ResizableBox}.
 *
 * <p>
 * If a node uses this skin its connectors must have one of the 8 types defined in {@link DefaultConnectorTypes}. If a
 * connector does not have one of these types, it will be set to <b>left-input</b>.
 * </p>
 *
 * <p>
 * Connectors are evenly spaced along the sides of the node according to their type.
 * </p>
 */
public class OwnDefaultNodeSkin extends GNodeSkin {

    private static final String STYLE_CLASS_BORDER = "default-node-border";
    private static final String STYLE_CLASS_BACKGROUND = "default-node-background";
    private static final String STYLE_CLASS_SELECTION_HALO = "default-node-selection-halo";

    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

    private static final double HALO_OFFSET = 5;
    private static final double HALO_CORNER_SIZE = 10;

    private static final double MINOR_POSITIVE_OFFSET = 2;
    private static final double MINOR_NEGATIVE_OFFSET = -3;

    private static final double MIN_WIDTH = 41;
    private static final double MIN_HEIGHT = 41;

    private final Rectangle selectionHalo = new Rectangle();

    private final List<GConnectorSkin> topConnectorSkins = new ArrayList<>();
    private final List<GConnectorSkin> rightConnectorSkins = new ArrayList<>();
    private final List<GConnectorSkin> bottomConnectorSkins = new ArrayList<>();
    private final List<GConnectorSkin> leftConnectorSkins = new ArrayList<>();

    // Border and background are separated into 2 rectangles so they can have different effects applied to them.
    private final Rectangle border = new Rectangle();
    private final Rectangle background = new Rectangle();

    /**
     * Creates a new default node skin instance.
     *
     * @param node the {@link GNode} the skin is being created for
     */
    public OwnDefaultNodeSkin(final GNode node) {
        super(node);

        performChecks();

        background.widthProperty().bind(border.widthProperty().subtract(border.strokeWidthProperty().multiply(2)));
        background.heightProperty().bind(border.heightProperty().subtract(border.strokeWidthProperty().multiply(2)));

        border.widthProperty().bind(getRoot().widthProperty());
        border.heightProperty().bind(getRoot().heightProperty());

        border.getStyleClass().setAll(STYLE_CLASS_BORDER);
        background.getStyleClass().setAll(STYLE_CLASS_BACKGROUND);

        getRoot().getChildren().addAll(border, background);
        getRoot().setMinSize(MIN_WIDTH, MIN_HEIGHT);

        background.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::filterMouseDragged);

        addSelectionHalo();
    }

    @Override
    public void setConnectorSkins(final List<GConnectorSkin> connectorSkins) {

        removeAllConnectors();

        topConnectorSkins.clear();
        rightConnectorSkins.clear();
        bottomConnectorSkins.clear();
        leftConnectorSkins.clear();

        if (connectorSkins != null) {
            for (final GConnectorSkin connectorSkin : connectorSkins) {

                final String type = connectorSkin.getItem().getType();

                if (DefaultConnectorTypes.isTop(type)) {
                    topConnectorSkins.add(connectorSkin);
                } else if (DefaultConnectorTypes.isRight(type)) {
                    rightConnectorSkins.add(connectorSkin);
                } else if (DefaultConnectorTypes.isBottom(type)) {
                    bottomConnectorSkins.add(connectorSkin);
                } else if (DefaultConnectorTypes.isLeft(type)) {
                    leftConnectorSkins.add(connectorSkin);
                }

                getRoot().getChildren().add(connectorSkin.getRoot());
            }
        }

        layoutConnectors();
    }

    @Override
    public void layoutConnectors() {
        layoutAllConnectors();
        layoutSelectionHalo();
    }

    @Override
    public Point2D getConnectorPosition(final GConnectorSkin connectorSkin) {

        final Node connectorRoot = connectorSkin.getRoot();

        final Side side = DefaultConnectorTypes.getSide(connectorSkin.getItem().getType());

        // The following logic is required because the connectors are offset slightly from the node edges.
        final double x, y;
        if (side.equals(Side.LEFT)) {
            x = 0;
            y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;
        } else if (side.equals(Side.RIGHT)) {
            x = getRoot().getWidth();
            y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;
        } else if (side.equals(Side.TOP)) {
            x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
            y = 0;
        } else {
            x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
            y = getRoot().getHeight();
        }

        return new Point2D(x, y);
    }

    /**
     * Checks that the node and its connectors have the correct values to be displayed using this skin.
     */
    private void performChecks() {
        for (final GConnector connector : getItem().getConnectors()) {
            if (!DefaultConnectorTypes.isValid(connector.getType())) {
                System.out.println(
                        "Connector type '{}' not recognized, setting to 'left-input'." + connector.getType());
                connector.setType(DefaultConnectorTypes.LEFT_INPUT);
            }
        }
    }

    /**
     * Lays out the node's connectors.
     */
    private void layoutAllConnectors() {

        layoutConnectors(topConnectorSkins, false, 0);
        layoutConnectors(rightConnectorSkins, true, getRoot().getWidth());
        layoutConnectors(bottomConnectorSkins, false, getRoot().getHeight());
        layoutConnectors(leftConnectorSkins, true, 0);
    }

    /**
     * Lays out the given connector skins in a horizontal or vertical direction at the given offset.
     *
     * @param connectorSkins the skins to lay out
     * @param vertical       {@code true} to lay out vertically, {@code false} to lay out horizontally
     * @param offset         the offset in the other dimension that the skins are layed out in
     */
    private void layoutConnectors(final List<GConnectorSkin> connectorSkins, final boolean vertical,
                                  final double offset) {

        final int count = connectorSkins.size();

        for (int i = 0; i < count; i++) {

            final GConnectorSkin skin = connectorSkins.get(i);
            final Node root = skin.getRoot();

            if (vertical) {

                final double offsetY = getRoot().getHeight() / (count + 1);
                final double offsetX = getMinorOffsetX(skin.getItem());

                root.setLayoutX(GeometryUtils.moveOnPixel(offset - skin.getWidth() / 2 + offsetX));
                root.setLayoutY(GeometryUtils.moveOnPixel((i + 1) * offsetY - skin.getHeight() / 2));

            } else {

                final double offsetX = getRoot().getWidth() / (count + 1);
                final double offsetY = getMinorOffsetY(skin.getItem());

                root.setLayoutX(GeometryUtils.moveOnPixel((i + 1) * offsetX - skin.getWidth() / 2));
                root.setLayoutY(GeometryUtils.moveOnPixel(offset - skin.getHeight() / 2 + offsetY));
            }
        }
    }

    /**
     * Adds the selection halo and initializes some of its values.
     */
    private void addSelectionHalo() {
        getRoot().getChildren().add(selectionHalo);

        List<String> atomicActivities = NodesManager.getInstance().getCurrentAtomicActivities();
        List<String> patternNames = new ArrayList<>(
                Arrays.asList("Seq", "Branch", "BranchRe", "Concur", "ConcurRe", "Cond", "Para", "Loop"));

        String currentNodeType = NodesManager.getInstance().getCurrentNodeType();
        ObservableList<String> options = FXCollections.observableArrayList(atomicActivities);
        options.addAll(patternNames);
        switch (currentNodeType) {
            case "Seq" -> {
                VBox seqVBox = createSeqPattern(currentNodeType, options, false, null, null);
                getRoot().getChildren().add(seqVBox);
                NodesManager.getInstance().setMain(seqVBox);
                NodesManager.getInstance().setMainName("Seq");
            }
            case "Branch" -> {
                VBox branchVBox = createBranchPattern(currentNodeType, options, false, null, null);
                getRoot().getChildren().add(branchVBox);
                NodesManager.getInstance().setMain(branchVBox);
                NodesManager.getInstance().setMainName("Branch");
            }
            case "BranchRe" -> {
                VBox branchReVBox = createBranchRePattern(currentNodeType, options, false, null, null);
                getRoot().getChildren().add(branchReVBox);
                NodesManager.getInstance().setMain(branchReVBox);
                NodesManager.getInstance().setMainName("BranchRe");
            }
            case "Cond" -> {
                VBox condVBox = createCondPattern(currentNodeType, options, false, null, null);
                getRoot().getChildren().add(condVBox);
                NodesManager.getInstance().setMain(condVBox);
                NodesManager.getInstance().setMainName("Cond");
            }
            case "Para" -> {
                VBox paraVBox = createParaPattern(currentNodeType, options, false, null, null);
                getRoot().getChildren().add(paraVBox);
                NodesManager.getInstance().setMain(paraVBox);
                NodesManager.getInstance().setMainName("Para");
            }
            case "Concur" -> {
                VBox concurVBox = createConcurPattern(currentNodeType, options, false, null, null);
                getRoot().getChildren().add(concurVBox);
                NodesManager.getInstance().setMain(concurVBox);
                NodesManager.getInstance().setMainName("Concur");
            }
            case "ConcurRe" -> {
                VBox concurReVBox = createConcurRePattern(currentNodeType, options, false, null, null);
                getRoot().getChildren().add(concurReVBox);
                NodesManager.getInstance().setMain(concurReVBox);
                NodesManager.getInstance().setMainName("ConcurRe");
            }
            case "Loop" -> {
                VBox loopVBox = createLoopPattern(currentNodeType, options, false, null, null);
                getRoot().getChildren().add(loopVBox);
                NodesManager.getInstance().setMain(loopVBox);
                NodesManager.getInstance().setMainName("Loop");
            }
        }

        selectionHalo.setManaged(false);
        selectionHalo.setMouseTransparent(false);
        selectionHalo.setVisible(false);

        selectionHalo.setLayoutX(-HALO_OFFSET);
        selectionHalo.setLayoutY(-HALO_OFFSET);

        selectionHalo.getStyleClass().add(STYLE_CLASS_SELECTION_HALO);
    }

    private VBox createSeqPattern(String patternTypeName, ObservableList<String> options, boolean isInnerPattern,
                                  String outerFormulaName, String outerPatternName) {
        Color borderColor = randomColor();

        // shape to represent selected Color
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setFill(null);
        }

        // title
        Text type = new Text("        " + patternTypeName + " ");

        // clear button
        Button clearFormulaButton = new Button();
        GlyphsDude.setIcon(clearFormulaButton, FontAwesomeIcon.TIMES_CIRCLE);
        addContextMenuToButton(clearFormulaButton, patternTypeName, outerFormulaName, outerPatternName, options,
                borderColor);
        clearFormulaButton.setDisable(!isInnerPattern);

        // hBox for rectangle, title and clear button
        HBox hBoxTitleAndClearButton = new HBox();
        hBoxTitleAndClearButton.getChildren().addAll(type, rectangle, new Text(" "), clearFormulaButton);
        hBoxTitleAndClearButton.setAlignment(Pos.CENTER);

        // a1
        VBox a1vBox = createFormulaVBox(patternTypeName, "a1", options, borderColor, false);

        // connection visualization
        MyArrow myArrow = new MyArrow(0, 0, 0, 100);
        myArrow.setHeadAVisible(false);

        // a2
        VBox a2vBox = createFormulaVBox(patternTypeName, "a2", options, borderColor, false);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        if (isInnerPattern) {
            addEntryArrow(vBox, outerPatternName, outerFormulaName);
        }
        vBox.getChildren().addAll(hBoxTitleAndClearButton, a1vBox, myArrow, a2vBox);
        if (isInnerPattern) {
            addOutArrow(vBox, outerPatternName, outerFormulaName);
        }
        addBorderToVBox(vBox, borderColor);
        return vBox;
    }

    // pionowa linia uzupełniająca pustą przestrzeń, gdy formuła obok ma większą wysokość
    private void addFillingSpaceLine(VBox vBox) {
        Line verticalLine = new Line(0, 0, 0, 5);

        VBox vBoxWithLine = new VBox();
        vBoxWithLine.setAlignment(Pos.CENTER);
        vBoxWithLine.getChildren().add(verticalLine);

        // dodaje VBox z linią na początek
        vBox.getChildren().add(0, vBoxWithLine);

        // przypadek zagnieżdżonego wzorca
        if (vBox.getChildren().get(1) instanceof VBox) {
            vBox.heightProperty().addListener((observable, oldValue, newValue) -> {
                double parentVBoxHeight = vBox.getHeight();
                double childVBoxHeight = ((VBox) vBox.getChildren().get(1)).getHeight();
                if (parentVBoxHeight > 10 && childVBoxHeight >
                        10) { // zapobiega przypadkowi, gdy któryś VBox wynosi 0 (bezpośrednio po stworzeniu)
                    double lineLength =
                            (parentVBoxHeight - childVBoxHeight > 20) ? parentVBoxHeight - childVBoxHeight - 10 : 0;
                    vBoxWithLine.getChildren().set(0, new Line(0, 0, 0, lineLength));
                }
            });

            ((VBox) vBox.getChildren().get(1)).heightProperty().addListener((observable, oldValue, newValue) -> {
                double parentVBoxHeight = vBox.getHeight();
                double childVBoxHeight = ((VBox) vBox.getChildren().get(1)).getHeight();
                if (parentVBoxHeight > 10 && childVBoxHeight > 10) {
                    double lineLength =
                            (parentVBoxHeight - childVBoxHeight > 20) ? parentVBoxHeight - childVBoxHeight - 10 : 0;
                    vBoxWithLine.getChildren().set(0, new Line(0, 0, 0, lineLength));
                }
            });

            // przypadek atomicznego wyboru
        } else if (vBox.getChildren().get(1) instanceof ComboBox<?>) {
            vBox.heightProperty().addListener((observable, oldValue, newValue) -> {
                if (vBox.getChildren().size() >= 2 && vBox.getChildren().get(1) instanceof ComboBox<?>) {
                    double parentVBoxHeight = vBox.getHeight();
                    double childVBoxHeight = ((ComboBox<?>) vBox.getChildren().get(1)).getHeight();
                    if (parentVBoxHeight > 10 && childVBoxHeight > 10) {
                        double lineLength =
                                (parentVBoxHeight - childVBoxHeight > 20) ? parentVBoxHeight - childVBoxHeight - 10 : 0;
                        vBoxWithLine.getChildren().set(0, new Line(0, 0, 0, lineLength));
                    }
                }
            });

            ((ComboBox<?>) vBox.getChildren().get(1)).heightProperty().addListener((observable, oldValue, newValue) -> {
                if (vBox.getChildren().size() >= 2 && vBox.getChildren().get(1) instanceof ComboBox<?>) {
                    double parentVBoxHeight = vBox.getHeight();
                    double childVBoxHeight = ((ComboBox<?>) vBox.getChildren().get(1)).getHeight();
                    if (parentVBoxHeight > 10 && childVBoxHeight > 10) {
                        double lineLength =
                                (parentVBoxHeight - childVBoxHeight > 20) ? parentVBoxHeight - childVBoxHeight - 10 : 0;
                        vBoxWithLine.getChildren().set(0, new Line(0, 0, 0, lineLength));
                    }
                }
            });
        } else {
            System.out.println("Inny przypadek!");
        }
    }

    private void addBorderToVBox(VBox vBox, Color borderColor) {
        Border border = new Border(
                new BorderStroke(borderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
        vBox.setBorder(border);
        NodesManager.getInstance().addBorderOnActivityDiagram(vBox, border);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            vBox.setBorder(null);
        }
    }

    private boolean addEntryArrow(Pane vBox, String outerPatternName, String outerFormulaName) {
        if (outerPatternName.equals("Seq") && outerFormulaName.equals("a2")) {
            MyArrow myArrowFirst = new MyArrow(0, 0, 0, 70);
            myArrowFirst.setHeadAVisible(false);
            vBox.getChildren().add(myArrowFirst);
            return true;
        } else if ((outerPatternName.equals("Branch") || outerPatternName.equals("Concur")) &&
                (outerFormulaName.equals("a2") || outerFormulaName.equals("a3"))) {
            MyArrow myArrowFirst = new MyArrow(0, 0, 0, 70);
            myArrowFirst.setHeadAVisible(false);
            vBox.getChildren().add(myArrowFirst);
            return true;
        } else if ((outerPatternName.equals("BranchRe") || outerPatternName.equals("ConcurRe")) &&
                outerFormulaName.equals("a3")) {
            MyArrow myArrowFirst = new MyArrow(0, 0, 0, 70);
            myArrowFirst.setHeadAVisible(false);
            vBox.getChildren().add(myArrowFirst);
            return true;
        } else if ((outerPatternName.equals("Cond") || outerPatternName.equals("Para")) &&
                (outerFormulaName.equals("a2") || outerFormulaName.equals("a3") || outerFormulaName.equals("a4"))) {
            MyArrow myArrowFirst = new MyArrow(0, 0, 0, 70);
            myArrowFirst.setHeadAVisible(false);
            vBox.getChildren().add(myArrowFirst);
            return true;
        } else if (outerPatternName.equals("Loop") &&
                (outerFormulaName.equals("a2") || outerFormulaName.equals("a3") || outerFormulaName.equals("a4"))) {
            MyArrow myArrowFirst = new MyArrow(0, 0, 0, 70);
            myArrowFirst.setHeadAVisible(false);
            vBox.getChildren().add(myArrowFirst);
            return true;
        }
        return false;
    }

    private boolean addOutArrow(Pane vBox, String outerPatternName, String outerFormulaName) {
        if (outerPatternName.equals("Seq") && outerFormulaName.equals("a1")) {
            MyArrow myArrowLast = new MyArrow(0, 0, 0, 70);
            myArrowLast.setHeadAVisible(false);
            vBox.getChildren().add(myArrowLast);
            return true;
        } else if ((outerPatternName.equals("Branch") || outerPatternName.equals("Concur")) &&
                outerFormulaName.equals("a1")) {
            MyArrow myArrowLast = new MyArrow(0, 0, 0, 70);
            myArrowLast.setHeadAVisible(false);
            vBox.getChildren().add(myArrowLast);
            return true;
        } else if ((outerPatternName.equals("BranchRe") || outerPatternName.equals("ConcurRe")) &&
                (outerFormulaName.equals("a1") || outerFormulaName.equals("a2"))) {
            MyArrow myArrowLast = new MyArrow(0, 0, 0, 70);
            myArrowLast.setHeadAVisible(false);
            vBox.getChildren().add(myArrowLast);
            return true;
        } else if ((outerPatternName.equals("Cond") || outerPatternName.equals("Para")) &&
                (outerFormulaName.equals("a1") || outerFormulaName.equals("a2") || outerFormulaName.equals("a3"))) {
            MyArrow myArrowLast = new MyArrow(0, 0, 0, 70);
            myArrowLast.setHeadAVisible(false);
            vBox.getChildren().add(myArrowLast);
            return true;
        } else if (outerPatternName.equals("Loop") &&
                (outerFormulaName.equals("a1") || outerFormulaName.equals("a2") || outerFormulaName.equals("a4"))) {
            MyArrow myArrowLast = new MyArrow(0, 0, 0, 70);
            myArrowLast.setHeadAVisible(false);
            vBox.getChildren().add(myArrowLast);
            return true;
        }
        return false;
    }

    private ComboBox<String> createDropdownMenu(String formulaName, ObservableList<String> options) {
        ComboBox<String> a1Dropdown = new ComboBox<>(options);
        a1Dropdown.setPromptText(formulaName);
        a1Dropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(formulaName);
                } else {
                    setText(item);
                }
            }
        });
        return a1Dropdown;
    }

    private void addContextMenuToButton(Button button, String patternName, String formulaName, String outerPatternName,
                                        ObservableList<String> options, Color borderColor) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem clearItem =
                new MenuItem("Remove " + patternName + " (Formula " + formulaName + " of " + outerPatternName + ")");
        contextMenu.getItems().addAll(clearItem);

        // showing context menu (to clear formula)
        button.setOnAction(event -> {
            contextMenu.show(button, Side.TOP, 0, 0);
            Node target = (Node) event.getTarget();
            Parent parentVBox =
                    target.getParent().getParent(); // button is inside HBox inside VBox ; contains whole formula
            parentVBox.setStyle("-fx-background-color: lightgray;");
            if (!(parentVBox instanceof VBox)) {
                System.out.println("Parent should be VBox, but is: " + parentVBox);
                return;
            }
            if (!(parentVBox.getParent() instanceof VBox)) {
                System.out.println("ParentParent should be VBox, but is: " + parentVBox.getParent());
                return;
            }
            if (!(parentVBox.getParent().getParent() instanceof VBox) &&
                    !(parentVBox.getParent().getParent() instanceof HBox)) {
                System.out.println(
                        "ParentParentParent should be VBox or HBox, but is: " + parentVBox.getParent().getParent());
                return;
            }

            // return to base background color
            contextMenu.setOnHidden(event2 -> {
                parentVBox.setStyle("");
            });

            // clearing selected formula (ex. "A1")
            clearItem.setOnAction(event3 -> {

                // nowy VBox, zawierający nowy dropdown ; zastąpi stary VBox
                VBox newVBox = createFormulaVBox(outerPatternName, formulaName, options, borderColor, true);
                HBox.setHgrow(newVBox, Priority.ALWAYS);

                // podmiana koloru borderu, aby zgadzał się z obecnym
                Border parentBorder =
                        NodesManager.getInstance().getBordersOnActivityDiagram().get(((VBox) parentVBox.getParent()));
                NodesManager.getInstance().addBorderOnActivityDiagram(newVBox, parentBorder);
                if (NodesManager.getInstance().isShowColorsOnDiagram()) {
                    newVBox.setBorder(parentBorder);
                } else {
                    newVBox.setBorder(null);
                }

                // podmiana starego VBox na nowy
                Pane parentOfParentPane = (Pane) parentVBox.getParent().getParent();
                int oldVBoxIndex = parentOfParentPane.getChildren().indexOf(parentVBox.getParent());
                parentOfParentPane.getChildren().set(oldVBoxIndex, newVBox);
                if (parentOfParentPane instanceof HBox) {
                    parentOfParentPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                        newVBox.setMaxWidth(parentOfParentPane.getWidth() / 2);
                    });
                }
            });
        });
    }

    private VBox createBranchPattern(String patternTypeName, ObservableList<String> options, boolean isInnerPattern,
                                     String outerFormulaName, String outerPatternName) {
        Color borderColor = randomColor();

        // shape to represent selected Color
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setFill(null);
        }

        // title
        Text type = new Text("        " + patternTypeName + " ");

        // clear button
        Button clearFormulaButton = new Button();
        GlyphsDude.setIcon(clearFormulaButton, FontAwesomeIcon.TIMES_CIRCLE);
        addContextMenuToButton(clearFormulaButton, patternTypeName, outerFormulaName, outerPatternName, options,
                borderColor);
        clearFormulaButton.setDisable(!isInnerPattern);

        // hBox for rectangle, title and clear button
        HBox hBoxTitleAndClearButton = new HBox();
        hBoxTitleAndClearButton.getChildren().addAll(type, rectangle, new Text(" "), clearFormulaButton);
        hBoxTitleAndClearButton.setAlignment(Pos.CENTER);

        // a1
        VBox a1vBox = createFormulaVBox(patternTypeName, "a1", options, borderColor, false);

        // connection visualization
        MyArrow myArrow1 = new MyArrow(100, 0, 0, 50);
        myArrow1.setHeadAVisible(false);
        MyArrow myArrow2 = new MyArrow(100, 0, 200, 50);
        myArrow2.setHeadAVisible(false);
        HBox myArrow = new HBox();
        myArrow.setAlignment(Pos.CENTER);
        Text plus = new Text("+");
        plus.setStyle("-fx-font: 20 arial;");
        Text minus = new Text("-");
        minus.setStyle("-fx-font: 23 arial;");
        myArrow.getChildren().addAll(plus, myArrow1, myArrow2, minus);
        myArrow.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow1.setX1(newValue.doubleValue() / 2);
            myArrow2.setX1(newValue.doubleValue() / 2);
            myArrow1.setX2(newValue.doubleValue() / 4.0 - 32.0);
            myArrow2.setX2(3.0 * newValue.doubleValue() / 4.0 + 32.0);
        });

        // a2
        VBox a2vBox = createFormulaVBox(patternTypeName, "a2", options, borderColor, true);

        // a3
        VBox a3vBox = createFormulaVBox(patternTypeName, "a3", options, borderColor, true);

        // HBox agregujący A2 i A3 były obok siebie
        HBox a2a3hBox = new HBox();
        a2a3hBox.setAlignment(Pos.CENTER);
        a2a3hBox.getChildren().addAll(a2vBox, a3vBox);

        // ustawia podział między A2 i A3 w połowie dostępnego miejsca
        HBox.setHgrow(a2vBox, Priority.ALWAYS);
        HBox.setHgrow(a3vBox, Priority.ALWAYS);
        a2a3hBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            a2vBox.setMaxWidth(a2a3hBox.getWidth() / 2);
            a3vBox.setMaxWidth(a2a3hBox.getWidth() / 2);
        });

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        if (isInnerPattern) {
            addEntryArrow(vBox, outerPatternName, outerFormulaName);
        }
        vBox.getChildren().addAll(hBoxTitleAndClearButton, a1vBox, myArrow, a2a3hBox);
        if (isInnerPattern) {
            addTwoOutArrows(vBox, outerPatternName, outerFormulaName);
        }
        addBorderToVBox(vBox, borderColor);
        return vBox;
    }

    private void addTwoOutArrows(VBox vBox, String outerPatternName, String outerFormulaName) {
        VBox vboxWithLeftOutArrow = new VBox();
        vboxWithLeftOutArrow.setAlignment(Pos.CENTER);
        if (addOutArrow(vboxWithLeftOutArrow, outerPatternName, outerFormulaName)) {
            VBox vboxWithRightOutArrow = new VBox();
            vboxWithRightOutArrow.setAlignment(Pos.CENTER);
            addOutArrow(vboxWithRightOutArrow, outerPatternName, outerFormulaName);

            Line horizontalLine = new Line(0, 0, 100, 0);

            HBox hBoxWithOutArrows = new HBox();
            hBoxWithOutArrows.setAlignment(Pos.CENTER);
            hBoxWithOutArrows.getChildren().addAll(vboxWithLeftOutArrow, vboxWithRightOutArrow);

            HBox.setHgrow(vboxWithLeftOutArrow, Priority.ALWAYS);
            HBox.setHgrow(vboxWithRightOutArrow, Priority.ALWAYS);

            hBoxWithOutArrows.widthProperty().addListener((observable, oldValue, newValue) -> {
                vboxWithLeftOutArrow.setMaxWidth(hBoxWithOutArrows.getWidth() / 2);
                vboxWithRightOutArrow.setMaxWidth(hBoxWithOutArrows.getWidth() / 2);
                horizontalLine.setEndX(hBoxWithOutArrows.getWidth() / 2);
            });
            vBox.getChildren().addAll(hBoxWithOutArrows, horizontalLine);
        }
    }

    private void addTwoEntryArrows(VBox vBox, String outerPatternName, String outerFormulaName) {
        VBox vboxWithLeftEntryArrow = new VBox();
        vboxWithLeftEntryArrow.setAlignment(Pos.CENTER);
        if (addEntryArrow(vboxWithLeftEntryArrow, outerPatternName, outerFormulaName)) {
            VBox vboxWithRightEntryArrow = new VBox();
            vboxWithRightEntryArrow.setAlignment(Pos.CENTER);
            addEntryArrow(vboxWithRightEntryArrow, outerPatternName, outerFormulaName);

            Line horizontalLine = new Line(0, 0, 100, 0);

            HBox hBoxWithEntryArrows = new HBox();
            hBoxWithEntryArrows.setAlignment(Pos.CENTER);
            hBoxWithEntryArrows.getChildren().addAll(vboxWithLeftEntryArrow, vboxWithRightEntryArrow);

            HBox.setHgrow(vboxWithLeftEntryArrow, Priority.ALWAYS);
            HBox.setHgrow(vboxWithRightEntryArrow, Priority.ALWAYS);

            hBoxWithEntryArrows.widthProperty().addListener((observable, oldValue, newValue) -> {
                vboxWithLeftEntryArrow.setMaxWidth(hBoxWithEntryArrows.getWidth() / 2);
                vboxWithRightEntryArrow.setMaxWidth(hBoxWithEntryArrows.getWidth() / 2);
                horizontalLine.setEndX(hBoxWithEntryArrows.getWidth() / 2);
            });
            vBox.getChildren().addAll(horizontalLine, hBoxWithEntryArrows);
        }
    }

    private VBox createBranchRePattern(String patternTypeName, ObservableList<String> options, boolean isInnerPattern,
                                       String outerFormulaName, String outerPatternName) {
        Color borderColor = randomColor();

        // shape to represent selected Color
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setFill(null);
        }

        // title
        Text type = new Text("        " + patternTypeName + " ");

        // clear button
        Button clearFormulaButton = new Button();
        GlyphsDude.setIcon(clearFormulaButton, FontAwesomeIcon.TIMES_CIRCLE);
        addContextMenuToButton(clearFormulaButton, patternTypeName, outerFormulaName, outerPatternName, options,
                borderColor);
        clearFormulaButton.setDisable(!isInnerPattern);

        // hBox for rectangle, title and clear button
        HBox hBoxTitleAndClearButton = new HBox();
        hBoxTitleAndClearButton.getChildren().addAll(type, rectangle, new Text(" "), clearFormulaButton);
        hBoxTitleAndClearButton.setAlignment(Pos.CENTER);

        // a1
        VBox a1vBox = createFormulaVBox(patternTypeName, "a1", options, borderColor, true);

        // a2
        VBox a2vBox = createFormulaVBox(patternTypeName, "a2", options, borderColor, true);

        // HBox agregujący A1 i A2 były obok siebie
        HBox a1a2hBox = new HBox();
        a1a2hBox.setAlignment(Pos.CENTER);
        a1a2hBox.getChildren().addAll(a1vBox, a2vBox);

        // ustawia podział między A1 i A2 w połowie dostępnego miejsca
        HBox.setHgrow(a1vBox, Priority.ALWAYS);
        HBox.setHgrow(a2vBox, Priority.ALWAYS);
        a1a2hBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            a1vBox.setMaxWidth(a1a2hBox.getWidth() / 2);
            a2vBox.setMaxWidth(a1a2hBox.getWidth() / 2);
        });

        // connection visualization
        MyArrow myArrow1 = new MyArrow(0, 0, 100, 50);
        myArrow1.setHeadAVisible(false);
        MyArrow myArrow2 = new MyArrow(200, 0, 100, 50);
        myArrow2.setHeadAVisible(false);
        HBox myArrow = new HBox();
        myArrow.setAlignment(Pos.CENTER);
        myArrow.getChildren().addAll(myArrow1, myArrow2);
        myArrow.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow1.setX1(newValue.doubleValue() / 4.0 - 30.0);
            myArrow2.setX1(3.0 * newValue.doubleValue() / 4.0 + 30.0);
            myArrow1.setX2(newValue.doubleValue() / 2);
            myArrow2.setX2(newValue.doubleValue() / 2);
        });

        // a3
        VBox a3vBox = createFormulaVBox(patternTypeName, "a3", options, borderColor, false);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        if (isInnerPattern) {
            addTwoEntryArrows(vBox, outerPatternName, outerFormulaName);
        }
        vBox.getChildren().addAll(hBoxTitleAndClearButton, a1a2hBox, myArrow, a3vBox);
        if (isInnerPattern) {
            addOutArrow(vBox, outerPatternName, outerFormulaName);
        }
        addBorderToVBox(vBox, borderColor);
        return vBox;
    }

    private VBox createFormulaVBox(String patternTypeName, String formulaName, ObservableList<String> options,
                                   Color borderColor, boolean addFillingSpaceLine) {
        ComboBox<String> dropdown = createDropdownMenu(formulaName, options);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(dropdown);
        if (addFillingSpaceLine) addFillingSpaceLine(vBox);
//        Border a3vBoxBorder = new Border(
//                new BorderStroke(borderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
//        vBox.setBorder(a3vBoxBorder);
//        NodesManager.getInstance().addBorderOnActivityDiagram(vBox, a3vBoxBorder);
//        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
//            vBox.setBorder(null);
//        }

        setDropdownOnAction(dropdown, vBox, options, patternTypeName);
        return vBox;
    }

    private VBox createCondPattern(String patternTypeName, ObservableList<String> options, boolean isInnerPattern,
                                   String outerFormulaName, String outerPatternName) {
        Color borderColor = randomColor();

        // shape to represent selected Color
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setFill(null);
        }

        // title
        Text type = new Text("        " + patternTypeName + " ");

        // clear button
        Button clearFormulaButton = new Button();
        GlyphsDude.setIcon(clearFormulaButton, FontAwesomeIcon.TIMES_CIRCLE);
        addContextMenuToButton(clearFormulaButton, patternTypeName, outerFormulaName, outerPatternName, options,
                borderColor);
        clearFormulaButton.setDisable(!isInnerPattern);

        // hBox for rectangle, title and clear button
        HBox hBoxTitleAndClearButton = new HBox();
        hBoxTitleAndClearButton.getChildren().addAll(type, rectangle, new Text(" "), clearFormulaButton);
        hBoxTitleAndClearButton.setAlignment(Pos.CENTER);

        // a1
        VBox a1vBox = createFormulaVBox(patternTypeName, "a1", options, borderColor, false);

        // connection visualization
        MyArrow myArrow1 = new MyArrow(100, 0, 0, 50);
        myArrow1.setHeadAVisible(false);
        MyArrow myArrow2 = new MyArrow(100, 0, 200, 50);
        myArrow2.setHeadAVisible(false);
        HBox myArrow = new HBox();
        myArrow.setAlignment(Pos.CENTER);
        Text plus = new Text("+");
        plus.setStyle("-fx-font: 20 arial;");
        Text minus = new Text("-");
        minus.setStyle("-fx-font: 23 arial;");
        myArrow.getChildren().addAll(plus, myArrow1, myArrow2, minus);
        myArrow.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow1.setX1(newValue.doubleValue() / 2);
            myArrow2.setX1(newValue.doubleValue() / 2);
            myArrow1.setX2(newValue.doubleValue() / 4.0 - 35.0);
            myArrow2.setX2(3.0 * newValue.doubleValue() / 4.0 + 35.0);
        });

        // a2
        VBox a2vBox = createFormulaVBox(patternTypeName, "a2", options, borderColor, true);

        // a3
        VBox a3vBox = createFormulaVBox(patternTypeName, "a3", options, borderColor, true);

        // HBox agregujący A2 i A3 były obok siebie
        HBox a2a3hBox = new HBox();
        a2a3hBox.setAlignment(Pos.CENTER);
        a2a3hBox.getChildren().addAll(a2vBox, a3vBox);

        // ustawia podział między A2 i A3 w połowie dostępnego miejsca
        HBox.setHgrow(a2vBox, Priority.ALWAYS);
        HBox.setHgrow(a3vBox, Priority.ALWAYS);
        a2a3hBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            a2vBox.setMaxWidth(a2a3hBox.getWidth() / 2);
            a3vBox.setMaxWidth(a2a3hBox.getWidth() / 2);
        });

        // connection visualization
        MyArrow myArrow3 = new MyArrow(0, 0, 100, 50);
        myArrow3.setHeadAVisible(false);
        MyArrow myArrow4 = new MyArrow(200, 0, 100, 50);
        myArrow4.setHeadAVisible(false);
        HBox myArrow_2 = new HBox();
        myArrow_2.setAlignment(Pos.CENTER);
        myArrow_2.getChildren().addAll(myArrow3, myArrow4);
        myArrow_2.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow3.setX1(newValue.doubleValue() / 4.0 - 35.0);
            myArrow4.setX1(3.0 * newValue.doubleValue() / 4.0 + 35.0);
            myArrow3.setX2(newValue.doubleValue() / 2);
            myArrow4.setX2(newValue.doubleValue() / 2);
        });

        // a4
        VBox a4vBox = createFormulaVBox(patternTypeName, "a4", options, borderColor, false);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        if (isInnerPattern) {
            addEntryArrow(vBox, outerPatternName, outerFormulaName);
        }
        vBox.getChildren().addAll(hBoxTitleAndClearButton, a1vBox, myArrow, a2a3hBox, myArrow_2, a4vBox);
        if (isInnerPattern) {
            addOutArrow(vBox, outerPatternName, outerFormulaName);
        }
        addBorderToVBox(vBox, borderColor);
        return vBox;
    }

    private VBox createParaPattern(String patternTypeName, ObservableList<String> options, boolean isInnerPattern,
                                   String outerFormulaName, String outerPatternName) {
        Color borderColor = randomColor();

        // shape to represent selected Color
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setFill(null);
        }

        // title
        Text type = new Text("        " + patternTypeName + " ");

        // clear button
        Button clearFormulaButton = new Button();
        GlyphsDude.setIcon(clearFormulaButton, FontAwesomeIcon.TIMES_CIRCLE);
        addContextMenuToButton(clearFormulaButton, patternTypeName, outerFormulaName, outerPatternName, options,
                borderColor);
        clearFormulaButton.setDisable(!isInnerPattern);

        // hBox for rectangle, title and clear button
        HBox hBoxTitleAndClearButton = new HBox();
        hBoxTitleAndClearButton.getChildren().addAll(type, rectangle, new Text(" "), clearFormulaButton);
        hBoxTitleAndClearButton.setAlignment(Pos.CENTER);

        // a1
        VBox a1vBox = createFormulaVBox(patternTypeName, "a1", options, borderColor, false);

        // connection visualization
        MyArrow myArrow1 = new MyArrow(0, 0, 0, 70);
        myArrow1.setHeadAVisible(false);
        MyArrow myArrow2 = new MyArrow(100, 0, 0, 50);
        myArrow2.setHeadAVisible(false);
        MyArrow myArrow3 = new MyArrow(100, 0, 200, 50);
        myArrow3.setHeadAVisible(false);
        HBox myArrow_2_3 = new HBox();
        myArrow_2_3.setAlignment(Pos.CENTER);
        myArrow_2_3.getChildren().addAll(myArrow2, myArrow3);
        VBox myArrow = new VBox();
        myArrow.setAlignment(Pos.CENTER);
        myArrow.getChildren().addAll(myArrow1, myArrow_2_3);
        myArrow_2_3.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow2.setX1(newValue.doubleValue() / 2);
            myArrow3.setX1(newValue.doubleValue() / 2);
            myArrow2.setX2(newValue.doubleValue() / 4.0 - 35.0);
            myArrow3.setX2(3.0 * newValue.doubleValue() / 4.0 + 35.0);
        });

        // a2
        VBox a2vBox = createFormulaVBox(patternTypeName, "a2", options, borderColor, true);

        // a3
        VBox a3vBox = createFormulaVBox(patternTypeName, "a3", options, borderColor, true);

        // HBox agregujący A2 i A3 były obok siebie
        HBox a2a3hBox = new HBox();
        a2a3hBox.setAlignment(Pos.CENTER);
        a2a3hBox.getChildren().addAll(a2vBox, a3vBox);

        // ustawia podział między A2 i A3 w połowie dostępnego miejsca
        HBox.setHgrow(a2vBox, Priority.ALWAYS);
        HBox.setHgrow(a3vBox, Priority.ALWAYS);
        a2a3hBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            a2vBox.setMaxWidth(a2a3hBox.getWidth() / 2);
            a3vBox.setMaxWidth(a2a3hBox.getWidth() / 2);
        });

        // connection visualization
        MyArrow myArrow5 = new MyArrow(0, 0, 100, 50);
        myArrow5.setHeadAVisible(false);
        MyArrow myArrow6 = new MyArrow(200, 0, 100, 50);
        myArrow6.setHeadAVisible(false);
        MyArrow myArrow7 = new MyArrow(0, 0, 0, 70);
        myArrow7.setHeadAVisible(false);
        HBox myArrow_5_6 = new HBox();
        myArrow_5_6.setAlignment(Pos.CENTER);
        myArrow_5_6.getChildren().addAll(myArrow5, myArrow6);
        VBox myArrow_2 = new VBox();
        myArrow_2.setAlignment(Pos.CENTER);
        myArrow_2.getChildren().addAll(myArrow_5_6, myArrow7);
        myArrow_5_6.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow5.setX1(newValue.doubleValue() / 4.0 - 35.0);
            myArrow6.setX1(3.0 * newValue.doubleValue() / 4.0 + 35.0);
            myArrow5.setX2(newValue.doubleValue() / 2);
            myArrow6.setX2(newValue.doubleValue() / 2);
        });

        // a4
        VBox a4vBox = createFormulaVBox(patternTypeName, "a4", options, borderColor, false);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        if (isInnerPattern) {
            addEntryArrow(vBox, outerPatternName, outerFormulaName);
        }
        vBox.getChildren().addAll(hBoxTitleAndClearButton, a1vBox, myArrow, a2a3hBox, myArrow_2, a4vBox);
        if (isInnerPattern) {
            addOutArrow(vBox, outerPatternName, outerFormulaName);
        }
        addBorderToVBox(vBox, borderColor);
        return vBox;
    }

    private VBox createConcurPattern(String patternTypeName, ObservableList<String> options, boolean isInnerPattern,
                                     String outerFormulaName, String outerPatternName) {
        Color borderColor = randomColor();

        // shape to represent selected Color
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setFill(null);
        }

        // title
        Text type = new Text("        " + patternTypeName + " ");

        // clear button
        Button clearFormulaButton = new Button();
        GlyphsDude.setIcon(clearFormulaButton, FontAwesomeIcon.TIMES_CIRCLE);
        addContextMenuToButton(clearFormulaButton, patternTypeName, outerFormulaName, outerPatternName, options,
                borderColor);
        clearFormulaButton.setDisable(!isInnerPattern);

        // hBox for rectangle, title and clear button
        HBox hBoxTitleAndClearButton = new HBox();
        hBoxTitleAndClearButton.getChildren().addAll(type, rectangle, new Text(" "), clearFormulaButton);
        hBoxTitleAndClearButton.setAlignment(Pos.CENTER);

        // a1
        VBox a1vBox = createFormulaVBox(patternTypeName, "a1", options, borderColor, false);

        // connection visualization
        MyArrow myArrow1 = new MyArrow(0, 0, 0, 70);
        myArrow1.setHeadAVisible(false);
        MyArrow myArrow2 = new MyArrow(100, 0, 0, 50);
        myArrow2.setHeadAVisible(false);
        MyArrow myArrow3 = new MyArrow(100, 0, 200, 50);
        myArrow3.setHeadAVisible(false);
        HBox myArrow_2_3 = new HBox();
        myArrow_2_3.setAlignment(Pos.CENTER);
        myArrow_2_3.getChildren().addAll(myArrow2, myArrow3);
        VBox myArrow = new VBox();
        myArrow.setAlignment(Pos.CENTER);
        myArrow.getChildren().addAll(myArrow1, myArrow_2_3);
        myArrow_2_3.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow2.setX1(newValue.doubleValue() / 2);
            myArrow3.setX1(newValue.doubleValue() / 2);
            myArrow2.setX2(newValue.doubleValue() / 4.0 - 35.0);
            myArrow3.setX2(3.0 * newValue.doubleValue() / 4.0 + 35.0);
        });

        // a2
        VBox a2vBox = createFormulaVBox(patternTypeName, "a2", options, borderColor, true);

        // a3
        VBox a3vBox = createFormulaVBox(patternTypeName, "a3", options, borderColor, true);

        // HBox agregujący A2 i A3 były obok siebie
        HBox a2a3hBox = new HBox();
        a2a3hBox.setAlignment(Pos.CENTER);
        a2a3hBox.getChildren().addAll(a2vBox, a3vBox);

        // ustawia podział między A2 i A3 w połowie dostępnego miejsca
        HBox.setHgrow(a2vBox, Priority.ALWAYS);
        HBox.setHgrow(a3vBox, Priority.ALWAYS);
        a2a3hBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            a2vBox.setMaxWidth(a2a3hBox.getWidth() / 2);
            a3vBox.setMaxWidth(a2a3hBox.getWidth() / 2);
        });

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        if (isInnerPattern) {
            addEntryArrow(vBox, outerPatternName, outerFormulaName);
        }
        vBox.getChildren().addAll(hBoxTitleAndClearButton, a1vBox, myArrow, a2a3hBox);
        if (isInnerPattern) {
            addTwoOutArrows(vBox, outerPatternName, outerFormulaName);
        }
        addBorderToVBox(vBox, borderColor);
        return vBox;
    }

    private VBox createConcurRePattern(String patternTypeName, ObservableList<String> options, boolean isInnerPattern,
                                       String outerFormulaName, String outerPatternName) {
        Color borderColor = randomColor();

        // shape to represent selected Color
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setFill(null);
        }

        // title
        Text type = new Text("        " + patternTypeName + " ");

        // clear button
        Button clearFormulaButton = new Button();
        GlyphsDude.setIcon(clearFormulaButton, FontAwesomeIcon.TIMES_CIRCLE);
        addContextMenuToButton(clearFormulaButton, patternTypeName, outerFormulaName, outerPatternName, options,
                borderColor);
        clearFormulaButton.setDisable(!isInnerPattern);

        // hBox for rectangle, title and clear button
        HBox hBoxTitleAndClearButton = new HBox();
        hBoxTitleAndClearButton.getChildren().addAll(type, rectangle, new Text(" "), clearFormulaButton);
        hBoxTitleAndClearButton.setAlignment(Pos.CENTER);

        // a1
        VBox a1vBox = createFormulaVBox(patternTypeName, "a1", options, borderColor, true);

        // a2
        VBox a2vBox = createFormulaVBox(patternTypeName, "a2", options, borderColor, true);

        // HBox agregujący A1 i A2 były obok siebie
        HBox a1a2hBox = new HBox();
        a1a2hBox.setAlignment(Pos.CENTER);
        a1a2hBox.getChildren().addAll(a1vBox, a2vBox);

        // ustawia podział między A1 i A2 w połowie dostępnego miejsca
        HBox.setHgrow(a1vBox, Priority.ALWAYS);
        HBox.setHgrow(a2vBox, Priority.ALWAYS);
        a1a2hBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            a1vBox.setMaxWidth(a1a2hBox.getWidth() / 2);
            a2vBox.setMaxWidth(a1a2hBox.getWidth() / 2);
        });

        // connection visualization
        MyArrow myArrow1 = new MyArrow(0, 0, 100, 50);
        myArrow1.setHeadAVisible(false);
        MyArrow myArrow2 = new MyArrow(200, 0, 100, 50);
        myArrow2.setHeadAVisible(false);
        MyArrow myArrow3 = new MyArrow(0, 0, 0, 70);
        myArrow3.setHeadAVisible(false);
        HBox myArrow_1_2 = new HBox();
        myArrow_1_2.setAlignment(Pos.CENTER);
        myArrow_1_2.getChildren().addAll(myArrow1, myArrow2);
        VBox myArrow = new VBox();
        myArrow.setAlignment(Pos.CENTER);
        myArrow.getChildren().addAll(myArrow_1_2, myArrow3);
        myArrow_1_2.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow1.setX1(newValue.doubleValue() / 4.0 - 35.0);
            myArrow2.setX1(3.0 * newValue.doubleValue() / 4.0 + 35.0);
            myArrow1.setX2(newValue.doubleValue() / 2);
            myArrow2.setX2(newValue.doubleValue() / 2);
        });

        // a3
        VBox a3vBox = createFormulaVBox(patternTypeName, "a3", options, borderColor, false);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        if (isInnerPattern) {
            addTwoEntryArrows(vBox, outerPatternName, outerFormulaName);
        }
        vBox.getChildren().addAll(hBoxTitleAndClearButton, a1a2hBox, myArrow, a3vBox);
        if (isInnerPattern) {
            addOutArrow(vBox, outerPatternName, outerFormulaName);
        }
        addBorderToVBox(vBox, borderColor);
        return vBox;
    }

    private VBox createLoopPattern(String patternTypeName, ObservableList<String> options, boolean isInnerPattern,
                                   String outerFormulaName, String outerPatternName) {
        Color borderColor = randomColor();

        // shape to represent selected Color
        Rectangle rectangle = new Rectangle(10, 10);
        rectangle.setFill(borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        NodesManager.getInstance().addColorOnActivityDiagram(rectangle, borderColor);
        if (!NodesManager.getInstance().isShowColorsOnDiagram()) {
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setFill(null);
        }

        // title
        Text type = new Text("        " + patternTypeName + " ");

        // clear button
        Button clearFormulaButton = new Button();
        GlyphsDude.setIcon(clearFormulaButton, FontAwesomeIcon.TIMES_CIRCLE);
        addContextMenuToButton(clearFormulaButton, patternTypeName, outerFormulaName, outerPatternName, options,
                borderColor);
        clearFormulaButton.setDisable(!isInnerPattern);

        // hBox for rectangle, title and clear button
        HBox hBoxTitleAndClearButton = new HBox();
        hBoxTitleAndClearButton.getChildren().addAll(type, rectangle, new Text(" "), clearFormulaButton);
        hBoxTitleAndClearButton.setAlignment(Pos.CENTER);

        // a1
        VBox a1vBox = createFormulaVBox(patternTypeName, "a1", options, borderColor, false);

        // connection visualization
        MyArrow myArrow1 = new MyArrow(0, 0, 0, 70);
        myArrow1.setHeadAVisible(false);
        HBox myArrow_1 = new HBox();
        myArrow_1.setAlignment(Pos.CENTER);
        myArrow_1.getChildren().addAll(myArrow1);

        // a2
        VBox a2vBox = createFormulaVBox(patternTypeName, "a2", options, borderColor, false);

        // connection visualization
        MyArrow myArrow2 = new MyArrow(100, 0, 0, 50);
        MyArrow myArrow3 = new MyArrow(100, 0, 200, 50);
        myArrow3.setHeadAVisible(false);
        HBox myArrow_2 = new HBox();
        myArrow_2.setAlignment(Pos.CENTER);
        Text plus = new Text("+");
        plus.setStyle("-fx-font: 20 arial;");
        Text minus = new Text("-");
        minus.setStyle("-fx-font: 23 arial;");
        myArrow_2.getChildren().addAll(plus, myArrow2, myArrow3, minus);
        myArrow_2.widthProperty().addListener((observable, oldValue, newValue) -> {
            myArrow2.setX1(newValue.doubleValue() / 2);
            myArrow3.setX1(newValue.doubleValue() / 2);
            myArrow2.setX2(newValue.doubleValue() / 4.0 - 35.0);
            myArrow3.setX2(3.0 * newValue.doubleValue() / 4.0 + 35.0);
        });

        // a3
        VBox a3vBox = createFormulaVBox(patternTypeName, "a3", options, borderColor, true);

        // a4
        VBox a4vBox = createFormulaVBox(patternTypeName, "a4", options, borderColor, true);

        // HBox agregujący A3 i A4 były obok siebie
        HBox a3a4hBox = new HBox();
        a3a4hBox.setAlignment(Pos.CENTER);
        a3a4hBox.getChildren().addAll(a3vBox, a4vBox);

        // ustawia podział między A3 i A4 w połowie dostępnego miejsca
        HBox.setHgrow(a3vBox, Priority.ALWAYS);
        HBox.setHgrow(a4vBox, Priority.ALWAYS);
        a3a4hBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            a3vBox.setMaxWidth(a3a4hBox.getWidth() / 2);
            a4vBox.setMaxWidth(a3a4hBox.getWidth() / 2);
        });

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        if (isInnerPattern) {
            addEntryArrow(vBox, outerPatternName, outerFormulaName);
        }
        vBox.getChildren().addAll(hBoxTitleAndClearButton, a1vBox, myArrow_1, a2vBox, myArrow_2, a3a4hBox);
        if (isInnerPattern) {
            if (addOutArrow(new VBox(), outerPatternName, outerFormulaName)) {
                MyArrow outArrow = new MyArrow(100, 0, 0, 50);
                outArrow.setHeadAVisible(false);

                VBox vboxWithLeftOutArrow = new VBox();
                vboxWithLeftOutArrow.setAlignment(Pos.CENTER);

                VBox vboxWithRightOutArrow = new VBox();
                vboxWithRightOutArrow.setAlignment(Pos.CENTER_LEFT);
                vboxWithRightOutArrow.getChildren().add(outArrow);

                HBox hBoxWithOutArrows = new HBox();
                hBoxWithOutArrows.setAlignment(Pos.CENTER);
                hBoxWithOutArrows.getChildren().addAll(vboxWithLeftOutArrow, vboxWithRightOutArrow);

                HBox.setHgrow(vboxWithLeftOutArrow, Priority.ALWAYS);
                HBox.setHgrow(vboxWithRightOutArrow, Priority.ALWAYS);

                hBoxWithOutArrows.widthProperty().addListener((observable, oldValue, newValue) -> {
                    vboxWithLeftOutArrow.setMaxWidth(hBoxWithOutArrows.getWidth() / 2);
                    vboxWithRightOutArrow.setMaxWidth(hBoxWithOutArrows.getWidth() / 2);
                });

                hBoxWithOutArrows.widthProperty().addListener((observable, oldValue, newValue) -> {
                    outArrow.setX1(3.0 * newValue.doubleValue() / 4.0 + 35.0);
                    outArrow.setX2(newValue.doubleValue() / 2);
                });
                vBox.getChildren().addAll(hBoxWithOutArrows);
            }
        }
        addBorderToVBox(vBox, borderColor);
        return vBox;
    }

    private void setDropdownOnAction(ComboBox<String> dropdownComboBox, VBox parentVBox, ObservableList<String> options,
                                     String patternTypeName) {
        dropdownComboBox.setOnAction(e -> {

            // usuń ComboBox jeśli wybrano zagnieżdżenie
            if (parentVBox.getChildren().size() == 2 && !isAtomicActivityChosen(dropdownComboBox.getValue())) {
                parentVBox.getChildren().remove(parentVBox.getChildren().size() - 1);
            }

            if (dropdownComboBox.getValue().equals("Seq")) {
                VBox newSeqBox =
                        createSeqPattern("Seq", options, true, dropdownComboBox.getPromptText(), patternTypeName);
                parentVBox.getChildren().add(newSeqBox);
                parentVBox.getChildren().remove(0);
                addFillingSpaceLine(parentVBox);
            } else if (dropdownComboBox.getValue().equals("Branch")) {
                VBox newBranchBox =
                        createBranchPattern("Branch", options, true, dropdownComboBox.getPromptText(), patternTypeName);
                parentVBox.getChildren().add(newBranchBox);
                parentVBox.getChildren().remove(0);
                addFillingSpaceLine(parentVBox);
            } else if (dropdownComboBox.getValue().equals("BranchRe")) {
                VBox newBranchReBox = createBranchRePattern("BranchRe", options, true, dropdownComboBox.getPromptText(),
                        patternTypeName);
                parentVBox.getChildren().add(newBranchReBox);
                parentVBox.getChildren().remove(0);
                addFillingSpaceLine(parentVBox);
            } else if (dropdownComboBox.getValue().equals("Cond")) {
                VBox newCondBox =
                        createCondPattern("Cond", options, true, dropdownComboBox.getPromptText(), patternTypeName);
                parentVBox.getChildren().add(newCondBox);
                parentVBox.getChildren().remove(0);
                addFillingSpaceLine(parentVBox);
            } else if (dropdownComboBox.getValue().equals("Para")) {
                VBox newParaBox =
                        createParaPattern("Para", options, true, dropdownComboBox.getPromptText(), patternTypeName);
                parentVBox.getChildren().add(newParaBox);
                parentVBox.getChildren().remove(0);
                addFillingSpaceLine(parentVBox);
            } else if (dropdownComboBox.getValue().equals("Concur")) {
                VBox newConcurBox =
                        createConcurPattern("Concur", options, true, dropdownComboBox.getPromptText(), patternTypeName);
                parentVBox.getChildren().add(newConcurBox);
                parentVBox.getChildren().remove(0);
                addFillingSpaceLine(parentVBox);
            } else if (dropdownComboBox.getValue().equals("ConcurRe")) {
                VBox newConcurReBox = createConcurRePattern("ConcurRe", options, true, dropdownComboBox.getPromptText(),
                        patternTypeName);
                parentVBox.getChildren().add(newConcurReBox);
                parentVBox.getChildren().remove(0);
                addFillingSpaceLine(parentVBox);
            } else if (dropdownComboBox.getValue().equals("Loop")) {
                VBox newLoopBox =
                        createLoopPattern("Loop", options, true, dropdownComboBox.getPromptText(), patternTypeName);
                parentVBox.getChildren().add(newLoopBox);
                parentVBox.getChildren().remove(0);
                addFillingSpaceLine(parentVBox);
            }
        });
    }

    private boolean isAtomicActivityChosen(String dropdownValue) {
        List<String> currentAtomicActivities = NodesManager.getInstance().getCurrentAtomicActivities();
        for (String aa : currentAtomicActivities) {
            if (dropdownValue.equals(aa)) {
                return true;
            }
        }
        return false;
    }

    private Color randomColor() {
        Random rand = new Random();
        double r = rand.nextFloat();
        double g = rand.nextFloat();
        double b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }

    /**
     * Lays out the selection halo based on the current width and height of the node skin region.
     */
    private void layoutSelectionHalo() {

        if (selectionHalo.isVisible()) {

            selectionHalo.setWidth(border.getWidth() + 2 * HALO_OFFSET);
            selectionHalo.setHeight(border.getHeight() + 2 * HALO_OFFSET);

            final double cornerLength = 2 * HALO_CORNER_SIZE;
            final double xGap = border.getWidth() - 2 * HALO_CORNER_SIZE + 2 * HALO_OFFSET;
            final double yGap = border.getHeight() - 2 * HALO_CORNER_SIZE + 2 * HALO_OFFSET;

            selectionHalo.setStrokeDashOffset(HALO_CORNER_SIZE);
            selectionHalo.getStrokeDashArray().setAll(cornerLength, yGap, cornerLength, xGap);
        }
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
        if (isSelected) {
            selectionHalo.setVisible(true);
            layoutSelectionHalo();
            background.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, true);
            getRoot().toFront();
        } else {
            selectionHalo.setVisible(false);
            background.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, false);
        }
    }

    /**
     * Removes all connectors from the list of children.
     */
    private void removeAllConnectors() {

        topConnectorSkins.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        rightConnectorSkins.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        bottomConnectorSkins.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        leftConnectorSkins.forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
    }

    /**
     * Gets a minor x-offset of a few pixels in order that the connector's area is distributed more evenly on either
     * side of the node border.
     *
     * @param connector the connector to be positioned
     * @return an x-offset of a few pixels
     */
    private double getMinorOffsetX(final GConnector connector) {

        final String type = connector.getType();

        if (type.equals(DefaultConnectorTypes.LEFT_INPUT) || type.equals(DefaultConnectorTypes.RIGHT_OUTPUT)) {
            return MINOR_POSITIVE_OFFSET;
        } else {
            return MINOR_NEGATIVE_OFFSET;
        }
    }

    /**
     * Gets a minor y-offset of a few pixels in order that the connector's area is distributed more evenly on either
     * side of the node border.
     *
     * @param connector the connector to be positioned
     * @return a y-offset of a few pixels
     */
    private double getMinorOffsetY(final GConnector connector) {

        final String type = connector.getType();

        if (type.equals(DefaultConnectorTypes.TOP_INPUT) || type.equals(DefaultConnectorTypes.BOTTOM_OUTPUT)) {
            return MINOR_POSITIVE_OFFSET;
        } else {
            return MINOR_NEGATIVE_OFFSET;
        }
    }

    /**
     * Stops the node being dragged if it isn't selected.
     *
     * @param event a mouse-dragged event on the node
     */
    private void filterMouseDragged(final MouseEvent event) {
        if (event.isPrimaryButtonDown() && !isSelected()) {
            event.consume();
        }
    }
}
