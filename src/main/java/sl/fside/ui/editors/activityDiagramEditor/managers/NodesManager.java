package sl.fside.ui.editors.activityDiagramEditor.managers;


import javafx.scene.*;
import sl.fside.services.logic_formula_generator.*;

import java.io.*;
import java.util.*;

public class NodesManager {
    private static NodesManager instance;
//    private final List<Tank> allTanks = new ArrayList<>();

    private Node main;
    private String patternExpression;
    private String folLogicalSpecification;
    private String ltlLogicalSpecification;

    public String getPatternExpression() {
        return patternExpression;
    }

    public String getFolLogicalSpecification() {
        return folLogicalSpecification;
    }

    public String getLtlLogicalSpecification() {
        return ltlLogicalSpecification;
    }

    public void setPatternExpression(String patternExpression) {
        this.patternExpression = patternExpression;

        String patternRulesFolFile = "./pattern_rules/pattern_rules_FOL.json"; // First Order Logic
        String patternRulesLtlFile = "./pattern_rules/pattern_rules_LTL.json"; // Linear Temporal Logic
        try {
            List<WorkflowPatternTemplate> folPatternPropertySet = WorkflowPatternTemplate.loadPatternPropertySet(patternRulesFolFile);
            List<WorkflowPatternTemplate> ltlPatternPropertySet = WorkflowPatternTemplate.loadPatternPropertySet(patternRulesLtlFile);
            this.folLogicalSpecification = GeneratingLogicalSpecifications.generateLogicalSpecifications(patternExpression.replace(" ", ""), folPatternPropertySet);
            this.ltlLogicalSpecification = GeneratingLogicalSpecifications.generateLogicalSpecifications(patternExpression.replace(" ", ""), ltlPatternPropertySet);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    private String mainName;

    public Node getMain() {
        return main;
    }

    public void setMain(Node main) {
        this.main = main;
    }

    private String currentNodeType;

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

    public String getCurrentNodeType() {
        return currentNodeType;
    }

    public void setCurrentNodeType(String nodeType) {
        currentNodeType = nodeType;
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
