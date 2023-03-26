package sl.fside.services.logic_formula_generator;

import org.apache.commons.lang3.*;

import java.util.*;

public class GeneratingLogicalSpecifications {

    // Algorithm 3 - Generating logical specifications
    //
    // Input:   - pattern expression w, ex. Seq(a, Seq(Concur(b, c, d), Concur Re(e, f, g)))
    //          - predefined pattern property set Σ (non-empty)
    // Output: logical specification L
    //
    public static String generateLogicalSpecifications(String patternExpression, List<WorkflowPatternTemplate> patternPropertySet) throws Exception {
        List<String> logicalSpecification = new ArrayList<>();
        String labelledExpression = LabellingPatternExpressions.labelExpressions(patternExpression);
        int highestLabelNumber = getHighestLabel(labelledExpression);
        for (int l = highestLabelNumber; l > 0; l--) {
            int c = 1;
            WorkflowPattern pat = getPat(labelledExpression, l, c, patternPropertySet);
            while (pat != null) {
                List<String> L2 = pat.getWorkflowPatternFilledRules();
                L2.remove(0);
                L2.remove(0);
                for (String arg : pat.getPatternArguments()) {
                    if (WorkflowPattern.isNotAtomic(arg)) {
                        String cons = CalculatingConsolidatedExpression.generateConsolidatedExpression(arg, "ini", patternPropertySet) + " | " + CalculatingConsolidatedExpression.generateConsolidatedExpression(arg, "fin", patternPropertySet);

                        List<String> L2_cons = new LinkedList<>();
                        for (String outcome : L2) {
                            L2_cons.add(outcome.replace(arg, cons));
                        }
                        L2 = L2_cons;
                    }
                }
                c++;
                logicalSpecification.addAll(L2);
                pat = getPat(labelledExpression, l, c, patternPropertySet);
            }
        }

        Set<String> set = new HashSet<>(logicalSpecification);
        logicalSpecification.clear();
        logicalSpecification.addAll(set);
        String connectedString = "";
        System.out.println("\nWynik: ");
        for (String lValue : logicalSpecification) {
            connectedString = connectedString + lValue + ", ";
            System.out.println(lValue);
        }
        return connectedString;

    }

    // input: np. "Seq(1]a, Seq(2]Concur(3]b,c,d[3), ConcurRe(3]e,f,g[3)[2)[1)"
    //
    // Wykonywana jest pętla po każdym znaku wyrażenia. Jeśli znak to '(' rozpoczyna się sczytywanie następnych znaków,
    // aż do natrafienia na znak ']'.
    // Zwracana jest największa wartość między '(' oraz ']', np. "(124]"
    public static int getHighestLabel(String labelledExpression) {
        int maxLabel = -1;
        boolean active = false;
        StringBuilder sb = new StringBuilder();
        for (char c : labelledExpression.toCharArray()) {
            if (c == '(') {
                active = true;
            } else if (c == ']') {
                if (Integer.parseInt(sb.toString()) > maxLabel) {
                    maxLabel = Integer.parseInt(sb.toString());
                }
                sb = new StringBuilder();
                active = false;
            } else if (active) {
                sb.append(c);
            }
        }
        return maxLabel;
    }

    // Funkcja getPat() zwraca wzór zawarty w przekazanym wyrażeniu, z etykietą numer 'l' na c-tej pozycji od lewej,
    // a także usuwa wprowadzone etykiety.
    //
    // Na przykład dla Formuły "Seq(1]a, Seq(2]Concur(3]b,c,d[3), ConcurRe(3]e,f,g[3)[2)[1)" funkcja zwróci:
    //      Concur Re(e,f,g).
    public static WorkflowPattern getPat(String labelledExpression, int l, int c, List<WorkflowPatternTemplate> patternPropertySet) throws Exception {

        // sprawdzenie poprawności wyrażenia
        int entryOccurrences = StringUtils.countMatches(labelledExpression, "(" + l + "]");
        int endOccurrences = StringUtils.countMatches(labelledExpression, "[" + l + ")");
        if (entryOccurrences != endOccurrences) throw new Exception("(" + l + "] nie równa się [" + l + ")");

        // jeżeli wprowadzono liczbę c większą niż wystąpienia danej etykiety w wyrażeniu to funkcja zwraca null
        if (entryOccurrences < c) return null;

        // dzielenie wyrażenia wg "(l]", np.:
        //      "Seq(1]a, Seq(2]Concur(3]b,c,d[3), ConcurRe(3]e,f,g[3)[2)[1)" ---> "[Seq(1]a,Seq(2]Concur, b,c,d[3),ConcurRe, e,f,g[3)[2)[1)]"
        String[] expressionSplitByEntry = labelledExpression.split("\\(" + l + "]");

        // wyciągnięcie zawartości pożądanego wzorca, tj.:
        //      jeśli wzorzec to: "ConcurRe(e,f,g)" to tu uzyskiwane jest "e,f,g"
        String patternContent = expressionSplitByEntry[c].split("\\[" + l + "\\)")[0];

        // wyciągnięcie nazwy wzorca, tj.:
        //      jeśli wzorzec to: "ConcurRe(e,f,g)" to tu uzyskiwane jest "ConcurRe"
        // Nazwa wzorca może znajdować się przez znakiem ']' lub ','
        String[] splitByBracket = expressionSplitByEntry[c - 1].split("]");
        String[] splitByComma = splitByBracket[splitByBracket.length - 1].split(",");
        String workflowName = splitByComma[splitByComma.length - 1];

        // sklejenie całego wzorca w jeden string, np.: "ConcurRe(3]e,f,g[3)"
        String workflowExp = workflowName + "(" + l + "]" + patternContent + "[" + l + ")";

        // zwraca obiekt wzorca
        return WorkflowPattern.getWorkflowPatternFromExpression(workflowExp, patternPropertySet);
    }
}
