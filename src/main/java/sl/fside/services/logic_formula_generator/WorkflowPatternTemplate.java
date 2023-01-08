package sl.fside.services.logic_formula_generator;

import org.json.*;

import java.io.*;
import java.util.*;

// zawiera dane, na temat danego wzorca, które są zawarte w pliku wsadowym
public class WorkflowPatternTemplate {
    private String name;
    private int numberOfArguments;
    private List<String> rules;

    public WorkflowPatternTemplate(String name, int numberOfArguments, List<String> rules) {
        this.name = name;
        this.numberOfArguments = numberOfArguments;
        this.rules = rules;
    }

    public static List<WorkflowPatternTemplate> loadPatternPropertySet(String pathToPatternRulesFile) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(pathToPatternRulesFile);
        JSONTokener jsonTokener = new JSONTokener(fileInputStream);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        List<WorkflowPatternTemplate> patternPropertySet = new ArrayList<>();
        for (String workflowPatternTemplateName : jsonObject.keySet()) {
            JSONObject patternDescrJSONObject = (JSONObject) jsonObject.get(workflowPatternTemplateName);
            int numberOfArguments = (int) patternDescrJSONObject.get("number of args");
            JSONArray rules = (JSONArray) patternDescrJSONObject.get("rules");
            List<String> rulesList = new ArrayList<>();
            for (var rule : rules.toList()) {
                rulesList.add((String) rule);
            }
            WorkflowPatternTemplate workflowPatternTemplate = new WorkflowPatternTemplate(workflowPatternTemplateName, numberOfArguments, rulesList);
            patternPropertySet.add(workflowPatternTemplate);
        }
        return patternPropertySet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    public void setNumberOfArguments(int numberOfArguments) {
        this.numberOfArguments = numberOfArguments;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }
}
