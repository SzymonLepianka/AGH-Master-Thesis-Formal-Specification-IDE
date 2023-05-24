package tl_to_spass;

// Generated from TL.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TLParser}.
 */
public interface TLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TLParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(TLParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(TLParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormula(TLParser.FormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormula(TLParser.FormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(TLParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(TLParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLParser#bin_connective}.
	 * @param ctx the parse tree
	 */
	void enterBin_connective(TLParser.Bin_connectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#bin_connective}.
	 * @param ctx the parse tree
	 */
	void exitBin_connective(TLParser.Bin_connectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(TLParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(TLParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLParser#pred_constant}.
	 * @param ctx the parse tree
	 */
	void enterPred_constant(TLParser.Pred_constantContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#pred_constant}.
	 * @param ctx the parse tree
	 */
	void exitPred_constant(TLParser.Pred_constantContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLParser#ind_constant}.
	 * @param ctx the parse tree
	 */
	void enterInd_constant(TLParser.Ind_constantContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#ind_constant}.
	 * @param ctx the parse tree
	 */
	void exitInd_constant(TLParser.Ind_constantContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLParser#func_constant}.
	 * @param ctx the parse tree
	 */
	void enterFunc_constant(TLParser.Func_constantContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#func_constant}.
	 * @param ctx the parse tree
	 */
	void exitFunc_constant(TLParser.Func_constantContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLParser#separator}.
	 * @param ctx the parse tree
	 */
	void enterSeparator(TLParser.SeparatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLParser#separator}.
	 * @param ctx the parse tree
	 */
	void exitSeparator(TLParser.SeparatorContext ctx);
}