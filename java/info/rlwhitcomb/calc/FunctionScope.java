/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022,2024 Roger L. Whitcomb.
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
 *  07-Oct-21 rlw ----	Add context parameter to "evaluateFunction".
 *  07-Nov-21 rlw #69	Maintain "$*" and "$#" variables for function parameters.
 *  13-Feb-22 rlw #199	Derive from ParameterizedScope; move common code to there.
 *  14-Feb-22 rlw #199	Override "toString" here from default in Scope.
 *  15-Feb-22 rlw #169	Set flag not to call zero-arg functions without parens
 *			during "setParameterValue".
 *  11-May-22 rlw #318	Rename "evaluateFunction" to just "evaluate".
 *  25-May-22 rlw #348	Make all methods package private.
 *  27-May-22 rlw ----	Change "getDeclaration" into "getFunctionBody".
 *  04-Jun-22 rlw #361	Add "getFunctionName".
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  08-Sep-22 rlw #475	Add calls for getting function stack, and full function name.
 *  11-Nov-22 rlw #554	Spiff up the "toString" value with quoted full function name.
 *  22-Mar-24 rlw #645	Start of non-constant parameter processing.
 *		  #664	Set parameter values by either name or index.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.ClassUtil;

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
	    String paramName = declaration.getParameterName(index);
	    setParameterValue(visitor, paramName, expr);
	}

	/**
	 * Set the value of the given parameter to the given expression.
	 *
	 * @param visitor   The visitor class used to evaluate expressions.
	 * @param paramName Parameter name.
	 * @param expr      The expression value (could be {@code null}) to be assigned.
	 */
	void setParameterValue(final CalcObjectVisitor visitor, final String paramName, final ParserRuleContext expr) {
	    ParserRuleContext valueExpr = expr;
	    Object paramValue = null;
// TODO: if the parameter is marked "var" (mutable) then we need the lvalue instead of the regular value
	    boolean constantFlag = declaration.getConstantFlag(paramName);
	    if (valueExpr == null) {
		valueExpr = declaration.getParameterExpr(paramName);
	    }
	    if (valueExpr != null) {
		if (constantFlag) {
		    paramValue = visitor.evaluateParameter(valueExpr);
		}
		else {
		    paramValue = visitor.evaluateParameter(valueExpr); // temp until we figure this out
//System.out.println("func scope: 'var' param: value = " + ClassUtil.fullToString(valueExpr) + ", param value = " + ClassUtil.fullToString(paramValue));
		}
	    }
	    ParameterValue.define(this, paramName, paramValue, constantFlag);
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

