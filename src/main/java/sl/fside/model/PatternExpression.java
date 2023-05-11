package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

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

    public enum MainPatternName {
        Seq, Branch, BranchRe, Concur, ConcurRe, Cond, Para, Loop, Alt
    }
}
