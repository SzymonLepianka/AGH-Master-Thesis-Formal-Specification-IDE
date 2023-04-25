package sl.fside.ui.editors.activityDiagramEditor.managers;


import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import sl.fside.model.*;
import sl.fside.services.logic_formula_generator.*;

import java.util.*;
import java.util.regex.*;

public class NodesManager {
    private static NodesManager instance;
    private final Map<Pane, Border> bordersOnActivityDiagram = new HashMap<>();
    private final Map<Rectangle, Color> colorsOnActivityDiagram = new HashMap<>();
    private Node main;
    private String patternExpressionBeforeProcessingNesting;
    private String patternExpressionAfterProcessingNesting;
    private String folLogicalSpecification;
    private String ltlLogicalSpecification;
    private boolean wasSpecificationGenerated;
    private UseCaseDiagram currentUseCaseDiagram;
    private UseCase currentUseCase;
    private List<String> currentAtomicActivities = new ArrayList<>();
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

    public String getPatternExpressionBeforeProcessingNesting() {
        return patternExpressionBeforeProcessingNesting;
    }

    public String getPatternExpressionAfterProcessingNesting() {
        return patternExpressionAfterProcessingNesting;
    }

    public void setPatternExpression(String patternExpressionBeforeProcessingNesting) {
        this.patternExpressionBeforeProcessingNesting = patternExpressionBeforeProcessingNesting;

        processNesting();

        String patternRulesFolFile = "./pattern_rules/pattern_rules_FOL.json"; // First Order Logic
        String patternRulesLtlFile = "./pattern_rules/pattern_rules_LTL.json"; // Linear Temporal Logic
        try {
            List<WorkflowPatternTemplate> folPatternPropertySet =
                    WorkflowPatternTemplate.loadPatternPropertySet(patternRulesFolFile);
            List<WorkflowPatternTemplate> ltlPatternPropertySet =
                    WorkflowPatternTemplate.loadPatternPropertySet(patternRulesLtlFile);
            this.folLogicalSpecification = GeneratingLogicalSpecifications.generateLogicalSpecifications(
                    patternExpressionAfterProcessingNesting.replace(" ", ""), folPatternPropertySet);
            this.ltlLogicalSpecification = GeneratingLogicalSpecifications.generateLogicalSpecifications(
                    patternExpressionAfterProcessingNesting.replace(" ", ""), ltlPatternPropertySet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePatternExpression() {
        this.patternExpressionBeforeProcessingNesting = null;
        this.patternExpressionAfterProcessingNesting = null;
    }

    private void processNesting() {

        String expression = patternExpressionBeforeProcessingNesting;

        // include
        List<Relation> includeRelations = currentUseCaseDiagram.getRelations().stream()
                .filter(r -> r.getType() == Relation.RelationType.INCLUDE &&
                        r.getFromId().equals(currentUseCase.getId())).toList();
        for (Relation r : includeRelations) {
            UseCase targetUseCase =
                    currentUseCaseDiagram.getUseCaseList().stream().filter(uc -> uc.getId().equals(r.getToId()))
                            .findFirst().orElseThrow();
            String obligatoryAtomicActivity = "<<include>>" + targetUseCase.getUseCaseName();
            Scenario mainScenario =
                    targetUseCase.getScenarioList().stream().filter(Scenario::isMainScenario).findFirst().orElseThrow();
            String patternExpressionToInject =
                    primPatternExpression(mainScenario.getPatternExpressionAfterProcessingNesting(),
                            includeRelations.indexOf(r));
            if (patternExpressionToInject != null) {
                expression = expression.replace(obligatoryAtomicActivity, patternExpressionToInject);
            }
        }

        // extend
        List<Relation> extendRelations = currentUseCaseDiagram.getRelations().stream()
                .filter(r -> r.getType() == Relation.RelationType.EXTEND && r.getToId().equals(currentUseCase.getId()))
                .toList();
        for (Relation r : extendRelations) {

            // UseCase that is injected into another UseCase
            UseCase targetUseCase =
                    currentUseCaseDiagram.getUseCaseList().stream().filter(uc -> uc.getId().equals(r.getFromId()))
                            .findFirst().orElseThrow();
            String targetUseCaseName = targetUseCase.getUseCaseName();
            String obligatoryRelationName = "<<extend>>";

            // MainScenario contains PatternExpression to inject into another UseCase
            Scenario mainScenario =
                    targetUseCase.getScenarioList().stream().filter(Scenario::isMainScenario).findFirst().orElseThrow();
            String patternExpressionToInject =
                    primPatternExpression(mainScenario.getPatternExpressionAfterProcessingNesting(),
                            extendRelations.indexOf(r));

            if (patternExpressionToInject != null) {
                String regex = obligatoryRelationName + "\\S*?" + targetUseCaseName;
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(expression);
                while (matcher.find()) {
                    String match = matcher.group(); // np. "<<extend>>warunek?ship_order"
                    String condition = match.substring(obligatoryRelationName.length(),
                            match.length() - targetUseCaseName.length() - 1); // np. "warunek"
                    String altPattern = "Alt(" + condition + ", " + patternExpressionToInject + ", null)";
                    expression = expression.replace(match, altPattern);
                }
            } else {
                System.out.println("PatternExpression for " + targetUseCaseName + " is not defined!");
            }
        }

        //TODO INHERIT

        this.patternExpressionAfterProcessingNesting = expression;
    }

    private String primPatternExpression(String patternExpression, int idx) {

        // check if patternExpression (to inject) exists
        if (patternExpression == null || patternExpression.isEmpty()) {
            return null;
        }

        // create as many "P" as needed
        String primString = "P".repeat(Math.max(0, idx + 1));

        StringBuilder primPatternExpression = new StringBuilder();
        for (int i = 0; i < patternExpression.length(); i++) {

            // put primString before ',' and after 'not ('
            if (patternExpression.charAt(i) == ',' && patternExpression.charAt(i - 1) != ')') {
                primPatternExpression.append(primString);
            }

            // put primString before ')' and after ') or ,'
            if (patternExpression.charAt(i) == ')' && patternExpression.charAt(i - 1) != ')' &&
                    patternExpression.charAt(i - 1) != ',') {
                primPatternExpression.append(primString);
            }

            primPatternExpression.append(patternExpression.charAt(i));
        }
        return primPatternExpression.toString();
    }

    public void setSpecificationFromScenario(Scenario scenario) {
        this.patternExpressionBeforeProcessingNesting = scenario.getPatternExpressionBeforeProcessingNesting();
        this.patternExpressionAfterProcessingNesting = scenario.getPatternExpressionAfterProcessingNesting();
        this.folLogicalSpecification = scenario.getFolLogicalSpecification();
        this.ltlLogicalSpecification = scenario.getLtlLogicalSpecification();
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

    public UseCaseDiagram getCurrentUseCaseDiagram() {
        return currentUseCaseDiagram;
    }

    public void setCurrentUseCaseDiagram(UseCaseDiagram currentUseCaseDiagram) {
        this.currentUseCaseDiagram = currentUseCaseDiagram;
    }

    public UseCase getCurrentUseCase() {
        return currentUseCase;
    }

    public void setCurrentUseCase(UseCase currentUseCase) {
        this.currentUseCase = currentUseCase;
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
