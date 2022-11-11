/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Roger L. Whitcomb.
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
 *	07-Oct-2021 (rlwhitcomb)
 *	    Add context parameter to "evaluateFunction".
 *	07-Nov-2021 (rlwhitcomb)
 *	    #69: Maintain "$*" and "$#" variables for function parameters.
 *	13-Feb-2022 (rlwhitcomb)
 *	    #199: Derive from ParameterizedScope; move common code to there.
 *	14-Feb-2022 (rlwhitcomb)
 *	    #199: Override "toString" here from default in Scope.
 *	15-Feb-2022 (rlwhitcomb)
 *	    #169: Set flag not to call zero-arg functions without parens
 *	    during "setParameterValue".
 *	11-May-2022 (rlwhitcomb)
 *	    #318: Rename "evaluateFunction" to just "evaluate".
 *	25-May-2022 (rlwhitcomb)
 *	    #348: Make all methods package private.
 *	27-May-2022 (rlwhitcomb)
 *	    Change "getDeclaration" into "getFunctionBody".
 *	04-Jun-2022 (rlwhitcomb)
 *	    #361: Add "getFunctionName".
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	08-Sep-2022 (rlwhitcomb)
 *	    #475: Add calls for getting function stack, and full function name.
 *	11-Nov-2022 (rlwhitcomb)
 *	    #554: Spiff up the "toString" value with quoted full function name.
 */
package info.rlwhitcomb.calc;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;


/**
 * A user-defined function scope, which includes the name, parameter list, function body,
 * as well as the local symbol table.
 */
class FunctionScope extends ParameterizedScope
{
	/**
	 * The prefix used for the parameter array values.
	 */
	static final String FUNC_PREFIX = "_";


	/**
	 * Reference to the function declaration, which has the name, parameters, etc.
	 */
	private final FunctionDeclaration declaration;


	/**
	 * Constructor given the function name and its declaration.
	 *
	 * @param decl The complete function declaration.
	 */
	FunctionScope(final FunctionDeclaration decl) {
	    super(Type.FUNCTION, FUNC_PREFIX);

	    declaration = decl;
	}


	/**
	 * Set the value of the given parameter number to the given expression.
	 *
	 * @param visitor The visitor class used to evaluate expressions.
	 * @param index   0-based parameter index.
	 * @param expr    The expression value (could be {@code null}) to be assigned.
	 */
	void setParameterValue(final CalcObjectVisitor visitor, final int index, final ParserRuleContext expr) {
	    ParserRuleContext valueExpr = expr;
	    String paramName = declaration.getParameterName(index);
	    Object paramValue = null;

	    if (valueExpr == null) {
		valueExpr = declaration.getParameterExpr(paramName);
	    }
	    if (valueExpr != null) {
		paramValue = visitor.evaluateParameter(valueExpr);
	    }
	    ParameterValue.define(this, paramName, paramValue);
	}

	/**
	 * Access the function's name.
	 *
	 * @return The simple function name (no parameters).
	 */
	String getFunctionName() {
	    return declaration.getFunctionName();
	}

	/**
	 * Access the full function name (name + parameters).
	 *
	 * @return The function's full name.
	 */
	String getFullFunctionName() {
	    return declaration.getFullFunctionName();
	}

	/**
	 * Access the function declaration's function body.
	 *
	 * @return The complete function body, set at construction time.
	 */
	ParserRuleContext getFunctionBody() {
	    return declaration.getFunctionBody();
	}

	/**
	 * @return An appropriate value for a function.
	 */
	@Override
	public String toString() {
	    return String.format("%1$s \"%2$s\" scope", toBookCase(), declaration.getFullFunctionName());
	}

	/**
	 * Return a list of the current function stack, starting from the top.
	 *
	 * @param currentScope The top of the current (nested) scope list.
	 * @return             The complete stack of functions, down to the global scope.
	 */
	public static List<FunctionScope> getCallers(final NestedScope currentScope) {
	    List<FunctionScope> callers = new ArrayList<>();

	    for (NestedScope scope = currentScope; scope != null; scope = scope.getEnclosingScope()) {
		if (scope instanceof FunctionScope) {
		    callers.add((FunctionScope) scope);
		}
	    }

	    return callers;
	}

}

