// Generated from java-escape by ANTLR 4.11.1
package org.example.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PythonParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PythonVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PythonParser#prule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrule(PythonParser.PruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#creators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreators(PythonParser.CreatorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#seq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeq(PythonParser.SeqContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#branch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBranch(PythonParser.BranchContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#branchRe}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBranchRe(PythonParser.BranchReContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#concur}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcur(PythonParser.ConcurContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#concurRe}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcurRe(PythonParser.ConcurReContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCond(PythonParser.CondContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#para}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPara(PythonParser.ParaContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop(PythonParser.LoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#choice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChoice(PythonParser.ChoiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#seqSeq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeqSeq(PythonParser.SeqSeqContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#repeat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeat(PythonParser.RepeatContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(PythonParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#arg_python}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg_python(PythonParser.Arg_pythonContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#special_String}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecial_String(PythonParser.Special_StringContext ctx);
	/**
	 * Visit a parse tree produced by {@link PythonParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(PythonParser.StringContext ctx);
}