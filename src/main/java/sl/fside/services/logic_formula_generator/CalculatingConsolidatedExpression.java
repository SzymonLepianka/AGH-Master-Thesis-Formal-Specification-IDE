package sl.fside.services.logic_formula_generator;

import java.util.*;

public class CalculatingConsolidatedExpression {

    // Algorithm 2 - Calculating consolidated expression
    //
    // Input:   - pattern expression w, ex. Seq(a, Seq(Concur(b, c, d), Concur Re(e, f, g)))
    //          - expression type t, either t='ini' or t='fin'
    //          - predefined pattern property set Σ (non-empty)
    // Output: function result, that is ConsEx as a consolidated expression
    //                          ex. (ini) "a"
    //                          ex. (fin) "g"
    //
    public static String generateConsolidatedExpression(String patternExpression, String type, List<WorkflowPatternTemplate> patternPropertySet) throws Exception {

        // type może równać się tylko "ini" lub "fin"
        if (!type.equals("ini") && !type.equals("fin")) throw new Exception("type must equal 'ini' or 'fin'!");

        String ex = "";
        WorkflowPattern workflowPattern = WorkflowPattern.getWorkflowPatternFromExpression(patternExpression, patternPropertySet);
        List<String> rulesWithAtomicActivities = workflowPattern.getWorkflowPatternFilledRules();
        String ini = rulesWithAtomicActivities.get(0);
        String fin = rulesWithAtomicActivities.get(1);
        rulesWithAtomicActivities.remove(0);
        rulesWithAtomicActivities.remove(0);

        // TODO (argType) nie wszystkie argumenty danego wzorca muszą być 'ini' lub 'fin'

        if (type.equals("ini")) {
            ex = ini;
        } else {
            ex = fin;
        }

        List<String> expressionArguments = WorkflowPattern.extractArgumentsFromLabelledExpression(patternExpression, patternPropertySet);
        for (String argument : expressionArguments) {
            if (WorkflowPattern.isNotAtomic(argument)) {
                String innerConsolidatedExpression = generateConsolidatedExpression(argument, type, patternPropertySet);
                ex = ex.replace(argument, innerConsolidatedExpression);
            }
        }
        return ex;
    }
}
