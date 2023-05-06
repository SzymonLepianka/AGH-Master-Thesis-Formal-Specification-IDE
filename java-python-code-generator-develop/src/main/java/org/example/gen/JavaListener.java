// Generated from C:/Users/kacpe/Desktop/java-python-code-generator-release3/src/main/java/org/example/grammar\Java.g4 by ANTLR 4.12.0
package org.example.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JavaParser}.
 */
public interface JavaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JavaParser#prule}.
	 * @param ctx the parse tree
	 */
	void enterPrule(JavaParser.PruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#prule}.
	 * @param ctx the parse tree
	 */
	void exitPrule(JavaParser.PruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#creators}.
	 * @param ctx the parse tree
	 */
	void enterCreators(JavaParser.CreatorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#creators}.
	 * @param ctx the parse tree
	 */
	void exitCreators(JavaParser.CreatorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#seq}.
	 * @param ctx the parse tree
	 */
	void enterSeq(JavaParser.SeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#seq}.
	 * @param ctx the parse tree
	 */
	void exitSeq(JavaParser.SeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#seqoptions}.
	 * @param ctx the parse tree
	 */
	void enterSeqoptions(JavaParser.SeqoptionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#seqoptions}.
	 * @param ctx the parse tree
	 */
	void exitSeqoptions(JavaParser.SeqoptionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#seqPrime}.
	 * @param ctx the parse tree
	 */
	void enterSeqPrime(JavaParser.SeqPrimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#seqPrime}.
	 * @param ctx the parse tree
	 */
	void exitSeqPrime(JavaParser.SeqPrimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#seqBranch}.
	 * @param ctx the parse tree
	 */
	void enterSeqBranch(JavaParser.SeqBranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#seqBranch}.
	 * @param ctx the parse tree
	 */
	void exitSeqBranch(JavaParser.SeqBranchContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#seqConcur}.
	 * @param ctx the parse tree
	 */
	void enterSeqConcur(JavaParser.SeqConcurContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#seqConcur}.
	 * @param ctx the parse tree
	 */
	void exitSeqConcur(JavaParser.SeqConcurContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#branch}.
	 * @param ctx the parse tree
	 */
	void enterBranch(JavaParser.BranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#branch}.
	 * @param ctx the parse tree
	 */
	void exitBranch(JavaParser.BranchContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#branchRe}.
	 * @param ctx the parse tree
	 */
	void enterBranchRe(JavaParser.BranchReContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#branchRe}.
	 * @param ctx the parse tree
	 */
	void exitBranchRe(JavaParser.BranchReContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#concur}.
	 * @param ctx the parse tree
	 */
	void enterConcur(JavaParser.ConcurContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#concur}.
	 * @param ctx the parse tree
	 */
	void exitConcur(JavaParser.ConcurContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#concurRe}.
	 * @param ctx the parse tree
	 */
	void enterConcurRe(JavaParser.ConcurReContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#concurRe}.
	 * @param ctx the parse tree
	 */
	void exitConcurRe(JavaParser.ConcurReContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterCond(JavaParser.CondContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitCond(JavaParser.CondContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#para}.
	 * @param ctx the parse tree
	 */
	void enterPara(JavaParser.ParaContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#para}.
	 * @param ctx the parse tree
	 */
	void exitPara(JavaParser.ParaContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#loop}.
	 * @param ctx the parse tree
	 */
	void enterLoop(JavaParser.LoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#loop}.
	 * @param ctx the parse tree
	 */
	void exitLoop(JavaParser.LoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#choice}.
	 * @param ctx the parse tree
	 */
	void enterChoice(JavaParser.ChoiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#choice}.
	 * @param ctx the parse tree
	 */
	void exitChoice(JavaParser.ChoiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#seqSeq}.
	 * @param ctx the parse tree
	 */
	void enterSeqSeq(JavaParser.SeqSeqContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#seqSeq}.
	 * @param ctx the parse tree
	 */
	void exitSeqSeq(JavaParser.SeqSeqContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#repeat}.
	 * @param ctx the parse tree
	 */
	void enterRepeat(JavaParser.RepeatContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#repeat}.
	 * @param ctx the parse tree
	 */
	void exitRepeat(JavaParser.RepeatContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#alt}.
	 * @param ctx the parse tree
	 */
	void enterAlt(JavaParser.AltContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#alt}.
	 * @param ctx the parse tree
	 */
	void exitAlt(JavaParser.AltContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#twoArguments}.
	 * @param ctx the parse tree
	 */
	void enterTwoArguments(JavaParser.TwoArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#twoArguments}.
	 * @param ctx the parse tree
	 */
	void exitTwoArguments(JavaParser.TwoArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#threeArguments}.
	 * @param ctx the parse tree
	 */
	void enterThreeArguments(JavaParser.ThreeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#threeArguments}.
	 * @param ctx the parse tree
	 */
	void exitThreeArguments(JavaParser.ThreeArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#fourArguments}.
	 * @param ctx the parse tree
	 */
	void enterFourArguments(JavaParser.FourArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#fourArguments}.
	 * @param ctx the parse tree
	 */
	void exitFourArguments(JavaParser.FourArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(JavaParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(JavaParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#arg_java}.
	 * @param ctx the parse tree
	 */
	void enterArg_java(JavaParser.Arg_javaContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#arg_java}.
	 * @param ctx the parse tree
	 */
	void exitArg_java(JavaParser.Arg_javaContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#special_String}.
	 * @param ctx the parse tree
	 */
	void enterSpecial_String(JavaParser.Special_StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#special_String}.
	 * @param ctx the parse tree
	 */
	void exitSpecial_String(JavaParser.Special_StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(JavaParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(JavaParser.StringContext ctx);
}