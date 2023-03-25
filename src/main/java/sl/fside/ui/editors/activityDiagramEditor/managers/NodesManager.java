package sl.fside.ui.editors.activityDiagramEditor.managers;


import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import sl.fside.services.logic_formula_generator.*;

import java.util.*;

public class NodesManager {
    private static NodesManager instance;

    private Node main;
    private String patternExpression;
    private String folLogicalSpecification;
    private String ltlLogicalSpecification;
    private boolean wasSpecificationGenerated;

    private List<String> currentAtomicActivities = new ArrayList<>();

    private final Map<Pane, Border> bordersOnActivityDiagram = new HashMap<>();
    private final Map<Rectangle, Color> colorsOnActivityDiagram = new HashMap<>();
    private String mainName;
    private String currentNodeType;
    private boolean showColorsOnDiagram = true;

    public static NodesManager getInstance() {
        var result = instance;
        if (instance != null) {
            return result;
        }
        synchronized (NodesManager.class) {
            if (instance == null) {
                instance = new NodesManager();
            }
            return instance;
        }
    }

    public boolean wasSpecificationGenerated() {
        return wasSpecificationGenerated;
    }

    public void setWasSpecificationGenerated(boolean wasSpecificationGenerated) {
        this.wasSpecificationGenerated = wasSpecificationGenerated;
    }

    public boolean isShowColorsOnDiagram() {
        return showColorsOnDiagram;
    }

    public void setShowColorsOnDiagram(boolean showColorsOnDiagram) {
        this.showColorsOnDiagram = showColorsOnDiagram;
    }

    public Map<Pane, Border> getBordersOnActivityDiagram() {
        return bordersOnActivityDiagram;
    }

    public void addBorderOnActivityDiagram(Pane pane, Border border) {
        this.bordersOnActivityDiagram.put(pane, border);
    }

    public Map<Rectangle, Color> getColorsOnActivityDiagram() {
        return colorsOnActivityDiagram;
    }

    public void addColorOnActivityDiagram(Rectangle rectangle, Color color) {
        this.colorsOnActivityDiagram.put(rectangle, color);
    }

    public String getPatternExpression() {
        return patternExpression;
    }

    public void setPatternExpression(String patternExpression) {
        this.patternExpression = patternExpression;

        String patternRulesFolFile = "./pattern_rules/pattern_rules_FOL.json"; // First Order Logic
        String patternRulesLtlFile = "./pattern_rules/pattern_rules_LTL.json"; // Linear Temporal Logic
        try {
            List<WorkflowPatternTemplate> folPatternPropertySet =
                    WorkflowPatternTemplate.loadPatternPropertySet(patternRulesFolFile);
            List<WorkflowPatternTemplate> ltlPatternPropertySet =
                    WorkflowPatternTemplate.loadPatternPropertySet(patternRulesLtlFile);
            this.folLogicalSpecification =
                    GeneratingLogicalSpecifications.generateLogicalSpecifications(patternExpression.replace(" ", ""),
                            folPatternPropertySet);
            this.ltlLogicalSpecification =
                    GeneratingLogicalSpecifications.generateLogicalSpecifications(patternExpression.replace(" ", ""),
                            ltlPatternPropertySet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFolLogicalSpecification() {
        return folLogicalSpecification;
    }

    public String getLtlLogicalSpecification() {
        return ltlLogicalSpecification;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public Node getMain() {
        return main;
    }

    public void setMain(Node main) {
        this.main = main;
    }

    public String getCurrentNodeType() {
        return currentNodeType;
    }

    public void setCurrentNodeType(String nodeType) {
        currentNodeType = nodeType;
    }

    public List<String> getCurrentAtomicActivities() {
        return currentAtomicActivities;
    }

    public void setCurrentAtomicActivities(List<String> currentAtomicActivities) {
        this.currentAtomicActivities = currentAtomicActivities;
    }

    //    public void addTank(Tank tank) {
//        synchronized (allTanks) {
//            allTanks.add(tank);
//        }
//    }
//
//    public void deleteTank(Tank tank) {
//        synchronized (allTanks) {
//            allTanks.remove(tank);
//        }
//    }
//
//    public List<Tank> getTanks() {
//        synchronized (allTanks) {
//            return allTanks;
//        }
//    }
}
