package sl.fside.services.logic_formula_generator;

import java.util.*;

// zawiera dane, na temat danego wzorca, zawartego w wyrażeniu wzorcowym
public class WorkflowPattern {
    private WorkflowPatternTemplate workflowPatternTemplate;
    private List<String> patternArguments;

    public WorkflowPattern(WorkflowPatternTemplate workflowPatternTemplate, List<String> patternArguments) {
        this.workflowPatternTemplate = workflowPatternTemplate;
        this.patternArguments = patternArguments;
    }

    public static WorkflowPattern getWorkflowPatternFromExpression(String patternExpression, List<WorkflowPatternTemplate> patternPropertySet) throws Exception {

        // pobiera nazwę głównego wzorca z wyrażenia; np. z "Seq(a,b)" wyciąga "Seq"
        String workflowName = patternExpression.substring(0, patternExpression.indexOf("("));

        // tworzy nowy obiekt WorkflowPattern (na podstawie WorkflowPatternTemplate i argumentów w patternExpression)
        WorkflowPatternTemplate workflowPatternTemplate = patternPropertySet.stream().filter(x -> x.getName().equals(workflowName)).findFirst().orElseThrow();
        List<String> patternArguments = extractArgumentsFromLabelledExpression(patternExpression, patternPropertySet);
        return new WorkflowPattern(workflowPatternTemplate, patternArguments);
    }

    //
    public static List<String> extractArgumentsFromLabelledExpression(String labelledExpression, List<WorkflowPatternTemplate> patternPropertySet) throws Exception {

        // pobiera liczbę argumentów odpowiednią dla danego wzorca
        String workflowName = labelledExpression.substring(0, labelledExpression.indexOf("("));
        WorkflowPatternTemplate workflowPatternTemplate = patternPropertySet.stream().filter(x -> x.getName().equals(workflowName)).findFirst().orElseThrow();
        int numberOfArguments = workflowPatternTemplate.getNumberOfArguments();

        // pobiera numer etykiety wzorca w przekazanym wyrażeniu
        int patternLabelNumber = Integer.parseInt(String.valueOf(labelledExpression.charAt(labelledExpression.length() - 2)));

        // skraca wyrażenia wg zasady: "Seq(2]Concur(3]b,c,d[3),ConcurRe(3]e,f,g[3)[2)" --> "Concur(3]b,c,d[3),ConcurRe(3]e,f,g[3)"
        String trimmedLabelledExpression = labelledExpression.substring(labelledExpression.indexOf("]") + 1, labelledExpression.length() - 3);

        // znajduje argumenty danego wzorca
        String[] split = trimmedLabelledExpression.split(",");
        List<String> arguments = new ArrayList<>();
        int bracketsCounter = 0;
        StringBuilder tempArg = new StringBuilder();
        for (String s : split) {
            bracketsCounter += countOccurrenceOfChar(s, '(');
            bracketsCounter -= countOccurrenceOfChar(s, ')');
            tempArg.append(s);
            tempArg.append(",");
            if (bracketsCounter == 0) {
                tempArg.deleteCharAt(tempArg.length() - 1);
                arguments.add(tempArg.toString());
                tempArg = new StringBuilder();
            }
        }

        // sprawdza czy znaleziono odpowiednią liczbę argumentów
        if (arguments.size() != numberOfArguments)
            throw new Exception("Znaleziono liczbę argumentów (" + arguments + ")różną od wymaganej(" + numberOfArguments + ")");

        return arguments;

        // w poprzedniej implementacji wyszukiwanie argumentów odbywało się za pomocą:
        // return extractArgumentsFromFunction(labelledExpression.substring(labelledExpression.indexOf("]") + 1, labelledExpression.length() - 3));
    }

    private static int countOccurrenceOfChar(String string, char c) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static boolean isNotAtomic(String argument) {
        return argument.contains("=>") || argument.contains("|") || argument.contains("^") || argument.contains("]");
    }

    public WorkflowPatternTemplate getWorkflowPatternTemplate() {
        return workflowPatternTemplate;
    }

    public void setWorkflowPatternTemplate(WorkflowPatternTemplate workflowPatternTemplate) {
        this.workflowPatternTemplate = workflowPatternTemplate;
    }

    // zwraca zasady z pliku wsadowego, ale z podmienionymi atomicznymi aktywnościami na zawarte w wyrażeniu wzorcowym
    //
    // np.:
    //  w pliku wsadowym:
    //      ["arg0", "arg1 | arg2", "Exist(arg0)", "ForAll(arg0 => Exist(arg1) ^ Exist(arg2))", "ForAll(~(arg0 ^ arg1))", "ForAll(~(arg0 ^ arg2))"]
    //
    // funkcja zwraca:
    //      [b, c | d, Exist(b), ForAll(b => Exist(c) ^ Exist(d)), ForAll(~(b ^ c)), ForAll(~(b ^ d))]
    //
    public List<String> getWorkflowPatternFilledRules() throws Exception {
        if (patternArguments.size() > 0) {
            List<String> outcomes = new ArrayList<>();
            for (String outcome : workflowPatternTemplate.getRules()) {
                String outcomeWithParams = outcome;
                for (int i = 0; i < patternArguments.size(); i++) {
                    outcomeWithParams = outcomeWithParams.replace("arg" + i, patternArguments.get(i));
                }
                outcomes.add(outcomeWithParams);
            }
            return outcomes;
        } else {
            throw new Exception("Nie ma argumentów dla danego wzoca w wyrażeniu");
        }
    }

    // zwraca argumenty danego wzorca, np.:
    //      dla Seq(1]a, Seq(2]Concur(3]b,c,d[3),ConcurRe(3]e,f,g[3)[2)[1) zwraca [a, Seq(2]Concur(3]b,c,d[3),ConcurRe(3]e,f,g[3)[2)]
    public List<String> getPatternArguments() {
        return patternArguments;
    }

    public void setPatternArguments(List<String> patternArguments) {
        this.patternArguments = patternArguments;
    }
}
