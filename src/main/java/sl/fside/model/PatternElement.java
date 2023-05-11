package sl.fside.model;

import com.fasterxml.jackson.annotation.*;
import sl.fside.ui.editors.activityDiagramEditor.managers.*;

public class PatternElement {
    private final PatternExpression nestedPatternExpression;
    private final String atomicActivity;
    private final boolean isNestedRelation;
    private final Relation relation;

    @JsonCreator
    public PatternElement(@JsonProperty("atomicActivity") String atomicActivity,
                          @JsonProperty("nestedPatternExpression") PatternExpression nestedPatternExpression,
                          @JsonProperty("isNestedRelation") boolean isNestedRelation,
                          @JsonProperty("relation") Relation relation) {
        this.atomicActivity = atomicActivity;
        this.nestedPatternExpression = nestedPatternExpression;
        this.isNestedRelation = isNestedRelation;
        this.relation = relation;
    }

    public String getPeWithProcessedNesting() {
        if (this.nestedPatternExpression != null) {
            return this.nestedPatternExpression.getPeWithProcessedNesting();
        } else if (this.atomicActivity != null) {
            if (this.isNestedRelation) {
                return replaceRelation();
            } else {
                return this.atomicActivity;
            }
        } else {
            System.err.println("Kontrola! 'patternExpression' oraz 'atomicActivity' są null");
            return null;
        }
    }

    public String primPatternElement(int primIdx) {
        if (this.nestedPatternExpression != null) {
            return this.nestedPatternExpression.primNestedPatternExpression(primIdx);
        } else if (this.atomicActivity != null) {
            if (this.isNestedRelation) {
                return replaceRelation();
            } else {

                // create as many "P" as needed
                String primString = "P".repeat(Math.max(0, primIdx));

                return this.atomicActivity + primString;
            }
        } else {
            System.err.println("Kontrola! 'patternExpression' oraz 'atomicActivity' są null");
            return null;
        }
    }

    @Override
    public String toString() {
        if (this.nestedPatternExpression != null) {
            return this.nestedPatternExpression.toString();
        } else if (this.atomicActivity != null) {
            return this.atomicActivity;
        } else {
            System.err.println("Kontrola! 'patternExpression' oraz 'atomicActivity' są null");
            return null;
        }
    }

    private String replaceRelation() {
        UseCaseDiagram currentUseCaseDiagram = NodesManager.getInstance().getCurrentUseCaseDiagram();

        if (this.relation.getType() == Relation.RelationType.INCLUDE) {

            UseCase targetUseCase = currentUseCaseDiagram.getUseCaseList().stream()
                    .filter(uc -> uc.getId().equals(this.relation.getToId())).findFirst().orElseThrow();
            Scenario mainScenario =
                    targetUseCase.getScenarioList().stream().filter(Scenario::isMainScenario).findFirst().orElseThrow();
            if (mainScenario.getPatternExpression() == null) {
                return this.atomicActivity;
            }
            return mainScenario.getPatternExpression().primNestedPatternExpression(this.relation.getPrimIdx());

        } else if (this.relation.getType() == Relation.RelationType.EXTEND) {

            UseCase targetUseCase = currentUseCaseDiagram.getUseCaseList().stream()
                    .filter(uc -> uc.getId().equals(this.relation.getFromId())).findFirst().orElseThrow();
            Scenario mainScenario =
                    targetUseCase.getScenarioList().stream().filter(Scenario::isMainScenario).findFirst().orElseThrow();
            if (mainScenario.getPatternExpression() == null) {
                return this.atomicActivity;
            }
            String patternExpressionToInject =
                    mainScenario.getPatternExpression().primNestedPatternExpression(this.relation.getPrimIdx());
            String condition = this.atomicActivity.substring(10,
                    this.atomicActivity.length() - targetUseCase.getUseCaseName().length() - 1); // np. "warunek"
            return "Alt(" + condition + ", " + patternExpressionToInject + ", null)";

        } else if (this.relation.getType() == Relation.RelationType.INHERIT) {
            System.err.println("Not implemented!");
        }
        return this.atomicActivity;
    }
}
