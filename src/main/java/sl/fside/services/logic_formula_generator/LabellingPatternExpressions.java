package sl.fside.services.logic_formula_generator;

public class LabellingPatternExpressions {

    // Algorithm 1 - Labelling pattern expressions
    //
    // Input: pattern expression w, ex. Seq(a, Seq(Concur(b, c, d), Concur Re(e, f, g)))
    // Output: function result, that is 'Labelling' as labelled pattern expression w',
    //                          ex. Seq(1]a, Seq(2]Concur(3]b, c,d[3), Concur Re(3]e, f , g[3)[2)[1)
    //
    // Lexical tokens are processed/scanned one by one from left to right
    public static String labelExpressions(String expression) {
        StringBuilder labelledExpression = new StringBuilder();
        int labelNumber = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                labelNumber++;
                labelledExpression.append("(").append(labelNumber).append("]");
            } else if (c == ')') {
                labelledExpression.append("[").append(labelNumber).append(")");
                labelNumber--;
            } else {
                labelledExpression.append(c);
            }
        }
        return labelledExpression.toString();
    }

}
