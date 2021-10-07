/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *  History:
 *	06-Oct-2021 (rlwhitcomb)
 *	    Initial coding.
 */
package info.rlwhitcomb.calc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;

import info.rlwhitcomb.util.Intl;
import static info.rlwhitcomb.calc.CalcUtil.getTreeText;


/**
 * A user-defined function definition, which includes the name, parameter list, and function body.
 */
class FunctionDeclaration
{
	/**
	 * Name of this function.
	 */
	private final String functionName;

	/**
	 * The function body definition.
	 */
	private final ParserRuleContext functionBody;

	/**
	 * The defined parameters, along with their default value expressions.
	 */
	private final LinkedHashMap<String, ParserRuleContext> parameters;

	/**
	 * For convenience, the ordered list of the parameter names (constructed as needed
	 * to access parameters by index.
	 */
	private String[] parameterNames;


	/**
	 * Constructor given the function name and its declaration (body).
	 *
	 * @param name Name of this function.
	 * @param body The complete parse tree of its declaration.
	 */
	FunctionDeclaration(final String name, final ParserRuleContext body) {
	    this.functionName   = name;
	    this.functionBody   = body;
	    this.parameters     = new LinkedHashMap<>();
	    this.parameterNames = null;
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
	    if (parameters.containsKey(name))
		throw new CalcExprException(ctx, "%calc#noDupLocalVar", name);

	    parameters.put(name, expr);

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
	 */
	public String getParameterName(final int index) {
	    if (parameterNames == null) {
		parameterNames = parameters.keySet().toArray(new String[0]);
	    }

	    if (index < 0 || index >= parameterNames.length)
		throw new Intl.IndexOutOfBoundsException("calc#indexOutOfBounds", index);

	    return parameterNames[index];
	}

	/**
	 * Get the named parameter's initial value expression (if any).
	 *
	 * @param name The parameter name (presumably gotten from {@link #getParameterName}).
	 * @return     The initial value expression declared in the script.
	 */
	public ParserRuleContext getParameterExpr(final String name) {
	    return parameters.get(name);
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
		for (Map.Entry<String, ParserRuleContext> entry : parameters.entrySet()) {
		    if (!first)
			buf.append(',');
		    buf.append(entry.getKey());
		    ParserRuleContext initialExpr = entry.getValue();
		    if (initialExpr != null)
			buf.append('=').append(getTreeText(initialExpr));
		    first = false;
		}
		buf.append(')');
	    }

	    return buf.toString();
	}

	/**
	 * Access the number of formal parameters.
	 *
	 * @return Number of formal parameters defined.
	 */
	public int getNumberOfParameters() {
	    return parameters.size();
	}

	/**
	 * Access the function body (parse tree).
	 *
	 * @return This function's body.
	 */
	public ParserRuleContext getFunctionBody() {
	    return functionBody;
	}

}

