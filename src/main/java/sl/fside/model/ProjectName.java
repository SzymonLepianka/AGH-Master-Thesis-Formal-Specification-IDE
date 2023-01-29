//package sl.fside.model;
//
//import com.fasterxml.jackson.annotation.*;
//
//import java.util.*;
//
//public class ProjectName extends ModelBase {
//
//    private final UUID projectId;
//
//    private String projectName;
//
//    @JsonCreator
//    public ProjectName(@JsonProperty("id") UUID id, @JsonProperty("projectId") UUID projectId, @JsonProperty("projectName") String projectName) {
//        super(id);
//        this.projectId = projectId;
//        this.projectName = projectName;
//    }
//
//    public UUID getProjectId() {
//        return projectId;
//    }
//
//    public String getProjectName() {
//        return projectName;
//    }
//
//    public void setProjectName(String projectName) {
//        if (!this.projectName.equals(projectName)) {
//            this.projectName = projectName;
//            propertyChanged("projectName");
//        }
//
//    }
//}
