package sl.fside.ui.editors.activityDiagramEditor.ownImpl;

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
                System.out.println("Connector type '{}' not recognized, setting to 'left-input'." + connector.getType());
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
    private void layoutConnectors(final List<GConnectorSkin> connectorSkins, final boolean vertical, final double offset) {

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

        List<String> atomicActivities = new ArrayList<>(Arrays.asList("atomic_activity_1", "atomic_activity_2", "atomic_activity_3"));
        List<String> patternNames = new ArrayList<>(Arrays.asList("SEQ", "BRANCH", "BRANCHRE", "CONCUR", "CONCURRE", "COND", "PARA", "LOOP"));

        String currentNodeType = NodesManager.getInstance().getCurrentNodeType();
        ObservableList<String> options = FXCollections.observableArrayList(atomicActivities);
        options.addAll(patternNames);
        switch (currentNodeType) {
            case "SEQ" -> {
                VBox seqVBox = createSeqPattern(currentNodeType, options);
                getRoot().getChildren().add(seqVBox);
            }
            case "BRANCH" -> {
                VBox branchVBox = createBranchPattern(currentNodeType, options);
                getRoot().getChildren().add(branchVBox);
            }
            case "BRANCHRE" -> {
                VBox branchReVBox = createBranchRePattern(currentNodeType, options);
                getRoot().getChildren().add(branchReVBox);
            }
            case "COND" -> {
                VBox condVBox = createCondPattern(currentNodeType, options);
                getRoot().getChildren().add(condVBox);
            }
            case "CONCUR", "CONCURRE" -> {
                Text type = new Text(currentNodeType);
                ComboBox<String> A1 = new ComboBox<>(options);
                A1.setPromptText("a1");
                ComboBox<String> A2 = new ComboBox<>(options);
                A2.setPromptText("a2");
                ComboBox<String> A3 = new ComboBox<>(options);
                A3.setPromptText("a3");

                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(type, A1, A2, A3);
                getRoot().getChildren().add(vBox);
            }
            case "PARA", "LOOP" -> {
                Text type = new Text(currentNodeType);
                ComboBox<String> A1 = new ComboBox<>(options);
                A1.setPromptText("a1");
                ComboBox<String> A2 = new ComboBox<>(options);
                A2.setPromptText("a2");
                ComboBox<String> A3 = new ComboBox<>(options);
                A3.setPromptText("a3");
                ComboBox<String> A4 = new ComboBox<>(options);
                A4.setPromptText("a4");

                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(type, A1, A2, A3, A4);
                getRoot().getChildren().add(vBox);
            }
        }

        selectionHalo.setManaged(false);
        selectionHalo.setMouseTransparent(false);
        selectionHalo.setVisible(false);

        selectionHalo.setLayoutX(-HALO_OFFSET);
        selectionHalo.setLayoutY(-HALO_OFFSET);

        selectionHalo.getStyleClass().add(STYLE_CLASS_SELECTION_HALO);
    }

    private VBox createSeqPattern(String patternTypeName, ObservableList<String> options) {
        // title
        Text type = new Text(patternTypeName);

        // a1
        ComboBox<String> a1Dropdown = new ComboBox<>(options);
        a1Dropdown.setPromptText("a1");
        VBox a1vBox = new VBox();
        a1vBox.setAlignment(Pos.CENTER);
        a1vBox.getChildren().addAll(type, a1Dropdown);
//        a1vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a1Dropdown, a1vBox, options);

        System.out.println(a1vBox.getLayoutX());

        // connection visualization
        Text line1 = new Text("|");
        Text line2 = new Text("|");
        Text line3 = new Text("\\/");

        // a2
        ComboBox<String> a2Dropdown = new ComboBox<>(options);
        a2Dropdown.setPromptText("a2");
        VBox a2vBox = new VBox();
        a2vBox.setAlignment(Pos.CENTER);
        a2vBox.getChildren().addAll(type, a2Dropdown);
//        a2vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a2Dropdown, a2vBox, options);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(type, a1vBox, line1, line2, line3, a2vBox);
        vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        return vBox;
    }

    private VBox createBranchPattern(String patternTypeName, ObservableList<String> options) {
        // title
        Text type = new Text(patternTypeName);

        // a1
        ComboBox<String> a1Dropdown = new ComboBox<>(options);
        a1Dropdown.setPromptText("a1");
        VBox a1vBox = new VBox();
        a1vBox.setAlignment(Pos.CENTER);
        a1vBox.getChildren().addAll(type, a1Dropdown);
//        a1vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a1Dropdown, a1vBox, options);

        // connection visualization
        Text line1 = new Text("|");
        Text line2 = new Text("-----------------");
        Text line3 = new Text("+|                 |-");
        Text line4 = new Text("\\/                 \\/");

        // a2
        ComboBox<String> a2Dropdown = new ComboBox<>(options);
        a2Dropdown.setPromptText("a2");
        VBox a2vBox = new VBox();
        a2vBox.setAlignment(Pos.CENTER);
        a2vBox.getChildren().addAll(type, a2Dropdown);
//        a2vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a2Dropdown, a2vBox, options);
        // a3
        ComboBox<String> a3Dropdown = new ComboBox<>(options);
        a3Dropdown.setPromptText("a3");
        VBox a3vBox = new VBox();
        a3vBox.setAlignment(Pos.CENTER);
        a3vBox.getChildren().addAll(type, a3Dropdown);
//        a3vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a3Dropdown, a3vBox, options);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox a2a3hBox = new HBox();
        a2a3hBox.setAlignment(Pos.CENTER);
        a2a3hBox.getChildren().addAll(a2vBox, a3vBox);
        vBox.getChildren().addAll(type, a1vBox, line1, line2, line3, line4, a2a3hBox);
        vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        return vBox;
    }


    private VBox createBranchRePattern(String patternTypeName, ObservableList<String> options) {
        // title
        Text type = new Text(patternTypeName);

        // a1
        ComboBox<String> a1Dropdown = new ComboBox<>(options);
        a1Dropdown.setPromptText("a1");
        VBox a1vBox = new VBox();
        a1vBox.setAlignment(Pos.CENTER);
        a1vBox.getChildren().addAll(type, a1Dropdown);
//        a1vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a1Dropdown, a1vBox, options);
        // a2
        ComboBox<String> a2Dropdown = new ComboBox<>(options);
        a2Dropdown.setPromptText("a2");
        VBox a2vBox = new VBox();
        a2vBox.setAlignment(Pos.CENTER);
        a2vBox.getChildren().addAll(type, a2Dropdown);
//        a2vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a2Dropdown, a2vBox, options);

        // connection visualization
        Text line1 = new Text("+|                 |-");
        Text line2 = new Text("|                 |");
        Text line3 = new Text("-----------------");
        Text line4 = new Text("         |          ");
        Text line5 = new Text("        \\/         ");

        // a3
        ComboBox<String> a3Dropdown = new ComboBox<>(options);
        a3Dropdown.setPromptText("a3");
        VBox a3vBox = new VBox();
        a3vBox.setAlignment(Pos.CENTER);
        a3vBox.getChildren().addAll(type, a3Dropdown);
//        a3vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a3Dropdown, a3vBox, options);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox a1a2hBox = new HBox();
        a1a2hBox.setAlignment(Pos.CENTER);
        a1a2hBox.getChildren().addAll(a1vBox, a2vBox);
        vBox.getChildren().addAll(type, a1a2hBox, line1, line2, line3, line4, line5, a3vBox);
        vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        return vBox;
    }

    private VBox createCondPattern(String patternTypeName, ObservableList<String> options) {
        // title
        Text type = new Text(patternTypeName);

        // a1
        ComboBox<String> a1Dropdown = new ComboBox<>(options);
        a1Dropdown.setPromptText("a1");
        VBox a1vBox = new VBox();
        a1vBox.setAlignment(Pos.CENTER);
        a1vBox.getChildren().addAll(type, a1Dropdown);
//        a1vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a1Dropdown, a1vBox, options);

        // connection visualization
        Text line1 = new Text("|");
        Text line2 = new Text("-----------------");
        Text line3 = new Text("+|                 |-");
        Text line4 = new Text("\\/                 \\/");

        // a2
        ComboBox<String> a2Dropdown = new ComboBox<>(options);
        a2Dropdown.setPromptText("a2");
        VBox a2vBox = new VBox();
        a2vBox.setAlignment(Pos.CENTER);
        a2vBox.getChildren().addAll(type, a2Dropdown);
//        a2vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a2Dropdown, a2vBox, options);
        // a3
        ComboBox<String> a3Dropdown = new ComboBox<>(options);
        a3Dropdown.setPromptText("a3");
        VBox a3vBox = new VBox();
        a3vBox.setAlignment(Pos.CENTER);
        a3vBox.getChildren().addAll(type, a3Dropdown);
//        a3vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a3Dropdown, a3vBox, options);

        // connection visualization
        Text line5 = new Text("+|                 |-");
        Text line6 = new Text("|                 |");
        Text line7 = new Text("-----------------");
        Text line8 = new Text("         |          ");
        Text line9 = new Text("        \\/         ");

        // a4
        ComboBox<String> a4Dropdown = new ComboBox<>(options);
        a4Dropdown.setPromptText("a4");
        VBox a4vBox = new VBox();
        a4vBox.setAlignment(Pos.CENTER);
        a4vBox.getChildren().addAll(type, a4Dropdown);
//        a4vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a4Dropdown, a4vBox, options);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox a2a3hBox = new HBox();
        a2a3hBox.setAlignment(Pos.CENTER);
        a2a3hBox.getChildren().addAll(a2vBox, a3vBox);
        vBox.getChildren().addAll(type, a1vBox, line1, line2, line3, line4, a2a3hBox, line5, line6, line7, line8, line9, a4vBox);
        vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        return vBox;
    }

    private VBox createParaPattern(String patternTypeName, ObservableList<String> options) {
        // title
        Text type = new Text(patternTypeName);

        // a1
        ComboBox<String> a1Dropdown = new ComboBox<>(options);
        a1Dropdown.setPromptText("a1");
        VBox a1vBox = new VBox();
        a1vBox.setAlignment(Pos.CENTER);
        a1vBox.getChildren().addAll(type, a1Dropdown);
//        a1vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a1Dropdown, a1vBox, options);

        // connection visualization
        Text line1 = new Text("|");
        Text line2 = new Text("-----------------");
        Text line3 = new Text("+|                 |-");
        Text line4 = new Text("\\/                 \\/");

        // a2
        ComboBox<String> a2Dropdown = new ComboBox<>(options);
        a2Dropdown.setPromptText("a2");
        VBox a2vBox = new VBox();
        a2vBox.setAlignment(Pos.CENTER);
        a2vBox.getChildren().addAll(type, a2Dropdown);
//        a2vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a2Dropdown, a2vBox, options);
        // a3
        ComboBox<String> a3Dropdown = new ComboBox<>(options);
        a3Dropdown.setPromptText("a3");
        VBox a3vBox = new VBox();
        a3vBox.setAlignment(Pos.CENTER);
        a3vBox.getChildren().addAll(type, a3Dropdown);
//        a3vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a3Dropdown, a3vBox, options);

        // connection visualization
        Text line5 = new Text("+|                 |-");
        Text line6 = new Text("|                 |");
        Text line7 = new Text("-----------------");
        Text line8 = new Text("         |          ");
        Text line9 = new Text("        \\/         ");

        // a4
        ComboBox<String> a4Dropdown = new ComboBox<>(options);
        a4Dropdown.setPromptText("a4");
        VBox a4vBox = new VBox();
        a4vBox.setAlignment(Pos.CENTER);
        a4vBox.getChildren().addAll(type, a4Dropdown);
//        a4vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        setDropdownOnAction(a4Dropdown, a4vBox, options);

        // main vBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox a2a3hBox = new HBox();
        a2a3hBox.setAlignment(Pos.CENTER);
        a2a3hBox.getChildren().addAll(a2vBox, a3vBox);
        vBox.getChildren().addAll(type, a1vBox, line1, line2, line3, line4, a2a3hBox, line5, line6, line7, line8, line9, a4vBox);
        vBox.setBorder(new Border(new BorderStroke(randomColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        return vBox;
    }

    private void setDropdownOnAction(ComboBox<String> dropdownComboBox, VBox parentVBox, ObservableList<String> options) {
        dropdownComboBox.setOnAction(e -> {

            // usuń zagnieżdżenie jeśli istnieje
            if (parentVBox.getChildren().size() == 2 && (dropdownComboBox.getValue().equals("atomic_activity_1") || dropdownComboBox.getValue().equals("atomic_activity_2") || dropdownComboBox.getValue().equals("atomic_activity_3"))) {
                parentVBox.getChildren().remove(parentVBox.getChildren().size() - 1);
            } else if (parentVBox.getChildren().size() == 2) {
                parentVBox.getChildren().remove(parentVBox.getChildren().size() - 1);
            }

            if (dropdownComboBox.getValue().equals("SEQ")) {
                VBox newSeqBox = createSeqPattern("", options);
                parentVBox.getChildren().add(newSeqBox);
            } else if (dropdownComboBox.getValue().equals("BRANCH")) {
                VBox newBranchBox = createBranchPattern("", options);
                parentVBox.getChildren().add(newBranchBox);
            } else if (dropdownComboBox.getValue().equals("BRANCHRE")) {
                VBox newBranchReBox = createBranchRePattern("", options);
                parentVBox.getChildren().add(newBranchReBox);
            } else if (dropdownComboBox.getValue().equals("COND")) {
                VBox newCondBox = createCondPattern("", options);
                parentVBox.getChildren().add(newCondBox);
            } else if (dropdownComboBox.getValue().equals("PARA")) {
                VBox newParaBox = createParaPattern("", options);
                parentVBox.getChildren().add(newParaBox);
            }
        });
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

        topConnectorSkins.stream().forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        rightConnectorSkins.stream().forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        bottomConnectorSkins.stream().forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
        leftConnectorSkins.stream().forEach(skin -> getRoot().getChildren().remove(skin.getRoot()));
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
