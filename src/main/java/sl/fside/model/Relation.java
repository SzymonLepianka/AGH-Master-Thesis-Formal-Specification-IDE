package sl.fside.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class Relation {

    private final UUID relationId;
    private final UUID fromId; // źródłowy UseCase relacji
    private final UUID toId; // docelowy UseCase relacji
    private final RelationType type;
    private int primIdx;

    @JsonCreator
    public Relation(@JsonProperty("relationId") UUID relationId, @JsonProperty("fromId") UUID fromId,
                    @JsonProperty("toId") UUID toId, @JsonProperty("type") RelationType type) {
        this.relationId = relationId;
        this.fromId = fromId;
        this.toId = toId;
        this.type = type;
    }

    public UUID getFromId() {
        return fromId;
    }

    public UUID getToId() {
        return toId;
    }

    public RelationType getType() {
        return type;
    }

    public String getPrettyType() {
        if (type == RelationType.INCLUDE) {
            return "<<include>>";
        } else if (type == RelationType.EXTEND) {
            return "<<extend>>";
        } else if (type == RelationType.GENERALIZATION) {
            return "<<generalization>>";
        } else {
            return "<<!UNKNOWN!>>";
        }
    }

    public int getPrimIdx() {
        return primIdx;
    }

    public void setPrimIdx(int primIdx) {
        this.primIdx = primIdx;
    }

    public enum RelationType {
        EXTEND, INCLUDE, GENERALIZATION
    }
}
