// Generated from pl\edu\agh\kis\Pattern.g4 by ANTLR 4.12.0
package sl.fside.services.code_generator2;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PatternParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PatternVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PatternParser#pattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern(PatternParser.PatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link PatternParser#patternName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPatternName(PatternParser.PatternNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link PatternParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(PatternParser.MethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link PatternParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(PatternParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link PatternParser#typedExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypedExpr(PatternParser.TypedExprContext ctx);
}