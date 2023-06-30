package sl.fside.model;

import com.fasterxml.jackson.annotation.*;
import sl.fside.services.logic_formula_generator.*;

import java.util.*;

public class PatternExpression {
    private final List<PatternElement> list;
    private final MainPatternName mainPatternName;

    @JsonCreator
    public PatternExpression(@JsonProperty("mainPatternName") MainPatternName mainPatternName) {
        this.list = new ArrayList<>();
        this.mainPatternName = mainPatternName;
    }

    public List<PatternElement> getList() {
        return this.list;
    }

    public void add(PatternElement patternElement) {
        list.add(patternElement);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mainPatternName.toString()).append("(");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toString());
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public String primNestedPatternExpression(int primIdx) {
        StringBuilder sb = new StringBuilder();
        sb.append(mainPatternName.toString()).append("(");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).primPatternElement(primIdx));
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public String getPeWithProcessedNesting() {
        StringBuilder sb = new StringBuilder();
        sb.append(mainPatternName.toString()).append("(");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getPeWithProcessedNesting());
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public List<String> getAtomicActivitiesFromPE() throws Exception {
        String patternExpression = this.getPeWithProcessedNesting().replace(" ", "");
        List<String> atomicActivities = new ArrayList<>();

        String patternRulesFolFile = "./pattern_rules/pattern_rules_FOL.json"; // First Order Logic
        List<WorkflowPatternTemplate> folPatternPropertySet =
                WorkflowPatternTemplate.loadPatternPropertySet(patternRulesFolFile);

        String labeledPatternExpression = LabellingPatternExpressions.labelExpressions(patternExpression);
        int highestLabel = GeneratingLogicalSpecifications.getHighestLabel(labeledPatternExpression);

        for (int l = highestLabel; l > 0; l--) {
            int c = 1;
            WorkflowPattern pat =
                    GeneratingLogicalSpecifications.getPat(labeledPatternExpression, l, c, folPatternPropertySet);
            while (pat != null) {
                for (String arg : pat.getPatternArguments()) {
                    if (!WorkflowPattern.isNotAtomic(arg)) {
                        atomicActivities.add(arg);
                    }
                }
                c++;
                pat = GeneratingLogicalSpecifications.getPat(labeledPatternExpression, l, c, folPatternPropertySet);
            }
        }

        return new ArrayList<>(new HashSet<>(atomicActivities));
    }

    public enum MainPatternName {
        Seq, Branch, BranchRe, Concur, ConcurRe, Cond, Para, Loop, Alt, SeqSeq
    }
}
