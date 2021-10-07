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
 *	07-Oct-2021 (rlwhitcomb)
 *	    Add context parameter to "evaluateFunction".
 */
package info.rlwhitcomb.calc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;

import info.rlwhitcomb.util.Intl;
import static info.rlwhitcomb.calc.CalcUtil.getTreeText;


/**
 * A user-defined function scope, which includes the name, parameter list, function body,
 * as well as the local symbol table.
 */
class FunctionScope extends NestedScope
{
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
	    super(Type.FUNCTION);
	    this.declaration = decl;
	}

	/**
	 * Set the value of the given parameter number to the given expression.
	 *
	 * @param index 0-based parameter index.
	 * @param expr  The expression value (could be {@code null}) to be assigned.
	 */
	public void setParameterValue(final CalcObjectVisitor visitor, final int index, final ParserRuleContext expr) {
	    ParserRuleContext valueExpr = expr;
	    String paramName = declaration.getParameterName(index);

	    if (valueExpr == null) {
		valueExpr = declaration.getParameterExpr(paramName);
	    }
	    if (valueExpr == null)
		setValue(paramName, false, null);
	    else
		setValue(paramName, false, visitor.evaluateFunction(valueExpr, visitor.visit(valueExpr)));
	}

	/**
	 * Access the function's declaration.
	 *
	 * @return The complete function declaration set at construction time.
	 */
	public FunctionDeclaration getDeclaration() {
	    return declaration;
	}

}

