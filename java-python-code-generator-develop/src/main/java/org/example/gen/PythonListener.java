// Generated from C:/Users/kacpe/Desktop/java-python-code-generator-release3/src/main/java/org/example/grammar\Python.g4 by ANTLR 4.12.0
package org.example.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PythonParser}.
 */
public interface PythonListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PythonParser#prule}.
	 * @param ctx the parse tree
	 */
	void enterPrule(PythonParser.PruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#prule}.
	 * @param ctx the parse tree
	 */
	void exitPrule(PythonParser.PruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#creators}.
	 * @param ctx the parse tree
	 */
	void enterCreators(PythonParser.CreatorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#creators}.
	 * @param ctx the parse tree
	 */
	void exitCreators(PythonParser.CreatorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#seq}.
	 * @param ctx the parse tree
	 */
	void enterSeq(PythonParser.SeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#seq}.
	 * @param ctx the parse tree
	 */
	void exitSeq(PythonParser.SeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#seqoptions}.
	 * @param ctx the parse tree
	 */
	void enterSeqoptions(PythonParser.SeqoptionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#seqoptions}.
	 * @param ctx the parse tree
	 */
	void exitSeqoptions(PythonParser.SeqoptionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#seqPrime}.
	 * @param ctx the parse tree
	 */
	void enterSeqPrime(PythonParser.SeqPrimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#seqPrime}.
	 * @param ctx the parse tree
	 */
	void exitSeqPrime(PythonParser.SeqPrimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#seqBranch}.
	 * @param ctx the parse tree
	 */
	void enterSeqBranch(PythonParser.SeqBranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#seqBranch}.
	 * @param ctx the parse tree
	 */
	void exitSeqBranch(PythonParser.SeqBranchContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#seqConcur}.
	 * @param ctx the parse tree
	 */
	void enterSeqConcur(PythonParser.SeqConcurContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#seqConcur}.
	 * @param ctx the parse tree
	 */
	void exitSeqConcur(PythonParser.SeqConcurContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#branch}.
	 * @param ctx the parse tree
	 */
	void enterBranch(PythonParser.BranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#branch}.
	 * @param ctx the parse tree
	 */
	void exitBranch(PythonParser.BranchContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#branchRe}.
	 * @param ctx the parse tree
	 */
	void enterBranchRe(PythonParser.BranchReContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#branchRe}.
	 * @param ctx the parse tree
	 */
	void exitBranchRe(PythonParser.BranchReContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#alt}.
	 * @param ctx the parse tree
	 */
	void enterAlt(PythonParser.AltContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#alt}.
	 * @param ctx the parse tree
	 */
	void exitAlt(PythonParser.AltContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#concur}.
	 * @param ctx the parse tree
	 */
	void enterConcur(PythonParser.ConcurContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#concur}.
	 * @param ctx the parse tree
	 */
	void exitConcur(PythonParser.ConcurContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#concurRe}.
	 * @param ctx the parse tree
	 */
	void enterConcurRe(PythonParser.ConcurReContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#concurRe}.
	 * @param ctx the parse tree
	 */
	void exitConcurRe(PythonParser.ConcurReContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterCond(PythonParser.CondContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitCond(PythonParser.CondContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#para}.
	 * @param ctx the parse tree
	 */
	void enterPara(PythonParser.ParaContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#para}.
	 * @param ctx the parse tree
	 */
	void exitPara(PythonParser.ParaContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#loop}.
	 * @param ctx the parse tree
	 */
	void enterLoop(PythonParser.LoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#loop}.
	 * @param ctx the parse tree
	 */
	void exitLoop(PythonParser.LoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#choice}.
	 * @param ctx the parse tree
	 */
	void enterChoice(PythonParser.ChoiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#choice}.
	 * @param ctx the parse tree
	 */
	void exitChoice(PythonParser.ChoiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#seqSeq}.
	 * @param ctx the parse tree
	 */
	void enterSeqSeq(PythonParser.SeqSeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#seqSeq}.
	 * @param ctx the parse tree
	 */
	void exitSeqSeq(PythonParser.SeqSeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#repeat}.
	 * @param ctx the parse tree
	 */
	void enterRepeat(PythonParser.RepeatContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#repeat}.
	 * @param ctx the parse tree
	 */
	void exitRepeat(PythonParser.RepeatContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#twoArguments}.
	 * @param ctx the parse tree
	 */
	void enterTwoArguments(PythonParser.TwoArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#twoArguments}.
	 * @param ctx the parse tree
	 */
	void exitTwoArguments(PythonParser.TwoArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#threeArguments}.
	 * @param ctx the parse tree
	 */
	void enterThreeArguments(PythonParser.ThreeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#threeArguments}.
	 * @param ctx the parse tree
	 */
	void exitThreeArguments(PythonParser.ThreeArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#fourArguments}.
	 * @param ctx the parse tree
	 */
	void enterFourArguments(PythonParser.FourArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#fourArguments}.
	 * @param ctx the parse tree
	 */
	void exitFourArguments(PythonParser.FourArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(PythonParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(PythonParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#arg_python}.
	 * @param ctx the parse tree
	 */
	void enterArg_python(PythonParser.Arg_pythonContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#arg_python}.
	 * @param ctx the parse tree
	 */
	void exitArg_python(PythonParser.Arg_pythonContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#special_String}.
	 * @param ctx the parse tree
	 */
	void enterSpecial_String(PythonParser.Special_StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#special_String}.
	 * @param ctx the parse tree
	 */
	void exitSpecial_String(PythonParser.Special_StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link PythonParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(PythonParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link PythonParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(PythonParser.StringContext ctx);
}