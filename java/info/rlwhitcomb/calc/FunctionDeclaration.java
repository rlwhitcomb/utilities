/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2025 Roger L. Whitcomb.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *      Data structures for Calc to hold user-defined function definitions,
 *      local symbol tables, etc.
 *
 * History:
 *  06-Oct-21 rlw ----	Initial coding.
 *  30-Oct-21 rlw ----	Implement "toString()".
 *  07-Nov-21 rlw #69	Implement variable number of parameters.
 *  09-Nov-21 rlw #74	Add "hasVarargs" method.
 *  13-Feb-22 rlw #199	Rename local parameter values to "_" prefix.
 *  14-Feb-22 rlw #199	Get the "_" prefix from FunctionScope.
 *  28-Apr-22 rlw #68	Tweak index out of bounds error.
 *  27-May-22 rlw ----	Move "setupFunctionCall" into here from CalcObjectVisitor.
 *  28-May-22 rlw #355	Define "_funcname" constant for functions.
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  01-Sep-22 rlw	Fix typo in error message key.
 *  10-Nov-22 rlw #554	Push and pop our scope during parameter evaluation so any
 *			default expressions can reference previous parameter values.
 *  11-Nov-22 rlw #554	Make "setupFunctionCall" THE place to push the scope, while
 *			"evaluate" is THE place to pop it.
 *  09-Dec-23 rlw #635	Additional spacing in the formal parameter list text.
 *  04-Feb-24 rlw #645	Non-constant parameter declarations.
 *  22-Mar-24 rlw #664	Support for named parameters.
 *  07-Apr-24 rlw #664	Fix bug with positional parameters before named ones.
 *  01-Jun-25 rlw #724	Call "finalizeParameters" at the end of setting up parameters.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Intl;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static info.rlwhitcomb.calc.CalcUtil.getTreeText;


/**
 * A user-defined function definition, which includes the name, parameter list, and function body.
 */
class FunctionDeclaration
{
	/**
	 * One parameter declaration, with name, initial value expression, and constant flag.
	 */
	private class ParameterDeclaration
	{
		String name;
		ParserRuleContext initialValueExpr;
		boolean constant;

		ParameterDeclaration(final String nm, final ParserRuleContext valExpr, final boolean con) {
		    name = nm;
		    initialValueExpr = valExpr;
		    constant = con;
		}
	}


	/**
	 * Name of predefined (local) constant that has the function name as the value.
	 */
	public static final String FUNCNAME = "_funcname";

	/**
	 * Name of this function.
	 */
	private final String functionName;

	/**
	 * The function body definition.
	 */
	private final ParserRuleContext functionBody;

	/**
	 * The defined parameters, along with their default value expressions, and constant flags.
	 */
	private final LinkedHashMap<String, ParameterDeclaration> parameters;

	/**
	 * For convenience, the ordered list of the parameter names (constructed as needed
	 * to access parameters by index).
	 */
	private String[] parameterNames;

	/**
	 * Does this function declare a variable number of arguments?
	 */
	private boolean hasVarargs;

	/**
	 * Placeholder parameter name for the variable parameter list.
	 */
	public static final String VARARG = "...";


	/**
	 * Constructor given the function name and its declaration (body).
	 *
	 * @param name Name of this function.
	 * @param body The complete parse tree of its declaration.
	 */
	FunctionDeclaration(final String name, final ParserRuleContext body) {
	    functionName   = name;
	    functionBody   = body;
	    parameters     = new LinkedHashMap<>();
	    parameterNames = null;
	    hasVarargs     = false;
	}

	/**
	 * Define a parameter / local variable with a possible initializer expression.
	 * <p> Note: must be called for each parameter in order of declaration.
	 *
	 * @param ctx	The parameter declaration context.
	 * @param name	The parameter / local variable name.
	 * @param expr	The (possibly {@code null}) initializer expression.
	 */
	public void defineParameter(final ParserRuleContext ctx, final String name, final ParserRuleContext expr) {
	    defineParameter(ctx, name, expr, true);
	}

	/**
	 * Define a parameter / local variable with a possible initializer expression.
	 * <p> Note: must be called for each parameter in order of declaration.
	 *
	 * @param ctx	The parameter declaration context.
	 * @param name	The parameter / local variable name.
	 * @param expr	The (possibly {@code null}) initializer expression.
	 * @param con	Flag to say whether the parameter can be modified.
	 */
	public void defineParameter(final ParserRuleContext ctx, final String name, final ParserRuleContext expr, final boolean con) {
	    if (hasVarargs)
		throw new CalcExprException(ctx, "%calc#varargsLast");

	    if (parameters.containsKey(name))
		throw new CalcExprException(ctx, "%calc#noDupLocalVar", name);

	    parameters.put(name, new ParameterDeclaration(name, expr, con));
	    if (name.equals(VARARG))
		hasVarargs = true;

	    /* Invalidate the parameter names array (if it exists) since we just added a new one */
	    parameterNames = null;
	}

	/**
	 * Access the name of the n-th parameter to this function (uses the dynamic
	 * {@link #parameterNames} array to do so).
	 *
	 * @param index Index into the parameter list to access.
	 * @return      The declared name of that parameter.
	 * @throws      IndexOutOfBoundsException if the index is negative or
	 *              greater than the number of parameters.
	 * @see FunctionScope#FUNC_PREFIX
	 */
	public String getParameterName(final int index) {
	    if (parameterNames == null) {
		parameterNames = parameters.keySet().toArray(new String[0]);
	    }

	    if (index < 0 || (!hasVarargs && index >= parameterNames.length))
		throw new Intl.IndexOutOfBoundsException("calc#indexOutOfBounds", index, parameterNames.length - 1);

	    if ((hasVarargs && index < parameterNames.length - 1) || (!hasVarargs && index < parameterNames.length))
		return parameterNames[index];

	    return String.format("%1$s%2$d", FunctionScope.FUNC_PREFIX, index);
	}

	/**
	 * Get the named parameter's initial value expression (if any).
	 *
	 * @param name The parameter name (presumably gotten from {@link #getParameterName}).
	 * @return     The initial value expression declared in the script.
	 */
	public ParserRuleContext getParameterExpr(final String name) {
	    ParameterDeclaration decl = parameters.get(name);
	    return decl == null ? null : decl.initialValueExpr;
	}

	/**
	 * Get the named parameter's "constant" flag (default is constant, which also can be indicated
	 * using the "const" declaration; the exception is to use "var", indicating a modifiable parameter).
	 *
	 * @param name The parameter name (presumably gotten from {@link #getParameterName}).
	 * @return     The constant flag value ({@code true} meaning the parameter cannot be modified).
	 */
	public boolean getConstantFlag(final String name) {
	    ParameterDeclaration decl = parameters.get(name);
	    return decl == null ? true : decl.constant;
	}

	/**
	 * Access the function's name.
	 *
	 * @return The function name as declared.
	 */
	public String getFunctionName() {
	    return functionName;
	}

	/**
	 * Access the function's "full" name, which is the name and parameter list.
	 *
	 * @return The string representation of the function's header declaration.
	 */
	public String getFullFunctionName() {
	    StringBuilder buf = new StringBuilder(functionName);

	    if (!parameters.isEmpty()) {
		buf.append('(');
		boolean first = true;
		for (Map.Entry<String, ParameterDeclaration> entry : parameters.entrySet()) {
		    if (!first)
			buf.append(", ");
		    ParameterDeclaration paramDecl = entry.getValue();
		    if (!paramDecl.constant)
			buf.append("var ");
		    buf.append(entry.getKey());
		    ParserRuleContext initialExpr = paramDecl.initialValueExpr;
		    if (initialExpr != null)
			buf.append(" = ").append(getTreeText(initialExpr));
		    first = false;
		}
		buf.append(')');
	    }

	    return buf.toString();
	}

	@Override
	public String toString() {
	    return String.format("define %1$s = %2$s", getFullFunctionName(), getTreeText(functionBody));
	}

	/**
	 * Access whether this function accepts a variable number of arguments.
	 *
	 * @return The varargs value for this function.
	 */
	public boolean hasVarargs() {
	    return hasVarargs;
	}

	/**
	 * Access the number of formal parameters: negative number of formals declared along with
	 * a varargs indicator, or a positive, fixed number.
	 * <p> Thus: <code>def f(a, ...)</code> will give <code>-2</code>, while
	 * <code>def f(a, b, c)</code> will give <code>3</code>.
	 *
	 * @return Number of formal parameters defined.
	 */
	public int getNumberOfParameters() {
	    return hasVarargs ? -parameters.size() : parameters.size();
	}

	/**
	 * Access the function body (parse tree).
	 *
	 * @return This function's body.
	 */
	public ParserRuleContext getFunctionBody() {
	    return functionBody;
	}

	/**
	 * Setup a {@link FunctionScope} given this declaration and the list of actual
	 * parameter values.
	 *
	 * @param ctx     The function call context (parse tree).
	 * @param visitor The visitor needed to calculate expressions.
	 * @param params  The actual parameter value list.
	 * @return        Function scope with the actual values set in it.
	 */
	public FunctionScope setupFunctionCall(final ParserRuleContext ctx, final CalcObjectVisitor visitor, final List<CalcParser.OptParamContext> params) {
	    FunctionScope funcScope = new FunctionScope(this);
	    funcScope.setName(functionName);

	    int numParams = getNumberOfParameters();
	    int numActuals = params != null ? params.size() : 0;

	    // First, make this function the current symbol table scope so we can define the parameter values in it, and so they
	    // can refer to previous parameters if desired. This scope won't be popped until the function call is over
	    // (in CalcObjectVisitor.evaluate).
	    visitor.pushScope(funcScope);

	    // And so firstly, set the "_funcname" value to the function's name, so it can be used by param default expressions too
	    ConstantValue.define(funcScope, FUNCNAME, functionName);

	    if (params != null) {
		// Special case: 0 or variable # params, but one actual, except the actual expr is zero -> zero actuals
		if (numParams <= 0 && numActuals == 1 && params.get(0).expr() == null)
		    numActuals--;

		if (numParams >= 0 && numActuals > numParams) {
		    if (numParams == 1)
			throw new CalcExprException(ctx, "%calc#tooManyForOneValue", numActuals);
		    else
			throw new CalcExprException(ctx, "%calc#tooManyForValues", numActuals, numParams);
		}

		// Before anything, make a map of the named actuals for quick retrieval as we encounter
		// the formal names to be matched, as well as a list of the positional parameters in the
		// the correct order.
		Map<String, CalcParser.ExprContext> namedActuals = new HashMap<>(numActuals);
		List<CalcParser.ExprContext> positionActuals = new ArrayList<>(numActuals);

		for (int index = 0; index < numActuals; index++) {
		    CalcParser.OptParamContext param = params.get(index);
		    CalcParser.IdContext id = param.id();
		    CalcParser.ExprContext expr = param.expr();

		    if (id != null)
			namedActuals.put(id.getText(), expr);
		    else
			positionActuals.add(expr);
		}

		// We MUST deal with the actual parameters in the order of the formal declaration
		// so that the _* array is in the correct order. So, iterate through the formals,
		// looking first for values with that name, and failing that, set in the positional order.
		int paramPos = 0;
		int actualPos = 0;

		for (Map.Entry<String, ParameterDeclaration> entry : parameters.entrySet()) {
		    String paramName = entry.getKey();
		    ParameterDeclaration decl = entry.getValue();

		    // If we have a named actual, use that expression to set the value
		    CalcParser.ExprContext namedExpr = namedActuals.get(paramName);
		    if (namedExpr != null) {
			funcScope.setParameterValue(visitor, paramName, namedExpr);
			namedActuals.remove(paramName);
		    }
		    // Then if there is an actual for this parameter position, use it
		    else if (actualPos < positionActuals.size()) {
			CalcParser.ExprContext actualExpr = positionActuals.get(actualPos++);

			if (paramName.equals(VARARG))
			    funcScope.setParameterValue(visitor, paramPos, actualExpr);
			else
			    funcScope.setParameterValue(visitor, paramName, actualExpr);
		    }
		    // Finally, set the parameter value to null because there are no more actuals
		    else if (!paramName.equals(VARARG)) {
			funcScope.setParameterValue(visitor, paramPos, null);
		    }
		    paramPos++;
		}

		// If there are still named parameters in the map, that means there were unknown parameters
		// listed, so this is an error.
		if (!namedActuals.isEmpty()) {
		    String names = CharUtil.makeSimpleStringList(namedActuals.keySet());
		    throw new CalcExprException(ctx, "%calc#noNamedParams", names);
		}

		// Now, if there are more positional arguments remaining after the formal list,
		// set the value into the parameter array
		for (int index = actualPos; index < positionActuals.size(); index++) {
		    funcScope.setParameterValue(visitor, paramPos++, positionActuals.get(index));
		}

		funcScope.finalizeParameters();
	    }

	    return funcScope;
	}
}

